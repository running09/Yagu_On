package com.example.myapplication.data.firebase;

import android.os.Handler;
import android.os.Looper;

import com.example.myapplication.data.AppCallback;
import com.example.myapplication.data.UserRepository;
import com.example.myapplication.domain.FirebaseUiMessageFormatter;
import com.example.myapplication.model.UserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FirebaseUserRepository implements UserRepository {
    private static final long FIREBASE_TIMEOUT_MS = 15000;

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final Handler handler = new Handler(Looper.getMainLooper());

    public void createProfile(String uid, String email, String nickname, AppCallback<UserProfile> callback) {
        Map<String, Object> data = new HashMap<>();
        data.put("uid", uid);
        data.put("email", email);
        data.put("nickname", nickname);
        data.put("createdAt", FieldValue.serverTimestamp());
        data.put("updatedAt", FieldValue.serverTimestamp());

        final boolean[] finished = {false};
        handler.postDelayed(() -> {
            if (!finished[0]) {
                finished[0] = true;
                callback.onError(FirebaseUiMessageFormatter.createProfileTimeout());
            }
        }, FIREBASE_TIMEOUT_MS);

        db.collection("users").document(uid).set(data)
                .addOnSuccessListener(unused -> {
                    if (finished[0]) {
                        return;
                    }
                    finished[0] = true;
                    callback.onSuccess(new UserProfile(uid, email, nickname, null));
                })
                .addOnFailureListener(e -> {
                    if (finished[0]) {
                        return;
                    }
                    finished[0] = true;
                    callback.onError(message(e));
                });
    }

    @Override
    public void getCurrentUser(AppCallback<UserProfile> callback) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            callback.onError(FirebaseUiMessageFormatter.loginRequired());
            return;
        }

        final boolean[] finished = {false};
        handler.postDelayed(() -> {
            if (!finished[0]) {
                finished[0] = true;
                callback.onError(FirebaseUiMessageFormatter.loadProfileTimeout());
            }
        }, FIREBASE_TIMEOUT_MS);

        db.collection("users").document(currentUser.getUid()).get()
                .addOnSuccessListener(snapshot -> {
                    if (finished[0]) {
                        return;
                    }
                    finished[0] = true;
                    UserProfile profile = snapshot.toObject(UserProfile.class);
                    if (profile == null) {
                        callback.onError(FirebaseUiMessageFormatter.userProfileMissing());
                    } else {
                        callback.onSuccess(profile);
                    }
                })
                .addOnFailureListener(e -> {
                    if (finished[0]) {
                        return;
                    }
                    finished[0] = true;
                    callback.onError(message(e));
                });
    }

    @Override
    public void updateFavoriteTeam(String teamId, AppCallback<Void> callback) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            callback.onError(FirebaseUiMessageFormatter.loginRequired());
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("favoriteTeamId", teamId);
        data.put("updatedAt", FieldValue.serverTimestamp());
        final boolean[] finished = {false};
        handler.postDelayed(() -> {
            if (!finished[0]) {
                finished[0] = true;
                callback.onError(FirebaseUiMessageFormatter.saveFavoriteTeamTimeout());
            }
        }, FIREBASE_TIMEOUT_MS);

        db.collection("users").document(currentUser.getUid()).update(data)
                .addOnSuccessListener(unused -> {
                    if (finished[0]) {
                        return;
                    }
                    finished[0] = true;
                    callback.onSuccess(null);
                })
                .addOnFailureListener(e -> {
                    if (finished[0]) {
                        return;
                    }
                    finished[0] = true;
                    callback.onError(message(e));
                });
    }

    private String message(Exception e) {
        return FirebaseUiMessageFormatter.firebaseDataFailure(e.getMessage());
    }
}
