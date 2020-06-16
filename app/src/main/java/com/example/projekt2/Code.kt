package com.example.projekt2

import java.sql.Blob

class Code {
    var id: Int = 0
    var itemid: Int = 0
    var colorid: Int? = null
    var code: Int? = null
    var image: ByteArray? = null

    constructor(id: Int, itemid: Int, colorid: Int?, code: Int?, image: ByteArray?) {
        this.id = id
        this.itemid = itemid
        this.colorid = colorid
        this.code = code
        this.image = image
    }
}