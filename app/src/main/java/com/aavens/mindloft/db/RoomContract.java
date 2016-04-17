package com.aavens.mindloft.db;

import android.provider.BaseColumns;

public final class RoomContract {
    public RoomContract() {
    }

    public static abstract class RoomEntry implements BaseColumns {
        public static final String TABLE_NAME = "rooms";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_TITLE = "title";
    }
}
