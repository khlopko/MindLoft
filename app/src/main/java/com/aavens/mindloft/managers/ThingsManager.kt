package com.aavens.mindloft.managers

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

import com.aavens.mindloft.db.ThingContract
import com.aavens.mindloft.db.ThingsDatabaseHelper
import com.aavens.mindloft.models.Thing

import java.util.ArrayList
import java.util.Observable

class ThingsManager private constructor() : Observable() {

    companion object {

        val instance: ThingsManager by lazy { ThingsManager() }
    }

    var context: Context? = null
        set(value) {
            dbHelper = ThingsDatabaseHelper(value!!)
        }
    var roomId: Long? = null
        set(value) = loadThings()

    private lateinit var dbHelper: ThingsDatabaseHelper
    private val things = ArrayList<Thing>()

    // MARK: Public

    operator fun get(index: Int): Thing {
        return things[index]
    }

    val count: Int
        get() = things.size

    fun append(thing: Thing) {
        val db = dbHelper?.writableDatabase
        val values = ContentValues()
        values.put(ThingContract.ThingEntry.COLUMN_NAME_ROOM_ID, thing.roomId)
        values.put(ThingContract.ThingEntry.COLUMN_NAME_VALUE, thing.data)
        values.put(ThingContract.ThingEntry.COLUMN_NAME_VALUE_TYPE, thing.type.ordinal)
        val id = db?.insert(ThingContract.ThingEntry.TABLE_NAME, null, values)
        if (id != null) {
            thing.id = id
            things.add(thing)
            setChanged()
            notifyObservers(ActionInfo(Action.ADD, things.size - 1))
        }
    }

    fun removeAtIndex(index: Int) {
        val thing = things[index]
        val db = dbHelper?.writableDatabase
        val selection = ThingContract.ThingEntry._ID + " = ?"
        val selectionArgs = arrayOf(thing.id.toString())
        db.delete(ThingContract.ThingEntry.TABLE_NAME, selection, selectionArgs)
        things.removeAt(index)
        setChanged()
        notifyObservers(ActionInfo(Action.REMOVE, index))
    }

    fun update(index: Int, value: String) {
        val thing = get(index)
        thing.data = value
        val db = dbHelper?.writableDatabase
        val values = ContentValues()
        values.put(ThingContract.ThingEntry.COLUMN_NAME_VALUE, thing.data)
        val selection = ThingContract.ThingEntry._ID + " = ?"
        val selectionArgs = arrayOf(thing.id.toString())
        db?.update(ThingContract.ThingEntry.TABLE_NAME, values, selection, selectionArgs)
        setChanged()
        notifyObservers(ActionInfo(Action.UPDATE, index))
    }

    // MARK: Update action

    inner class ActionInfo internal constructor(val action: Action, val position: Int)

    enum class Action {
        ADD,
        UPDATE,
        REMOVE
    }

    private fun loadThings() {
        val db = dbHelper?.readableDatabase
        val projection = arrayOf(
                ThingContract.ThingEntry._ID,
                ThingContract.ThingEntry.COLUMN_NAME_ROOM_ID,
                ThingContract.ThingEntry.COLUMN_NAME_VALUE,
                ThingContract.ThingEntry.COLUMN_NAME_VALUE_TYPE,
                ThingContract.ThingEntry.COLUMN_NAME_DATE
        )
        val selection = ThingContract.ThingEntry.COLUMN_NAME_ROOM_ID + " = ?"
        val selectionArgs = arrayOf(roomId.toString())
        val cursor = db.query(
                ThingContract.ThingEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null)
        mapCursor(cursor)
    }

    private fun mapCursor(cursor: Cursor) {
        if (cursor.count == 0) {
            return
        }
        things.clear()
        cursor.moveToFirst()
        do {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(ThingContract.ThingEntry._ID))
            val value = cursor.getString(cursor.getColumnIndexOrThrow(ThingContract.ThingEntry.COLUMN_NAME_VALUE))
            val typeRaw = cursor.getInt(cursor.getColumnIndexOrThrow(ThingContract.ThingEntry.COLUMN_NAME_VALUE_TYPE))
            val type = Thing.Type.values()[typeRaw]
            val roomId = cursor.getLong(cursor.getColumnIndexOrThrow(ThingContract.ThingEntry.COLUMN_NAME_ROOM_ID))
            val thing = Thing(id, type, value, roomId)
            things.add(thing)
        } while (cursor.moveToNext())
        setChanged()
        notifyObservers(ActionInfo(Action.ADD, -1))
        cursor.close()
    }
}