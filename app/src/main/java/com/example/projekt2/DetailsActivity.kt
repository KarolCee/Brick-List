package com.example.projekt2

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_details.*

class DetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        //Fetching all parts of a specific project
        val dbHandler = MyDbHandler(this, null, null, 1)
        var bundle: Bundle? = intent.extras
        var inventoryid: Int = bundle!!.getInt("inventory_id_key")
        var partstodisplay: MutableList<InventoryPart> =
            dbHandler.fetch_all_inventory_parts(inventoryid)

        //Indexing all found parts in RecyclerView
        recyclerviewparts.apply {
            layoutManager = LinearLayoutManager(this@DetailsActivity)
            adapter = InventoryPartAdapter(partstodisplay) {

            }
        }

    }



}