package com.example.myapplication.domain;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GroupPermissionPolicyTest {
    private final GroupPermissionPolicy policy = new GroupPermissionPolicy();

    @Test
    public void ownerCanGrantAdminAndDeleteGroup() {
        assertTrue(policy.canGrantAdmin(GroupPermissionPolicy.ROLE_OWNER));
        assertTrue(policy.canRevokeAdmin(GroupPermissionPolicy.ROLE_OWNER));
        assertTrue(policy.canDeleteGroup(GroupPermissionPolicy.ROLE_OWNER));
    }

    @Test
    public void adminCanManageMembersAndCreateInningsButCannotGrantAdminOrDeleteGroup() {
        assertTrue(policy.canInviteMembers(GroupPermissionPolicy.ROLE_ADMIN));
        assertTrue(policy.canManageMembers(GroupPermissionPolicy.ROLE_ADMIN));
        assertTrue(policy.canCreateInning(GroupPermissionPolicy.ROLE_ADMIN));
        assertFalse(policy.canGrantAdmin(GroupPermissionPolicy.ROLE_ADMIN));
        assertFalse(policy.canDeleteGroup(GroupPermissionPolicy.ROLE_ADMIN));
    }

    @Test
    public void memberCannotUseGroupManagementActions() {
        assertFalse(policy.canManageMembers(GroupPermissionPolicy.ROLE_MEMBER));
        assertFalse(policy.canGrantAdmin(GroupPermissionPolicy.ROLE_MEMBER));
        assertFalse(policy.canDeleteGroup(GroupPermissionPolicy.ROLE_MEMBER));
        assertFalse(policy.canCreateInning(GroupPermissionPolicy.ROLE_MEMBER));
    }

    @Test
    public void memberCanWriteOnlyOwnInningRecord() {
        assertTrue(policy.canCreateOrUpdateRecord(GroupPermissionPolicy.ROLE_MEMBER, "user-a", "user-a"));
        assertFalse(policy.canCreateOrUpdateRecord(GroupPermissionPolicy.ROLE_MEMBER, "user-a", "user-b"));
    }

    @Test
    public void ownerAndAdminCanDeleteAnyInningRecord() {
        assertTrue(policy.canDeleteRecord(GroupPermissionPolicy.ROLE_OWNER, "owner", "member"));
        assertTrue(policy.canDeleteRecord(GroupPermissionPolicy.ROLE_ADMIN, "admin", "member"));
        assertTrue(policy.canDeleteRecord(GroupPermissionPolicy.ROLE_MEMBER, "member", "member"));
        assertFalse(policy.canDeleteRecord(GroupPermissionPolicy.ROLE_MEMBER, "member", "other"));
    }
}
