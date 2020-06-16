package com.example.projekt2

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.Context
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory

class MyDbHandler(
    context: Context, name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(
    context, DATABASE_NAME,
    factory, DATABASE_VERSION
) {
    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "BrickList.db"

    }

    override fun onCreate(db: SQLiteDatabase?) {

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }


    fun fetch_all_inventories(): MutableList<Inventory> {

        var inventories = mutableListOf<Inventory>()

        val query = "SELECT * FROM Inventories order by LastAccessed DESC"

        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        while (cursor.moveToNext()) {
            val id: Int = Integer.parseInt(cursor.getString(0))
            val name: String = cursor.getString(1)
            val active: Int = Integer.parseInt(cursor.getString(2))
            val lastaccesed: Int = Integer.parseInt(cursor.getString(3))
            inventories.add(Inventory(id, name, active, lastaccesed))
        }
        cursor.close()
        db.close()
        return inventories
    }

    fun fetch_all_unarchived(): MutableList<Inventory> {

        var inventories = mutableListOf<Inventory>()

        val query = "SELECT * FROM Inventories where Active=1 order by LastAccessed DESC"

        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        while (cursor.moveToNext()) {
            val id: Int = Integer.parseInt(cursor.getString(0))
            val name: String = cursor.getString(1)
            val active: Int = Integer.parseInt(cursor.getString(2))
            val lastaccesed: Int = Integer.parseInt(cursor.getString(3))
            inventories.add(Inventory(id, name, active, lastaccesed))
        }
        cursor.close()
        db.close()
        return inventories
    }

    fun fetch_all_inventory_parts(inventoryid: Int): MutableList<InventoryPart> {

        var inventory_parts = mutableListOf<InventoryPart>()

        var inventoryid: String = inventoryid.toString()
        val query: String = "SELECT * FROM InventoriesParts where InventoryID=${inventoryid}"

        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        while (cursor.moveToNext()) {
            val id: Int = Integer.parseInt(cursor.getString(0))
            val inventoryid: Int = Integer.parseInt(cursor.getString(1))
            val typeid: Int = Integer.parseInt(cursor.getString(2))
            val itemid: Int = Integer.parseInt(cursor.getString(3))
            val quantityinset: Int = Integer.parseInt(cursor.getString(4))
            val quantityinstore: Int = Integer.parseInt(cursor.getString(5))
            val colorid: Int = Integer.parseInt(cursor.getString(6))
            val extra: Int = Integer.parseInt(cursor.getString(7))
            inventory_parts.add(
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
        cursor.close()
        db.close()
        return inventory_parts
    }

    fun add_new_inventory(new_inventory: Inventory) {
        val values = ContentValues()
        values.put("Name", new_inventory.name)
        values.put("Active", new_inventory.active)
        values.put("LastAccessed", new_inventory.lastaccesed)
        val db = this.writableDatabase
        db.insert("Inventories", null, values)
        db.close()
    }

    fun add_new_inventory_parts(inventory_part: InventoryPart) {
        val values = ContentValues()
        values.put("InventoryID", inventory_part.inventoryid)
        values.put("TypeID", inventory_part.typeid)
        values.put("ItemID", inventory_part.itemid)
        values.put("QuantityInSet", inventory_part.quantityinset)
        values.put("QuantityInStore", inventory_part.quantityinstore)
        values.put("ColorID", inventory_part.colorid)
        values.put("Extra", inventory_part.extra)
        val db = this.writableDatabase
        db.insert("InventoriesParts", null, values)
        db.close()
    }

    fun fetch_new_inventory_id(new_inventory_name: String): Int {
        val query = "SELECT id FROM Inventories where Name='${new_inventory_name}' and LastAccessed=0"
        var return_int:Int = 1
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToNext()) {
            return_int = Integer.parseInt(cursor.getString(0))
        }
        cursor.close()
        db.close()
        return return_int
    }

    fun fetch_part_itemid(itemcode: String) : Int{
        var return_itemid : Int = -1
        val query = "SELECT id FROM Parts where Code='${itemcode}'"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToNext()) {
            return_itemid = Integer.parseInt(cursor.getString(0))
        }
        cursor.close()
        db.close()
        return return_itemid
    }

    fun fetch_color_id(itemcode: String) : Int{
        var return_colorid = 1
        val query = "SELECT id FROM Colors where Code='${itemcode}'"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToNext()) {
            return_colorid = Integer.parseInt(cursor.getString(0))
        }
        cursor.close()
        db.close()
        return return_colorid
    }

    fun fetch_color(id:Int):Color?{
        val query = "SELECT * FROM Colors where id=${id}"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToNext()) {
            var colorid: Int = Integer.parseInt(cursor.getString(0))
            var code: Int = Integer.parseInt(cursor.getString(1))
            var name: String = cursor.getString(2)
            var namepl: String? = cursor.getString(3)
            cursor.close()
            db.close()
            return Color(colorid,code,name,namepl)
        }

        cursor.close()
        db.close()
        return null


    }

    fun delete_inventory(inventory_id : Int){
        val db = this.writableDatabase
        db.delete("Inventories","id=?", arrayOf(inventory_id.toString()))
        db.delete("InventoriesParts","InventoryID=?", arrayOf(inventory_id.toString()))
        db.close()
    }

    fun archive_inventory(inventory_id: Int){
        val db = this.writableDatabase
        db.execSQL("UPDATE Inventories set Active = 0 where id =${inventory_id}")
        db.close()
    }

    fun unarchive_inventory(inventory_id: Int){
        val db = this.writableDatabase
        db.execSQL("UPDATE Inventories set Active = 1 where id =${inventory_id}")
        db.close()
    }

    fun fetch_part(id:Int):Part?{
        val query = "SELECT * FROM Parts where id=${id}"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToNext()) {
            var id: Int = Integer.parseInt(cursor.getString(0))
            var typeid: Int = Integer.parseInt(cursor.getString(1))
            var code: String = cursor.getString(2)
            var name: String = cursor.getString(3)
            var namepl: String? = cursor.getString(4)
            var categoryid: Int = Integer.parseInt(cursor.getString(5))
            cursor.close()
            db.close()
            return Part(id,typeid,code,name,namepl,categoryid)
        }

        cursor.close()
        db.close()
        return null

    }

    fun increment_qty(id : Int){
        val db = this.writableDatabase
        db.execSQL("UPDATE InventoriesParts set QuantityInStore = QuantityInStore + 1 where id =${id}")
        db.close()
    }

    fun increment_lastaccessed(id : Int){
        val db = this.writableDatabase
        db.execSQL("UPDATE Inventories set LastAccessed = LastAccessed + 1 where id =${id}")
        db.close()
    }

    fun set_lastaccessed(id : Int){
        val db = this.writableDatabase
        db.execSQL("UPDATE Inventories set LastAccessed = (select max(LastAccessed)+1 from Inventories) where id =${id}")
        db.close()
    }



    fun decrement_qty(id : Int){
        val db = this.writableDatabase
        db.execSQL("UPDATE InventoriesParts set QuantityInStore = QuantityInStore - 1 where id =${id}")
        db.close()
    }

    fun save_img(itemid : Int, colorid : Int, photo : ByteArray){
        val values = ContentValues()
        values.put("Image", photo)
        val db = this.writableDatabase
        db.update("Codes",values,"ItemID =${itemid} and ColorID=${colorid}",null)
        db.close()
    }

    fun getImage(itemid : Int, colorid : Int): Bitmap? {
        val query = "SELECT Image FROM Codes where ItemID=${itemid} and ColorID=${colorid}"
        val db = this.writableDatabase

        val cursor = db.rawQuery(query,null)
        var photo:ByteArray? = null
        var bm : Bitmap? = null
        if(cursor.moveToNext()){

            photo = cursor.getBlob(0)
            if(photo != null)
            {bm = BitmapFactory.decodeByteArray(photo,0,photo.size)}
        }
        cursor.close()
        db.close()

        return bm

    }

    fun fetch_code(itemid : Int, colorid : Int):Code?{
        val query = "SELECT * FROM Codes where ItemID=${itemid} and ColorID=${colorid}"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)

        var code: Int? = null
        if (cursor.moveToNext()) {
            var id: Int = Integer.parseInt(cursor.getString(0))
            var itemid: Int = Integer.parseInt(cursor.getString(1))
            var colorid: Int? = Integer.parseInt(cursor.getString(2))
            if(cursor.getString(3)!=null){
            code = Integer.parseInt(cursor.getString(3))}
            var image: ByteArray? = cursor.getBlob(4)

            cursor.close()
            db.close()
            return Code(id,itemid,colorid,code,image)
        }

        cursor.close()
        db.close()
        return null
    }

    fun insertImage(photo:ByteArray,itemid:Int,colorid:Int){
        val values = ContentValues()
        values.put("Image", photo)
        values.put("ItemID", itemid)
        values.put("ColorID", colorid)
        val db = this.writableDatabase
        db.insert("Codes", null, values)
        db.close()
    }



}
