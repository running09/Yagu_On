package com.example.myapplication.data.firebase;

import com.example.myapplication.data.AppCallback;
import com.example.myapplication.data.AuthRepository;
import com.example.myapplication.domain.FirebaseUiMessageFormatter;
import com.example.myapplication.model.UserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseAuthRepository implements AuthRepository {
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseUserRepository userRepository = new FirebaseUserRepository();

    @Override
    public boolean isSignedIn() {
        return auth.getCurrentUser() != null;
    }

    @Override
    public String currentUid() {
        FirebaseUser user = auth.getCurrentUser();
        return user == null ? null : user.getUid();
    }

    @Override
    public void login(String email, String password, AppCallback<UserProfile> callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> userRepository.getCurrentUser(callback))
                .addOnFailureListener(e -> callback.onError(message(e)));
    }

    @Override
    public void signup(String email, String password, String nickname, AppCallback<UserProfile> callback) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    FirebaseUser user = result.getUser();
                    if (user == null) {
                        callback.onError(FirebaseUiMessageFormatter.signupProfileUnavailable());
                        return;
                    }
                    userRepository.createProfile(user.getUid(), email, nickname, callback);
                })
                .addOnFailureListener(e -> callback.onError(message(e)));
    }

    @Override
    public void logout() {
        auth.signOut();
    }

    private String message(Exception e) {
        return FirebaseUiMessageFormatter.authFailure(e.getMessage());
    }
}
