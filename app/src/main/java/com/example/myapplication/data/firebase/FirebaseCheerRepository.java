package com.example.myapplication.data.firebase;

import com.example.myapplication.data.AppCallback;
import com.example.myapplication.data.CheerRepository;
import com.example.myapplication.data.ListCallback;
import com.example.myapplication.data.RealtimeSubscription;
import com.example.myapplication.model.CheerMessage;
import com.example.myapplication.model.UserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseCheerRepository implements CheerRepository {
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseUserRepository userRepository = new FirebaseUserRepository();

    @Override
    public RealtimeSubscription listenToMessages(String teamId, ListCallback<CheerMessage> callback) {
        ListenerRegistration registration = db.collection("cheerRooms")
                .document(teamId)
                .collection("messages")
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .limit(50)
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null) {
                        callback.onError(error.getMessage() == null ? "응원방을 불러오지 못했습니다." : error.getMessage());
                        return;
                    }
                    List<CheerMessage> messages = new ArrayList<>();
                    if (snapshot != null) {
                        snapshot.forEach(document -> {
                            CheerMessage message = document.toObject(CheerMessage.class);
                            message.id = document.getId();
                            messages.add(message);
                        });
                    }
                    callback.onSuccess(messages);
                });
        return registration::remove;
    }

    @Override
    public void sendMessage(String teamId, String message, AppCallback<Void> callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            callback.onError("로그인이 필요합니다.");
            return;
        }

        userRepository.getCurrentUser(new AppCallback<UserProfile>() {
            @Override
            public void onSuccess(UserProfile profile) {
                Map<String, Object> data = new HashMap<>();
                data.put("senderUid", user.getUid());
                data.put("senderNickname", profile.nickname);
                data.put("message", message);
                data.put("createdAt", FieldValue.serverTimestamp());

                db.collection("cheerRooms").document(teamId).collection("messages").add(data)
                        .addOnSuccessListener(unused -> callback.onSuccess(null))
                        .addOnFailureListener(e -> callback.onError(e.getMessage() == null ? "메시지 전송에 실패했습니다." : e.getMessage()));
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }
}
