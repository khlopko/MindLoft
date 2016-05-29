package com.aavens.mindloft.db

import android.provider.BaseColumns

class ThingContract {

    abstract class ThingEntry: BaseColumns {
        companion object {
            val _ID = "_id"
            val TABLE_NAME = "things"
            val COLUMN_NAME_VALUE_TYPE = "type"
            val COLUMN_NAME_VALUE = "value"
            val COLUMN_NAME_ROOM_ID = "room_id"
            val COLUMN_NAME_DATE = "date"
        }
    }
}
