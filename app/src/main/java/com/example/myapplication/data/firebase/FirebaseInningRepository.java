package com.example.myapplication.data.firebase;

import android.net.Uri;

import com.example.myapplication.data.AppCallback;
import com.example.myapplication.data.InningRepository;
import com.example.myapplication.data.ListCallback;
import com.example.myapplication.domain.GroupPermissionPolicy;
import com.example.myapplication.model.Inning;
import com.example.myapplication.model.InningRecord;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseInningRepository implements InningRepository {
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final GroupPermissionPolicy permissionPolicy = new GroupPermissionPolicy();

    @Override
    public void getInnings(String groupId, ListCallback<Inning> callback) {
        innings(groupId)
                .orderBy("inningNumber", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<Inning> innings = new ArrayList<>();
                    snapshot.forEach(document -> {
                        Inning inning = document.toObject(Inning.class);
                        inning.id = document.getId();
                        innings.add(inning);
                    });
                    callback.onSuccess(innings);
                })
                .addOnFailureListener(e -> callback.onError(message(e)));
    }

    @Override
    public void createInning(String groupId, int inningNumber, String actorRole, AppCallback<Inning> callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            callback.onError("로그인이 필요합니다.");
            return;
        }
        if (!permissionPolicy.canCreateInning(actorRole)) {
            callback.onError("회차를 추가할 권한이 없습니다.");
            return;
        }
        if (inningNumber < 1) {
            callback.onError("회차는 1 이상이어야 합니다.");
            return;
        }

        DocumentReference inningRef = innings(groupId).document(String.valueOf(inningNumber));
        Map<String, Object> data = new HashMap<>();
        data.put("inningNumber", inningNumber);
        data.put("createdBy", user.getUid());
        data.put("createdAt", FieldValue.serverTimestamp());

        inningRef.set(data)
                .addOnSuccessListener(unused -> {
                    Inning inning = new Inning();
                    inning.id = inningRef.getId();
                    inning.inningNumber = inningNumber;
                    inning.createdBy = user.getUid();
                    callback.onSuccess(inning);
                })
                .addOnFailureListener(e -> callback.onError(message(e)));
    }

    @Override
    public void getRecords(String groupId, String inningId, ListCallback<InningRecord> callback) {
        records(groupId, inningId)
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<InningRecord> records = new ArrayList<>();
                    snapshot.forEach(document -> {
                        InningRecord record = document.toObject(InningRecord.class);
                        record.id = document.getId();
                        records.add(record);
                    });
                    callback.onSuccess(records);
                })
                .addOnFailureListener(e -> callback.onError(message(e)));
    }

    @Override
    public void createOrUpdateRecord(String groupId, String inningId, InningRecord record, String actorUserId,
                                     String actorRole, AppCallback<InningRecord> callback) {
        if (record == null || record.authorId == null) {
            callback.onError("회차 기록 작성자 정보가 필요합니다.");
            return;
        }
        if (!permissionPolicy.canCreateOrUpdateRecord(actorRole, actorUserId, record.authorId)) {
            callback.onError("다른 구성원의 회차 기록을 작성하거나 수정할 수 없습니다.");
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("authorId", record.authorId);
        data.put("authorNickname", record.authorNickname);
        data.put("mediaType", record.mediaType);
        data.put("mediaUrl", record.mediaUrl);
        data.put("text", record.text);
        data.put("updatedAt", FieldValue.serverTimestamp());
        if (record.createdAt == null) {
            data.put("createdAt", FieldValue.serverTimestamp());
        }

        records(groupId, inningId).document(record.authorId)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(unused -> {
                    record.id = record.authorId;
                    callback.onSuccess(record);
                })
                .addOnFailureListener(e -> callback.onError(message(e)));
    }

    @Override
    public void createOrUpdateRecordWithMedia(String groupId, String inningId, InningRecord record, Uri mediaUri,
                                              String actorUserId, String actorRole, AppCallback<InningRecord> callback) {
        if (mediaUri == null) {
            createOrUpdateRecord(groupId, inningId, record, actorUserId, actorRole, callback);
            return;
        }
        if (record == null || record.authorId == null) {
            callback.onError("회차 기록 작성자 정보가 필요합니다.");
            return;
        }
        if (!permissionPolicy.canCreateOrUpdateRecord(actorRole, actorUserId, record.authorId)) {
            callback.onError("다른 구성원의 회차 기록을 작성하거나 수정할 수 없습니다.");
            return;
        }

        String extension = "video".equals(record.mediaType) ? ".mp4" : ".jpg";
        String path = "groups/" + groupId + "/innings/" + inningId + "/records/" + record.authorId + "/" + System.currentTimeMillis() + extension;
        StorageReference reference = storage.getReference().child(path);
        reference.putFile(mediaUri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful() && task.getException() != null) {
                        throw task.getException();
                    }
                    return reference.getDownloadUrl();
                })
                .addOnSuccessListener(downloadUri -> {
                    record.mediaUrl = downloadUri.toString();
                    createOrUpdateRecord(groupId, inningId, record, actorUserId, actorRole, callback);
                })
                .addOnFailureListener(e -> callback.onError(message(e)));
    }

    @Override
    public void deleteRecord(String groupId, String inningId, String recordUserId, String actorUserId,
                             String actorRole, AppCallback<Void> callback) {
        if (!permissionPolicy.canDeleteRecord(actorRole, actorUserId, recordUserId)) {
            callback.onError("회차 기록을 삭제할 권한이 없습니다.");
            return;
        }

        records(groupId, inningId).document(recordUserId)
                .delete()
                .addOnSuccessListener(unused -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onError(message(e)));
    }

    private com.google.firebase.firestore.CollectionReference innings(String groupId) {
        return db.collection("groups").document(groupId).collection("innings");
    }

    private com.google.firebase.firestore.CollectionReference records(String groupId, String inningId) {
        return innings(groupId).document(inningId).collection("records");
    }

    private String message(Exception e) {
        return e.getMessage() == null ? "회차 기록 작업에 실패했습니다." : e.getMessage();
    }
}
