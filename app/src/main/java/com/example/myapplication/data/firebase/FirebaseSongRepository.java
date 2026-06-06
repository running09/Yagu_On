package com.example.myapplication.data.firebase;

import com.example.myapplication.data.ListCallback;
import com.example.myapplication.data.SongRepository;
import com.example.myapplication.model.CheerSong;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class FirebaseSongRepository implements SongRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public void getSongsByTeam(String teamId, ListCallback<CheerSong> callback) {
        db.collection("cheerSongs")
                .whereEqualTo("teamId", teamId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<CheerSong> songs = new ArrayList<>();
                    snapshot.forEach(document -> {
                        CheerSong song = document.toObject(CheerSong.class);
                        song.id = document.getId();
                        songs.add(song);
                    });
                    if (songs.isEmpty()) {
                        songs.add(new CheerSong("default_team", teamId, "", "승리를 향해", "팀 대표 응원가", "", "team"));
                        songs.add(new CheerSong("default_chance", teamId, "", "찬스 테마", "득점권 상황 응원가", "", "team"));
                        songs.add(new CheerSong("default_player", teamId, "1번 타자", "1번 타자 응원가", "선수별 응원가", "", "player"));
                    }
                    callback.onSuccess(songs);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage() == null ? "응원가를 불러오지 못했습니다." : e.getMessage()));
    }
}
