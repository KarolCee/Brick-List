package com.example.projekt2

class InventoryPart {
    var id: Int = 0
    var inventoryid: Int = 0
    var typeid: Int = 0
    var itemid: Int = 0
    var quantityinset: Int = 0
    var quantityinstore: Int = 0
    var colorid: Int = 0
    var extra: Int = 0

    constructor(
        id: Int,
        inventoryid: Int,
        typeid: Int,
        itemid: Int,
        quantityinset: Int,
        quantityinstore: Int,
        colorid: Int,
        extra: Int
    ) {
        this.id = id
        this.inventoryid = inventoryid
        this.typeid = typeid
        this.itemid = itemid
        this.quantityinset = quantityinset
        this.quantityinstore = quantityinstore
        this.colorid = colorid
        this.extra = extra
    }
}