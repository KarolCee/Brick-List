package com.example.projekt2

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_newproject.*
import org.xml.sax.InputSource
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.net.URL

class MyXMLParser : AppCompatActivity() {
    var new_inventory_id: Int = 1
    val dbHandler = MyDbHandler(this, null, null, 1)
    var set_id: String = "615"
    var url_settings = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_newproject)

        btnAddProject.setOnClickListener {

            //Create new a inventory instance in db
            var new_inventory: Inventory = Inventory(0, editTextName.text.toString(), 1, 0)
            dbHandler.add_new_inventory(new_inventory)
            set_id = editTextCode.text.toString()
            new_inventory_id = dbHandler.fetch_new_inventory_id(editTextName.text.toString())
            dbHandler.increment_lastaccessed(new_inventory_id)

            //get the url prefix
            var legosettings = load()
            url_settings = legosettings[0]

            //Create InventoriesParts intances in db
            BgTask().execute()


        }
    }

    //READ PREFERENCES FROM FILE
    fun load(): Array<String> {
        var archived = "HIDE"
        var urlprefix = "http://fcds.cs.put.poznan.pl/MyWeb/BL/"
        try {
            val filename = "legosettings.txt"
            val file = InputStreamReader(openFileInput(filename))
            val br = BufferedReader(file)
            urlprefix = br.readLine().toString()
            archived = br.readLine().toString()
            file.close()
        } catch (e: Exception) {

        }
        return arrayOf(urlprefix, archived)
    }


    private inner class BgTask : AsyncTask<String, Int, ArrayList<Item>?>() {
        override fun doInBackground(vararg params: String?): ArrayList<Item>? {


            val pullParserFactory: XmlPullParserFactory

            var items: ArrayList<Item>? = null

                pullParserFactory = XmlPullParserFactory.newInstance()
                val parser = pullParserFactory.newPullParser()

                //val inputStream = applicationContext.assets.open("615.xml")

try {
    var url: URL = URL(url_settings + set_id + ".xml")


    parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
    parser.setInput(url.openStream(), null)

    items = parseXml(parser)


    var Code_object: Code? = null
    var real_color_id: Int = 0
    var real_part_id: Int = 0
    var instream_image: InputStream? = null
    for (item in items!!) {
        real_color_id = dbHandler.fetch_color_id(item.color)
        real_part_id = dbHandler.fetch_part_itemid(item.itemid)
        Code_object = dbHandler.fetch_code(real_part_id, real_color_id)


        if (Code_object == null) {
            if (Integer.parseInt(item.color) != 0) {
                try {
                    var url_image: URL =
                        URL("http://img.bricklink.com/P/${item.color}/${item.itemid}.gif")
                    instream_image = url_image.openStream()
                    var biciki: ByteArray = instream_image.readBytes()

                    dbHandler.insertImage(biciki, real_part_id, real_color_id)

                } catch (e: Exception) {

                } finally {
                    if (instream_image != null) {
                        instream_image.close()
                    }
                }
            } else {
                try {
                    var url_image: URL =
                        URL("https://www.bricklink.com/PL/${item.itemid}.jpg")
                    instream_image = url_image.openStream()
                    var biciki: ByteArray = instream_image.readBytes()
                    instream_image.close()
                    dbHandler.insertImage(biciki, real_part_id, real_color_id)
                } catch (e: Exception) {

                } finally {
                    if (instream_image != null) {
                        instream_image.close()
                    }
                }
            }
        } else {
            if (Code_object.code != null && Code_object.image == null) {
                try {
                    var url_image: URL =
                        URL("https://www.lego.com/service/bricks/5/2/${Code_object.code}")
                    instream_image = url_image.openStream()
                    var biciki: ByteArray = instream_image.readBytes()
                    instream_image.close()
                    dbHandler.save_img(real_part_id, real_color_id, biciki)
                } catch (e: Exception) {
                } finally {
                    if (instream_image != null) {
                        instream_image.close()
                    }
                }
            }
        }
    }

}catch(e:Exception){}
            return items
        }

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun onPostExecute(result: ArrayList<Item>?) {
            super.onPostExecute(result)

            try {
                for (item in result!!) {
                    //info from XML file
                    var id: Int = 0
                    var inventoryid: Int = new_inventory_id
                    var typeid: Int = 1
                    var itemid: Int = dbHandler.fetch_part_itemid(item.itemid)
                    var quantityinset: Int = Integer.parseInt(item.qty)
                    var quantityinstore: Int = 0 //by default we dont have any
                    var colorid: Int = dbHandler.fetch_color_id(item.color)
                    var extra: Int = 0
                    //add new inventory part corresponding to given inventory
                    dbHandler.add_new_inventory_parts(
                        InventoryPart(
                            id,
                            inventoryid,
                            typeid,
                            itemid,
                            quantityinset,
                            quantityinstore,
                            colorid,
                            extra
                        )
                    )


                }
                Toast.makeText(this@MyXMLParser, "New project created", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@MyXMLParser, "Wrong set code given", Toast.LENGTH_SHORT).show()
            }

            startActivity(Intent(this@MyXMLParser, MainActivity::class.java))


        }


    }


    @Throws(XmlPullParserException::class, IOException::class)
    fun parseXml(parser: XmlPullParser): ArrayList<Item>? {
        var items: ArrayList<Item>? = null
        var eventType = parser.eventType
        var item: Item? = null

        while (eventType != XmlPullParser.END_DOCUMENT) {
            val name: String
            when (eventType) {
                XmlPullParser.START_DOCUMENT -> items = ArrayList()
                XmlPullParser.START_TAG -> {
                    name = parser.name
                    if (name == "ITEM") {
                        item = Item()
                    } else if (item != null) {
                        if (name == "ITEMTYPE")
                            item.itemtype = parser.nextText()
                        else if (name == "ITEMID")
                            item.itemid = parser.nextText()
                        else if (name == "QTY")
                            item.qty = parser.nextText()
                        else if (name == "COLOR")
                            item.color = parser.nextText()
                        else if (name == "EXTRA")
                            item.extra = parser.nextText()
                        else if (name == "ALTERNATE")
                            item.alternate = parser.nextText()
                        else if (name == "MATCHID")
                            item.matchid = parser.nextText()
                        else if (name == "COUNTERPART")
                            item.counterpart = parser.nextText()
                    }

                }
                XmlPullParser.END_TAG -> {
                    name = parser.name
                    if (name.equals("ITEM", ignoreCase = true) && item != null) {
                        items!!.add(item)
                    }
                }
            }
            eventType = parser.next()

        }
        return items
    }


}