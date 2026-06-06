package com.example.myapplication.data;

import com.example.myapplication.model.UserProfile;

public interface AuthRepository {
    boolean isSignedIn();

    String currentUid();

    void login(String email, String password, AppCallback<UserProfile> callback);

    void signup(String email, String password, String nickname, AppCallback<UserProfile> callback);

    void logout();
}
