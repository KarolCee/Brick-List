package com.example.projekt2

class Color {
    var id: Int = 0
    var code: Int = 0
    var name: String = ""
    var namepl: String? = null

    constructor(id: Int, code: Int, name: String, namepl: String?) {
        this.id = id
        this.code = code
        this.name = name
        this.namepl = namepl
    }
}