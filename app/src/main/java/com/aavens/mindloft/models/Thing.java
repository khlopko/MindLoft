package com.aavens.mindloft.models;

public class Thing {

    private long id;
    private String title;
    private Type type;
    private String data;
    private long roomId;

    public Thing(long id, String title, Type type, String data, long roomId) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.data = data;
        this.roomId = roomId;
    }

    public Thing(String title, Type type, String data, long roomId) {
        this.title = title;
        this.type = type;
        this.data = data;
        this.roomId = roomId;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    public Type getType() { return type; }

    public long getRoomId() { return roomId; }

    // MARK: Type

    public enum Type {
        TEXT,
        LINK,
        IMAGE
    }
}
