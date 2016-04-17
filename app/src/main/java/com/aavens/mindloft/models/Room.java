package com.aavens.mindloft.models;

public class Room {

    private long id;
    private String title;
    private String datestamp;

    public Room(String title) {
        this.title = title;
    }

    public Room(long id, String title, String datestamp) {
        this.id = id;
        this.title = title;
        this.datestamp = datestamp;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
}
