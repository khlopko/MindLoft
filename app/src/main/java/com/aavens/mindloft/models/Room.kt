package com.aavens.mindloft.models

data class Room(var id: Long, var title: String, var datestamp: String) {
    constructor(title: String) : this(0, title, "")
}