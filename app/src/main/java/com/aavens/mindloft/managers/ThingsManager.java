package com.aavens.mindloft.managers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.aavens.mindloft.db.ThingContract;
import com.aavens.mindloft.db.ThingsDatabaseHelper;
import com.aavens.mindloft.models.Thing;

import java.util.ArrayList;
import java.util.Observable;

public class ThingsManager extends Observable {

    private static ThingsManager instance;

    public static ThingsManager getInstance() {
        if (instance == null) {
            instance = new ThingsManager();
        }
        return instance;
    }

    public static void initialize(Context context, long roomId) {
        instance = new ThingsManager();
        instance.dbHelper = new ThingsDatabaseHelper(context);
        instance.roomId = roomId;
        instance.loadThings();
    }

    private ThingsDatabaseHelper dbHelper;
    private ArrayList<Thing> things =  new ArrayList<>();
    private long roomId;

    // MARK: Public

    public Thing get(int index) {
        return things.get(index);
    }

    public int getCount() {
        return things.size();
    }

    public void append(Thing thing) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ThingContract.ThingEntry.COLUMN_NAME_ROOM_ID, thing.getRoomId());
        values.put(ThingContract.ThingEntry.COLUMN_NAME_VALUE, thing.getData());
        values.put(ThingContract.ThingEntry.COLUMN_NAME_VALUE_TYPE, thing.getType().ordinal());
        long id = db.insert(ThingContract.ThingEntry.TABLE_NAME, null, values);
        thing.setId(id);
        things.add(thing);
        setChanged();
        notifyObservers(new ActionInfo(Action.ADD, things.size() - 1));
    }

    public void removeAtIndex(int index) {
        Thing thing = things.get(index);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = ThingContract.ThingEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(thing.getId()) };
        db.delete(ThingContract.ThingEntry.TABLE_NAME, selection, selectionArgs);
        things.remove(index);
        setChanged();
        notifyObservers(new ActionInfo(Action.REMOVE, index));
    }

    public void update(int index, String value) {
        Thing thing = get(index);
        thing.setData(value);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ThingContract.ThingEntry.COLUMN_NAME_VALUE, thing.getData());
        String selection = ThingContract.ThingEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(thing.getId()) };
        db.update(ThingContract.ThingEntry.TABLE_NAME, values, selection, selectionArgs);
        setChanged();
        notifyObservers(new ActionInfo(Action.UPDATE, index));
    }

    // MARK: Update action

    public class ActionInfo {

        private Action action;
        private int position;

        private ActionInfo(Action action, int position) {
            this.position = position;
            this.action = action;
        }

        public Action getAction() {
            return action;
        }

        public int getPosition() {
            return position;
        }
    }

    public enum Action {
        ADD,
        UPDATE,
        REMOVE
    }

    // MARK: Private

    private ThingsManager() {
    }

    private void loadThings() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                ThingContract.ThingEntry._ID,
                ThingContract.ThingEntry.COLUMN_NAME_ROOM_ID,
                ThingContract.ThingEntry.COLUMN_NAME_VALUE,
                ThingContract.ThingEntry.COLUMN_NAME_VALUE_TYPE,
                ThingContract.ThingEntry.COLUMN_NAME_DATE
        };
        String selection = ThingContract.ThingEntry.COLUMN_NAME_ROOM_ID + " = ?";
        String[] selectionArgs = { String.valueOf(roomId) };
        Cursor cursor = db.query(
                ThingContract.ThingEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null);
        mapCursor(cursor);
    }

    private void mapCursor(Cursor cursor) {
        if (cursor.getCount() == 0) {
            return;
        }
        things.clear();
        cursor.moveToFirst();
        do {
            long id = cursor.getLong(cursor
                    .getColumnIndexOrThrow(ThingContract.ThingEntry._ID));
            String value = cursor.getString(cursor
                    .getColumnIndexOrThrow(ThingContract.ThingEntry.COLUMN_NAME_VALUE));
            int typeRaw = cursor.getInt(cursor
                    .getColumnIndexOrThrow(ThingContract.ThingEntry.COLUMN_NAME_VALUE_TYPE));
            Thing.Type type = Thing.Type.values()[typeRaw];
            long roomId = cursor.getLong(cursor
                    .getColumnIndexOrThrow(ThingContract.ThingEntry.COLUMN_NAME_ROOM_ID));
            Thing thing = new Thing(id, type, value, roomId);
            things.add(thing);
        } while (cursor.moveToNext());
        setChanged();
        notifyObservers(new ActionInfo(Action.ADD, -1));
        cursor.close();
    }
}
