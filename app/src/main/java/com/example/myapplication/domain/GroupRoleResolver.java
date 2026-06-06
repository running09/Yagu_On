package com.example.myapplication.domain;

import com.example.myapplication.model.Group;
import com.example.myapplication.model.GroupMember;

import java.util.List;

public class GroupRoleResolver {
    public String resolve(Group group, List<GroupMember> members, String currentUserId) {
        if (group != null && currentUserId != null) {
            if (currentUserId.equals(group.ownerId)) {
                return GroupPermissionPolicy.ROLE_OWNER;
            }
            if (group.adminIds != null && group.adminIds.contains(currentUserId)) {
                return GroupPermissionPolicy.ROLE_ADMIN;
            }
        }

        if (members != null && currentUserId != null) {
            for (GroupMember member : members) {
                if (member != null && currentUserId.equals(member.userId) && member.role != null) {
                    return member.role;
                }
            }
        }

        return GroupPermissionPolicy.ROLE_MEMBER;
    }
}
