package com.aavens.mindloft.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ThingsDatabaseHelper(context: Context) : SQLiteOpenHelper(context, ThingsDatabaseHelper.DATABASE_NAME, null, ThingsDatabaseHelper.DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    companion object {
        private val DATABASE_VERSION = 3
        private val DATABASE_NAME = "Rooms.db"

        private val TEXT_TYPE = " TEXT"
        private val COMMA = ","
        private val SQL_CREATE = "CREATE TABLE " + ThingContract.ThingEntry.TABLE_NAME + "( " +
                ThingContract.ThingEntry._ID + " INTEGER PRIMARY KEY, " +
                ThingContract.ThingEntry.COLUMN_NAME_VALUE_TYPE + TEXT_TYPE + COMMA +
                ThingContract.ThingEntry.COLUMN_NAME_VALUE + TEXT_TYPE + COMMA +
                ThingContract.ThingEntry.COLUMN_NAME_ROOM_ID + TEXT_TYPE + COMMA +
                ThingContract.ThingEntry.COLUMN_NAME_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ")"
        private val SQL_DELETE = "DROP TABLE IF EXISTS " + ThingContract.ThingEntry.TABLE_NAME
    }
}
