package com.aavens.mindloft.managers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.aavens.mindloft.db.RoomContract;
import com.aavens.mindloft.db.RoomDatabaseHelper;
import com.aavens.mindloft.db.ThingsDatabaseHelper;
import com.aavens.mindloft.models.Room;
import com.aavens.mindloft.models.Thing;
import com.aavens.mindloft.ui.main.MainActivity;

import java.util.ArrayList;
import java.util.Observable;

public class RoomsManager extends Observable {

    private static RoomsManager instance;

    public static RoomsManager getInstance() {
        if (instance == null) {
            instance = new RoomsManager();
        }
        return instance;
    }

    private ArrayList<Room> list = new ArrayList<>();
    private RoomDatabaseHelper roomDBHelper;

    // MARK: Public

    public void setContext(Context context) {
        roomDBHelper = new RoomDatabaseHelper(context);
        loadRooms();
    }

    public Room get(int index) {
        return list.get(index);
    }

    public int getCount() {
        return list.size();
    }

    public void append(Room room) {
        list.add(0, room);
        setChanged();
        notifyObservers(new ActionInfo(Action.ADD, 0));
        SQLiteDatabase db = roomDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(RoomContract.RoomEntry.COLUMN_NAME_TITLE, room.getTitle());
        long id = db.insert(RoomContract.RoomEntry.TABLE_NAME, null, values);
        room.setId(id);
    }

    public void delete(Room room) {
        setChanged();
        notifyObservers(new ActionInfo(Action.REMOVE, list.indexOf(room)));
        list.remove(room);
        SQLiteDatabase db = roomDBHelper.getWritableDatabase();
        String selection = RoomContract.RoomEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(room.getId()) };
        db.delete(RoomContract.RoomEntry.TABLE_NAME, selection, selectionArgs);
    }

    public void update(Room room) {
        SQLiteDatabase db = roomDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(RoomContract.RoomEntry.COLUMN_NAME_TITLE, room.getTitle());
        String selection = RoomContract.RoomEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(room.getId()) };
        db.update(RoomContract.RoomEntry.TABLE_NAME, values, selection, selectionArgs);
        setChanged();
        notifyObservers(new ActionInfo(Action.UPDATE, list.indexOf(room)));
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

    private RoomsManager() {
    }

    private void loadRooms() {
        SQLiteDatabase db = roomDBHelper.getReadableDatabase();
        String[] projection = {
                RoomContract.RoomEntry._ID,
                RoomContract.RoomEntry.COLUMN_NAME_DATE,
                RoomContract.RoomEntry.COLUMN_NAME_TITLE
        };
        String sortOrder = RoomContract.RoomEntry.COLUMN_NAME_DATE + " DESC";
        Cursor cursor = db.query(
                RoomContract.RoomEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder);
        mapCursor(cursor);
    }

    private void mapCursor(Cursor cursor) {
        if (cursor.getCount() == 0) {
            return;
        }
        list.clear();
        cursor.moveToFirst();
        while (!cursor.isLast()) {
            long id = cursor.getInt(cursor
                    .getColumnIndexOrThrow(RoomContract.RoomEntry._ID));
            String title = cursor.getString(cursor
                    .getColumnIndexOrThrow(RoomContract.RoomEntry.COLUMN_NAME_TITLE));
            String datestamp = cursor.getString(cursor
                    .getColumnIndexOrThrow(RoomContract.RoomEntry.COLUMN_NAME_DATE));
            Room room = new Room(id, title, datestamp);
            list.add(room);
            cursor.moveToNext();
        }
        cursor.close();
    }
}
