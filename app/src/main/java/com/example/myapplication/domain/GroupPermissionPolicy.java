package com.example.myapplication.domain;

public class GroupPermissionPolicy {
    public static final String ROLE_OWNER = "owner";
    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_MEMBER = "member";

    public boolean canInviteMembers(String role) {
        return isOwner(role) || isAdmin(role);
    }

    public boolean canManageMembers(String role) {
        return isOwner(role) || isAdmin(role);
    }

    public boolean canGrantAdmin(String role) {
        return isOwner(role);
    }

    public boolean canRevokeAdmin(String role) {
        return isOwner(role);
    }

    public boolean canDeleteGroup(String role) {
        return isOwner(role);
    }

    public boolean canCreateInning(String role) {
        return isOwner(role) || isAdmin(role);
    }

    public boolean canCreateOrUpdateRecord(String role, String actorUserId, String recordUserId) {
        return sameUser(actorUserId, recordUserId) && (isOwner(role) || isAdmin(role) || isMember(role));
    }

    public boolean canDeleteRecord(String role, String actorUserId, String recordUserId) {
        return isOwner(role) || isAdmin(role) || sameUser(actorUserId, recordUserId);
    }

    public boolean isOwner(String role) {
        return ROLE_OWNER.equals(role);
    }

    public boolean isAdmin(String role) {
        return ROLE_ADMIN.equals(role);
    }

    public boolean isMember(String role) {
        return ROLE_MEMBER.equals(role);
    }

    private boolean sameUser(String actorUserId, String targetUserId) {
        return actorUserId != null && actorUserId.equals(targetUserId);
    }
}
