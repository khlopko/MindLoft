package com.aavens.mindloft.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ThingsDatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Things.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA = ",";
    private static final String SQL_CREATE = "CREATE TABLE " + ThingContract.ThingEntry.TABLE_NAME + "( " +
            ThingContract.ThingEntry._ID + " INTEGER PRIMARY KEY, " +
            ThingContract.ThingEntry.COLUMN_NAME_ID + TEXT_TYPE + COMMA +
            ThingContract.ThingEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA +
            ThingContract.ThingEntry.COLUMN_NAME_VALUE_TYPE + TEXT_TYPE + COMMA +
            ThingContract.ThingEntry.COLUMN_NAME_VALUE + TEXT_TYPE + COMMA +
            ThingContract.ThingEntry.COLUMN_NAME_ROOM_ID + TEXT_TYPE + COMMA +
            ThingContract.ThingEntry.COLUMN_NAME_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP" +
            ")";
    private static final String SQL_DELETE = "DROP TABLE IF EXISTS " + ThingContract.ThingEntry.TABLE_NAME;

    public ThingsDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
