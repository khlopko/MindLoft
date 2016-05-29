package com.aavens.mindloft.managers

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

import com.aavens.mindloft.db.RoomContract
import com.aavens.mindloft.db.RoomDatabaseHelper
import com.aavens.mindloft.models.Room

import java.util.ArrayList
import java.util.Observable

class RoomsManager private constructor(): Observable() {

    lateinit var context: Context

    private val list = ArrayList<Room>()
    private var roomDBHelper: RoomDatabaseHelper? = null

    operator fun get(index: Int): Room {
        return list[index]
    }

    val count: Int
        get() = list.size

    fun append(room: Room) {
        list.add(0, room)
        setChanged()
        notifyObservers(ActionInfo(Action.ADD, 0))
        val db = roomDBHelper?.writableDatabase
        val values = ContentValues()
        values.put(RoomContract.RoomEntry.COLUMN_NAME_TITLE, room.title)
        val id = db?.insert(RoomContract.RoomEntry.TABLE_NAME, null, values)
        if (id != null) {
            room.id = id
        }
    }

    fun delete(room: Room) {
        setChanged()
        notifyObservers(ActionInfo(Action.REMOVE, list.indexOf(room)))
        list.remove(room)
        val db = roomDBHelper?.writableDatabase
        val selection = RoomContract.RoomEntry._ID + " = ?"
        val selectionArgs = arrayOf(room.id.toString())
        db?.delete(RoomContract.RoomEntry.TABLE_NAME, selection, selectionArgs)
    }

    fun update(room: Room) {
        val db = roomDBHelper?.writableDatabase
        val values = ContentValues()
        values.put(RoomContract.RoomEntry.COLUMN_NAME_TITLE, room.title)
        val selection = RoomContract.RoomEntry._ID + " = ?"
        val selectionArgs = arrayOf(room.id.toString())
        db?.update(RoomContract.RoomEntry.TABLE_NAME, values, selection, selectionArgs)
        setChanged()
        notifyObservers(ActionInfo(Action.UPDATE, list.indexOf(room)))
    }

    // MARK: Update action

    inner class ActionInfo internal constructor(val action: Action, val position: Int)

    enum class Action {
        ADD,
        UPDATE,
        REMOVE
    }

    private fun loadRooms() {
        val db = roomDBHelper?.readableDatabase
        val projection = arrayOf(RoomContract.RoomEntry._ID, RoomContract.RoomEntry.COLUMN_NAME_DATE, RoomContract.RoomEntry.COLUMN_NAME_TITLE)
        val sortOrder = RoomContract.RoomEntry.COLUMN_NAME_DATE + " DESC"
        val cursor = db?.query(
                RoomContract.RoomEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder)
        if (cursor != null) {
            mapCursor(cursor)
        }
    }

    private fun mapCursor(cursor: Cursor) {
        if (cursor.count == 0) {
            return
        }
        list.clear()
        cursor.moveToFirst()
        while (!cursor.isLast) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(RoomContract.RoomEntry._ID)).toLong()
            val title = cursor.getString(cursor.getColumnIndexOrThrow(RoomContract.RoomEntry.COLUMN_NAME_TITLE))
            val datestamp = cursor.getString(cursor.getColumnIndexOrThrow(RoomContract.RoomEntry.COLUMN_NAME_DATE))
            val room = Room(id, title, datestamp)
            list.add(room)
            cursor.moveToNext()
        }
        cursor.close()
    }

    companion object {

        val instance: RoomsManager by lazy {
            RoomsManager()
        }
    }
}
