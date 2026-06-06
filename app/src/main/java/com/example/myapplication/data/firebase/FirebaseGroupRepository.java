package com.example.myapplication.data.firebase;

import com.example.myapplication.data.AppCallback;
import com.example.myapplication.data.GroupRepository;
import com.example.myapplication.data.ListCallback;
import com.example.myapplication.domain.FirebaseUiMessageFormatter;
import com.example.myapplication.domain.GroupPermissionPolicy;
import com.example.myapplication.model.Group;
import com.example.myapplication.model.GroupMember;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseGroupRepository implements GroupRepository {
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final GroupPermissionPolicy permissionPolicy = new GroupPermissionPolicy();

    @Override
    public void createGroup(String name, String teamId, AppCallback<Group> callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            callback.onError(FirebaseUiMessageFormatter.loginRequired());
            return;
        }

        DocumentReference groupRef = db.collection("groups").document();
        DocumentReference memberRef = groupRef.collection("members").document(user.getUid());

        Map<String, Object> groupData = new HashMap<>();
        groupData.put("ownerId", user.getUid());
        groupData.put("adminIds", Collections.emptyList());
        groupData.put("memberIds", Collections.singletonList(user.getUid()));
        groupData.put("name", name);
        groupData.put("teamId", teamId);
        groupData.put("createdAt", FieldValue.serverTimestamp());
        groupData.put("updatedAt", FieldValue.serverTimestamp());

        Map<String, Object> memberData = new HashMap<>();
        memberData.put("userId", user.getUid());
        memberData.put("nickname", "");
        memberData.put("role", GroupPermissionPolicy.ROLE_OWNER);
        memberData.put("joinedAt", FieldValue.serverTimestamp());
        memberData.put("updatedAt", FieldValue.serverTimestamp());

        WriteBatch batch = db.batch();
        batch.set(groupRef, groupData);
        batch.set(memberRef, memberData);
        batch.commit()
                .addOnSuccessListener(unused -> {
                    Group group = new Group();
                    group.id = groupRef.getId();
                    group.ownerId = user.getUid();
                    group.adminIds = Collections.emptyList();
                    group.memberIds = Collections.singletonList(user.getUid());
                    group.name = name;
                    group.teamId = teamId;
                    callback.onSuccess(group);
                })
                .addOnFailureListener(e -> callback.onError(message(e)));
    }

    @Override
    public void getGroupsByTeam(String teamId, ListCallback<Group> callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            callback.onError(FirebaseUiMessageFormatter.loginRequired());
            return;
        }

        db.collection("groups")
                .whereArrayContains("memberIds", user.getUid())
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<Group> groups = new ArrayList<>();
                    snapshot.forEach(document -> {
                        Group group = document.toObject(Group.class);
                        group.id = document.getId();
                        if (teamId == null || teamId.equals(group.teamId)) {
                            groups.add(group);
                        }
                    });
                    callback.onSuccess(groups);
                })
                .addOnFailureListener(e -> callback.onError(message(e)));
    }

    @Override
    public void getMembers(String groupId, ListCallback<GroupMember> callback) {
        db.collection("groups").document(groupId).collection("members")
                .orderBy("joinedAt", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<GroupMember> members = new ArrayList<>();
                    snapshot.forEach(document -> members.add(document.toObject(GroupMember.class)));
                    callback.onSuccess(members);
                })
                .addOnFailureListener(e -> callback.onError(message(e)));
    }

    @Override
    public void addMember(String groupId, String userId, String actorRole, AppCallback<Void> callback) {
        if (!permissionPolicy.canInviteMembers(actorRole)) {
            callback.onError(FirebaseUiMessageFormatter.groupPermissionDenied());
            return;
        }

        Map<String, Object> memberData = new HashMap<>();
        memberData.put("userId", userId);
        memberData.put("nickname", "");
        memberData.put("role", GroupPermissionPolicy.ROLE_MEMBER);
        memberData.put("joinedAt", FieldValue.serverTimestamp());
        memberData.put("updatedAt", FieldValue.serverTimestamp());

        DocumentReference groupRef = db.collection("groups").document(groupId);
        DocumentReference memberRef = groupRef.collection("members").document(userId);
        WriteBatch batch = db.batch();
        batch.set(memberRef, memberData);
        batch.update(groupRef, "memberIds", FieldValue.arrayUnion(userId), "updatedAt", FieldValue.serverTimestamp());
        batch.commit()
                .addOnSuccessListener(unused -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onError(message(e)));
    }

    @Override
    public void removeMember(String groupId, String targetUserId, String actorRole, AppCallback<Void> callback) {
        if (!permissionPolicy.canManageMembers(actorRole)) {
            callback.onError(FirebaseUiMessageFormatter.groupPermissionDenied());
            return;
        }

        DocumentReference groupRef = db.collection("groups").document(groupId);
        DocumentReference memberRef = groupRef.collection("members").document(targetUserId);
        WriteBatch batch = db.batch();
        batch.delete(memberRef);
        batch.update(groupRef,
                "memberIds", FieldValue.arrayRemove(targetUserId),
                "adminIds", FieldValue.arrayRemove(targetUserId),
                "updatedAt", FieldValue.serverTimestamp());
        batch.commit()
                .addOnSuccessListener(unused -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onError(message(e)));
    }

    @Override
    public void updateMemberRole(String groupId, String targetUserId, String newRole, String actorRole,
                                 AppCallback<Void> callback) {
        if (!permissionPolicy.canGrantAdmin(actorRole)) {
            callback.onError(FirebaseUiMessageFormatter.groupPermissionDenied());
            return;
        }
        if (!GroupPermissionPolicy.ROLE_ADMIN.equals(newRole) && !GroupPermissionPolicy.ROLE_MEMBER.equals(newRole)) {
            callback.onError(FirebaseUiMessageFormatter.groupRoleChangeNotAllowed());
            return;
        }

        DocumentReference groupRef = db.collection("groups").document(groupId);
        DocumentReference memberRef = groupRef.collection("members").document(targetUserId);
        WriteBatch batch = db.batch();
        Map<String, Object> memberData = new HashMap<>();
        memberData.put("role", newRole);
        memberData.put("updatedAt", FieldValue.serverTimestamp());
        batch.update(memberRef, memberData);
        if (GroupPermissionPolicy.ROLE_ADMIN.equals(newRole)) {
            batch.update(groupRef, "adminIds", FieldValue.arrayUnion(targetUserId), "updatedAt", FieldValue.serverTimestamp());
        } else {
            batch.update(groupRef, "adminIds", FieldValue.arrayRemove(targetUserId), "updatedAt", FieldValue.serverTimestamp());
        }
        batch.commit()
                .addOnSuccessListener(unused -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onError(message(e)));
    }

    @Override
    public void deleteGroup(String groupId, String actorRole, AppCallback<Void> callback) {
        if (!permissionPolicy.canDeleteGroup(actorRole)) {
            callback.onError(FirebaseUiMessageFormatter.groupPermissionDenied());
            return;
        }

        db.collection("groups").document(groupId)
                .delete()
                .addOnSuccessListener(unused -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onError(message(e)));
    }

    private String message(Exception e) {
        return FirebaseUiMessageFormatter.firebaseDataFailure(e.getMessage());
    }
}
