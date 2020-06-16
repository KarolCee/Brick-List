package com.example.projekt2

import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.inventory_row.view.*
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.File
import java.lang.Exception
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult


class InventoryAdapter(private val returnedList: MutableList<Inventory>,val clicked : (Inventory) -> Unit) :
    RecyclerView.Adapter<InventoryAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.inventory_row,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount() = returnedList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dbHandler = MyDbHandler(holder.view.context, null, null, 1)
        holder.view.mytextview.text = returnedList[position].name
        var item = returnedList[position]

        if(item.active == 0){
            holder.view.btnArchive.text = "UNARCHIVE"
            holder.view.cardviewinventory.setBackgroundColor(android.graphics.Color.YELLOW)
        }

        holder.view.mytextview.setOnClickListener(){
            dbHandler.set_lastaccessed(item.id)
            clicked(item)

        }



        holder.view.btnDel.setOnClickListener {
            dbHandler.delete_inventory(item.id)
            holder.view.cardviewinventory.setCardBackgroundColor(android.graphics.Color.RED)
            holder.view.mytextview.text = "DELETED"
            holder.view.btnDel.text = ""
            holder.view.btnArchive.text = ""
            holder.view.btnArchive.isEnabled = false
            holder.view.btnDel.isEnabled = false


        }

        holder.view.btnArchive.setOnClickListener {
            if (holder.view.btnArchive.text == "ARCHIVE") {
                dbHandler.archive_inventory(item.id)
                holder.view.cardviewinventory.setBackgroundColor(android.graphics.Color.YELLOW)
                holder.view.btnArchive.text = "UNARCHIVE"
                try{
                    write_xml(it,dbHandler.fetch_all_inventory_parts(item.id),item.name.toString())
                }catch(e:Exception){ }


            } else {
                dbHandler.unarchive_inventory(item.id)
                holder.view.btnArchive.text = "ARCHIVE"
                holder.view.cardviewinventory.setBackgroundColor(android.graphics.Color.WHITE)
            }

        }

    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)


}

fun write_xml(view:View, parts: MutableList<InventoryPart>,itemname:String) {
    val docBuilder: DocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
    val doc: Document = docBuilder.newDocument()

//    var rootElement: Element = doc.createElement("INVENTORY")
//    var ITEMID: Element = doc.createElement("ITEMID")
//    var ITEMTYPE: Element = doc.createElement("ITEMTYPE")
//    var COLOR: Element = doc.createElement("COLOR")
//    var QTYFILLED: Element = doc.createElement("QTYFILLED")
//    var ITEM: Element = doc.createElement("ITEM")


    var rootElement = doc.createElement("INVENTORY")

    for(p in parts){


        var ITEMID= doc.createElement("ITEMID")
        var ITEMTYPE = doc.createElement("ITEMTYPE")
        var COLOR = doc.createElement("COLOR")
        var QTYFILLED = doc.createElement("QTYFILLED")
        var ITEM = doc.createElement("ITEM")

        ITEMID.appendChild(doc.createTextNode(p.itemid.toString()))
        ITEMTYPE.appendChild(doc.createTextNode(p.typeid.toString()))
        COLOR.appendChild(doc.createTextNode(p.colorid.toString()))
        QTYFILLED.appendChild(doc.createTextNode((p.quantityinset - p.quantityinstore).toString()))

        ITEM.appendChild(ITEMTYPE)
        ITEM.appendChild(ITEMID)
        ITEM.appendChild(COLOR)
        ITEM.appendChild(QTYFILLED)

        rootElement.appendChild(ITEM)

    }



    doc.appendChild(rootElement)


    val transformer: Transformer = TransformerFactory.newInstance().newTransformer()
    transformer.setOutputProperty(OutputKeys.INDENT, "yes")


    val rut = Environment.getExternalStorageDirectory()

    val dir_name = "/Output"
    val file_name = "${itemname}.xml"

    val outDir = File(rut.absolutePath, dir_name)
    outDir.mkdir()

    val file = File(outDir, file_name)


    transformer.transform(DOMSource(doc), StreamResult(file))
    Toast.makeText(
        view.context,
        "Wanted list saved in ${rut.absolutePath.toString() + dir_name + "/" + file_name}",
        Toast.LENGTH_SHORT
    ).show()

}



