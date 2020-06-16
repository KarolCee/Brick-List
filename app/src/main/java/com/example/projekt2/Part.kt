package com.example.projekt2

class Part {
    var id: Int = 0
    var typeid: Int = 0
    var code: String = ""
    var name: String = ""
    var namepl: String? = null
    var categoryid: Int = 0

    constructor(
        id: Int,
        typeid: Int,
        code: String,
        name: String,
        namepl: String?,
        categoryid: Int
    ) {
        this.id = id
        this.typeid = typeid
        this.code = code
        this.name = name
        this.namepl = namepl
        this.categoryid = categoryid
    }
}