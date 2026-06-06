package com.example.myapplication.data;

import com.example.myapplication.model.Group;
import com.example.myapplication.model.GroupMember;

public interface GroupRepository {
    void createGroup(String name, String teamId, AppCallback<Group> callback);

    void getGroupsByTeam(String teamId, ListCallback<Group> callback);

    void getMembers(String groupId, ListCallback<GroupMember> callback);

    void addMember(String groupId, String userId, String actorRole, AppCallback<Void> callback);

    void removeMember(String groupId, String targetUserId, String actorRole, AppCallback<Void> callback);

    void updateMemberRole(String groupId, String targetUserId, String newRole, String actorRole, AppCallback<Void> callback);

    void deleteGroup(String groupId, String actorRole, AppCallback<Void> callback);
}
