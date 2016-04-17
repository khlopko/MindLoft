package com.aavens.mindloft.db;

import android.provider.BaseColumns;

public class ThingContract {

    public ThingContract() {
    }

    public static abstract class ThingEntry implements BaseColumns {
        public static final String TABLE_NAME = "things";
        public static final String COLUMN_NAME_VALUE_TYPE = "type";
        public static final String COLUMN_NAME_VALUE = "value";
        public static final String COLUMN_NAME_ROOM_ID = "room_id";
        public static final String COLUMN_NAME_DATE = "date";
    }
}
