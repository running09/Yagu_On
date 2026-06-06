package com.example.myapplication.data;

import com.example.myapplication.model.CheerSong;

public interface SongRepository {
    void getSongsByTeam(String teamId, ListCallback<CheerSong> callback);
}
