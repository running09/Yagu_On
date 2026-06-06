package com.example.myapplication.model;

public class UserProfile {
    public String uid;
    public String email;
    public String nickname;
    public String favoriteTeamId;
    public Object createdAt;
    public Object updatedAt;

    public UserProfile() {
    }

    public UserProfile(String uid, String email, String nickname, String favoriteTeamId) {
        this.uid = uid;
        this.email = email;
        this.nickname = nickname;
        this.favoriteTeamId = favoriteTeamId;
    }

    public boolean hasFavoriteTeam() {
        return favoriteTeamId != null && !favoriteTeamId.trim().isEmpty();
    }
}
