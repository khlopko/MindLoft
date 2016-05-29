package com.aavens.mindloft.db

import android.provider.BaseColumns

class RoomContract {

    abstract class RoomEntry: BaseColumns {
        companion object {
            val _ID = "_id"
            val TABLE_NAME = "rooms"
            val COLUMN_NAME_DATE = "date"
            val COLUMN_NAME_TITLE = "title"
        }
    }
}
