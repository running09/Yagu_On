package com.example.myapplication.model;

public class CheerSong {
    public String id;
    public String teamId;
    public String playerName;
    public String title;
    public String lyrics;
    public String videoUrl;
    public String type;

    public CheerSong() {
    }

    public CheerSong(String id, String teamId, String playerName, String title, String lyrics, String videoUrl, String type) {
        this.id = id;
        this.teamId = teamId;
        this.playerName = playerName;
        this.title = title;
        this.lyrics = lyrics;
        this.videoUrl = videoUrl;
        this.type = type;
    }
}
