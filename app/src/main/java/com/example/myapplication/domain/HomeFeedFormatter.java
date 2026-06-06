package com.example.myapplication.domain;

import com.example.myapplication.model.Post;

public class HomeFeedFormatter {
    private static final String DEFAULT_RANKING = "순위 준비 중";
    private static final String DEFAULT_RECENT_RECORD = "승 · 패 · 승 · 무 · 승";

    public String body(Post post) {
        if (post == null || post.caption == null || post.caption.trim().isEmpty()) {
            return sampleCaption();
        }
        return post.caption.trim();
    }

    public String mediaSummary(Post post) {
        String label = "사진";
        if (post != null && "video".equals(post.mediaType)) {
            label = "영상";
        }
        long likeCount = post == null ? 0 : post.likeCount;
        return label + " · 좋아요 " + likeCount;
    }

    public String sampleCaption() {
        return "오늘도 끝까지 응원합니다.";
    }

    public String rankingSummary(String teamId) {
        if ("lotte".equals(teamId)) {
            return "6위 / 10개 구단";
        }
        if ("samsung".equals(teamId)) {
            return "4위 / 10개 구단";
        }
        if ("kia".equals(teamId)) {
            return "3위 / 10개 구단";
        }
        if ("lg".equals(teamId)) {
            return "2위 / 10개 구단";
        }
        return DEFAULT_RANKING;
    }

    public String recentRecordSummary(String teamId) {
        if ("lotte".equals(teamId)) {
            return "패 · 패 · 승 · 패 · 승";
        }
        if ("kia".equals(teamId)) {
            return "승 · 승 · 패 · 승 · 무";
        }
        return DEFAULT_RECENT_RECORD;
    }

    public String lastGameSummary(String teamId, String teamName) {
        if ("lotte".equals(teamId)) {
            return "롯데 4:6 KIA";
        }
        String label = teamName == null || teamName.trim().isEmpty() ? "우리 팀" : teamName.trim();
        return label + " 경기 준비 중";
    }
}
