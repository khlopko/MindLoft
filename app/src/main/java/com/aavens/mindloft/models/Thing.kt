package com.aavens.mindloft.models

data class Thing(var id: Long, var type: Type, var data: String = "", var roomId: Long) {
    constructor(type: Type, data: String, roomId: Long) : this(-1, type, data, roomId)

    enum class Type {
        TEXT,
        LINK,
        IMAGE
    }
}