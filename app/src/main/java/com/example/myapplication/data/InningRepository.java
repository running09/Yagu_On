package com.example.myapplication.data;

import android.net.Uri;

import com.example.myapplication.model.Inning;
import com.example.myapplication.model.InningRecord;

public interface InningRepository {
    void getInnings(String groupId, ListCallback<Inning> callback);

    void createInning(String groupId, int inningNumber, String actorRole, AppCallback<Inning> callback);

    void getRecords(String groupId, String inningId, ListCallback<InningRecord> callback);

    void createOrUpdateRecord(String groupId, String inningId, InningRecord record, String actorUserId,
                              String actorRole, AppCallback<InningRecord> callback);

    void createOrUpdateRecordWithMedia(String groupId, String inningId, InningRecord record, Uri mediaUri,
                                       String actorUserId, String actorRole, AppCallback<InningRecord> callback);

    void deleteRecord(String groupId, String inningId, String recordUserId, String actorUserId,
                      String actorRole, AppCallback<Void> callback);
}
