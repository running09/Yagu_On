package com.example.myapplication.data;

import com.example.myapplication.model.UserProfile;

public interface UserRepository {
    void getCurrentUser(AppCallback<UserProfile> callback);

    void updateFavoriteTeam(String teamId, AppCallback<Void> callback);
}
