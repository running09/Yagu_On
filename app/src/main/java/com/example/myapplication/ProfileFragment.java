package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.myapplication.model.Team;
import com.google.android.material.button.MaterialButton;

public class ProfileFragment extends BaseMainFragment {
    private LinearLayout content;

    public static ProfileFragment newInstance(String teamId, String nickname, String email) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString("teamId", teamId);
        args.putString("nickname", nickname);
        args.putString("email", email);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        content = view.findViewById(R.id.profile_content);
        MaterialButton changeTeamButton = view.findViewById(R.id.change_team_button);
        MaterialButton logoutButton = view.findViewById(R.id.logout_button);
        changeTeamButton.setOnClickListener(v -> host().openTeamSelect());
        logoutButton.setOnClickListener(v -> host().logout());
        render();
        return view;
    }

    private void render() {
        Team team = selectedTeam();
        host().setScreenHeader("마이페이지", "계정과 선호 팀을 관리합니다.");
        content.addView(infoCard(nickname == null ? "온라인 응원러" : nickname, email == null ? "" : email, "선호 팀 · " + team.name), 0);
    }
}
