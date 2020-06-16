package com.example.projekt2

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            0
        )



        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Copying db from assets
        val helperDb = DBHelper(this)
        helperDb.openDatabase()

        //Fetching all of our projects - inventories from database
        var legosettings = load()
        var inventories_to_display: MutableList<Inventory>
        val dbHandler = MyDbHandler(this, null, null, 1)
        if (legosettings[1] == "SHOW") {
            inventories_to_display = dbHandler.fetch_all_inventories()
        } else {
            inventories_to_display = dbHandler.fetch_all_unarchived()
        }


        //Indexing all found projects in RecyclerView
        recyclerviewinventories.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = InventoryAdapter(inventories_to_display) {

                Toast.makeText(this@MainActivity, "kiknieto ${it.name}", Toast.LENGTH_SHORT).show()
                //Go to details activity after clicking the projects name
                val intent = Intent(this@MainActivity, DetailsActivity::class.java)
                intent.putExtra("inventory_id_key", it.id)
                startActivity(intent)
            }
        }
        //Go to Settings activity
        btnSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
        //Create a new project - go to new project activity
        btnNewProject.setOnClickListener {
            val intent = Intent(this, MyXMLParser::class.java)
            startActivity(intent)
        }

        btnDelProject.setOnClickListener {
            finish()
            startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()
        //Fetching all of our projects - inventories from database
        val dbHandler = MyDbHandler(this, null, null, 1)
        var legosettings = load()
        var inventories_to_display: MutableList<Inventory>
        if (legosettings[1] == "SHOW") {
            inventories_to_display = dbHandler.fetch_all_inventories()
        } else {
            inventories_to_display = dbHandler.fetch_all_unarchived()
        }

        //Indexing all found projects in RecyclerView
        recyclerviewinventories.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = InventoryAdapter(inventories_to_display) {


                //Go to details activity after clicking the projects name
                val intent = Intent(this@MainActivity, DetailsActivity::class.java)
                intent.putExtra("inventory_id_key", it.id)
                startActivity(intent)
            }
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


}
