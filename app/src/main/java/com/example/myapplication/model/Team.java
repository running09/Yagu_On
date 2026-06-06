package com.example.myapplication.model;

public class Team {
    public final String id;
    public final String name;
    public final String shortName;
    public final int colorRes;

    public Team(String id, String name, String shortName, int colorRes) {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
        this.colorRes = colorRes;
    }
}
