package com.example.projekt2

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.inventorypart_row.view.*
import java.lang.Exception


class InventoryPartAdapter(
    private val returnedList: MutableList<InventoryPart>,
    val clicked: (InventoryPart) -> Unit
) :
    RecyclerView.Adapter<InventoryPartAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.inventorypart_row, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = returnedList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //holder.view.mytextview.text = returnedList[position].inventoryid.toString()
        var item = returnedList[position]
        val dbHandler = MyDbHandler(holder.view.context, null, null, 1)
        if(item.quantityinstore == item.quantityinset){
            holder.view.cardview.setBackgroundColor(android.graphics.Color.GREEN)
        }
        else{
            holder.view.cardview.setBackgroundColor(android.graphics.Color.WHITE)
        }


        var item_color:Color? = dbHandler.fetch_color(item.colorid)
        var item_part:Part? = dbHandler.fetch_part(item.itemid)

        var item_color_name : String = ""
        var item_part_name : String = ""
        var item_part_code : String = ""


        if(item_color?.name != null){
            item_color_name = item_color?.name
        }
        if(item_part?.code!=null){
            item_part_code = "[${item_part?.code}]"
        }

        if(item_part?.name!=null){
            item_part_name = item_part?.name
        }
        holder.view.imageView.setImageResource(android.R.color.transparent)
        var mymap : Bitmap? = null
        if(item_color!=null && item_part!=null){
            mymap = dbHandler.getImage(item_part.id,item_color.id)
            if (mymap!=null){
                try{
                    holder.view.imageView.setImageBitmap(mymap)
                }catch(e:Exception){

                }
            }
        }
        else{
            item_part_name = "No such part in DB"
            item_color_name = ""
        }



        holder.view.txtInfo.text = "${item_part_name} \n ${item_color_name} ${item_part_code}"
        holder.view.txtQty.text = "${item.quantityinstore} of ${item.quantityinset}"


        holder.view.btnIncrement.setOnClickListener {
            if (item.quantityinstore < item.quantityinset) {
                dbHandler.increment_qty(item.id)
                item.quantityinstore = item.quantityinstore + 1
                holder.view.txtQty.text = "${item.quantityinstore} of ${item.quantityinset}"

                if(item.quantityinstore == item.quantityinset){
                    holder.view.cardview.setBackgroundColor(android.graphics.Color.GREEN)
                }
            }

        }

        holder.view.btnDecrement.setOnClickListener {
            if (item.quantityinstore > 0) {
                dbHandler.decrement_qty(item.id)
                item.quantityinstore = item.quantityinstore - 1
                holder.view.txtQty.text = "${item.quantityinstore} of ${item.quantityinset}"
                if(item.quantityinstore != item.quantityinset){
                    holder.view.cardview.setBackgroundColor(android.graphics.Color.WHITE)
                }
            }
        }

    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)


}

