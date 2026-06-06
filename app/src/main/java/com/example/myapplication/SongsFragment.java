package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.myapplication.data.ListCallback;
import com.example.myapplication.data.RepositoryProvider;
import com.example.myapplication.data.SongRepository;
import com.example.myapplication.model.CheerSong;
import com.example.myapplication.model.Team;

import java.util.ArrayList;
import java.util.List;

public class SongsFragment extends BaseMainFragment {
    private final SongRepository songRepository = RepositoryProvider.songs();
    private LinearLayout content;

    public static SongsFragment newInstance(String teamId, String nickname, String email) {
        SongsFragment fragment = new SongsFragment();
        Bundle args = new Bundle();
        args.putString("teamId", teamId);
        args.putString("nickname", nickname);
        args.putString("email", email);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_songs, container, false);
        content = view.findViewById(R.id.songs_content);
        render();
        return view;
    }

    private void render() {
        Team team = selectedTeam();
        host().setScreenHeader("응원가 모음", team.name + " 응원가를 모아봅니다.");
        addBodyText(content, "응원가를 불러오는 중입니다...");

        songRepository.getSongsByTeam(team.id, new ListCallback<CheerSong>() {
            @Override
            public void onSuccess(List<CheerSong> songs) {
                if (!isAdded()) {
                    return;
                }
                content.removeAllViews();
                List<CheerSong> teamSongs = new ArrayList<>();
                List<CheerSong> playerSongs = new ArrayList<>();
                for (CheerSong song : songs) {
                    if ("player".equals(song.type)) {
                        playerSongs.add(song);
                    } else {
                        teamSongs.add(song);
                    }
                }
                addSection(content, "팀 응원가");
                for (CheerSong song : teamSongs) {
                    content.addView(songCard(song));
                }
                addSection(content, "선수 응원가");
                for (CheerSong song : playerSongs) {
                    content.addView(songCard(song));
                }
            }

            @Override
            public void onError(String message) {
                if (isAdded()) {
                    content.addView(infoCard("응원가를 불러오지 못했습니다", "잠시 후 다시 시도해 주세요.", "네트워크 상태를 확인하면 도움이 됩니다."));
                }
            }
        });
    }

    private View songCard(CheerSong song) {
        String meta = ("player".equals(song.type) ? "선수 응원가" : "팀 응원가");
        if (song.playerName != null && !song.playerName.isEmpty()) {
            meta += " · " + song.playerName;
        }
        return infoCard(song.title, meta, song.lyrics == null ? "" : song.lyrics);
    }
}
