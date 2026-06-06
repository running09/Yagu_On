package com.example.myapplication.util;

import com.example.myapplication.R;
import com.example.myapplication.model.Team;

import java.util.Arrays;
import java.util.List;

public final class TeamCatalog {
    private static final List<Team> TEAMS = Arrays.asList(
            new Team("lg", "LG 트윈스", "LG", R.color.brand_red),
            new Team("doosan", "두산 베어스", "두산", R.color.brand_blue),
            new Team("kiwoom", "키움 히어로즈", "키움", R.color.brand_green),
            new Team("ssg", "SSG 랜더스", "SSG", R.color.brand_red),
            new Team("kt", "KT 위즈", "KT", R.color.brand_blue),
            new Team("kia", "KIA 타이거즈", "KIA", R.color.brand_red),
            new Team("samsung", "삼성 라이온즈", "삼성", R.color.brand_blue),
            new Team("lotte", "롯데 자이언츠", "롯데", R.color.brand_green),
            new Team("nc", "NC 다이노스", "NC", R.color.brand_blue),
            new Team("hanwha", "한화 이글스", "한화", R.color.brand_gold)
    );

    private TeamCatalog() {
    }

    public static List<Team> all() {
        return TEAMS;
    }

    public static Team find(String id) {
        for (Team team : TEAMS) {
            if (team.id.equals(id)) {
                return team;
            }
        }
        return TEAMS.get(0);
    }
}
