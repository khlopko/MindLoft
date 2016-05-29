package com.aavens.mindloft.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class RoomDatabaseHelper(context: Context) : SQLiteOpenHelper(context, RoomDatabaseHelper.DATABASE_NAME, null, RoomDatabaseHelper.DATABASE_VERSION) {

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
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "Rooms.db"

        private val TEXT_TYPE = " TEXT"
        private val COMMA = ","
        private val SQL_CREATE = "CREATE TABLE " + RoomContract.RoomEntry.TABLE_NAME + "( " +
                RoomContract.RoomEntry._ID + " INTEGER PRIMARY KEY, " +
                RoomContract.RoomEntry.COLUMN_NAME_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP" + COMMA +
                RoomContract.RoomEntry.COLUMN_NAME_TITLE + TEXT_TYPE +
                ")"
        private val SQL_DELETE = "DROP TABLE IF EXISTS " + RoomContract.RoomEntry.TABLE_NAME
    }
}
