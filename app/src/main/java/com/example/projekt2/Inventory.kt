package com.example.projekt2

class Inventory {
    var id: Int = 0
    var name: String = ""
    var active: Int = 0
    var lastaccesed: Int = 0

    constructor(id: Int, name: String, active: Int, lastaccesed: Int) {
        this.id = id
        this.name = name
        this.active = active
        this.lastaccesed = lastaccesed
    }
}