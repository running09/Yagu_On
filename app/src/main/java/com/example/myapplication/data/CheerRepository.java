package com.example.myapplication.data;

import com.example.myapplication.model.CheerMessage;

public interface CheerRepository {
    RealtimeSubscription listenToMessages(String teamId, ListCallback<CheerMessage> callback);

    void sendMessage(String teamId, String message, AppCallback<Void> callback);
}
