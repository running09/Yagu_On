package com.example.myapplication.domain;

import com.example.myapplication.model.Group;
import com.example.myapplication.model.GroupMember;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class GroupRoleResolverTest {
    private final GroupRoleResolver resolver = new GroupRoleResolver();

    @Test
    public void ownerIdWinsOverOtherRoleSources() {
        Group group = new Group();
        group.ownerId = "user-1";
        group.adminIds = Collections.singletonList("user-1");

        assertEquals(GroupPermissionPolicy.ROLE_OWNER, resolver.resolve(group, Collections.emptyList(), "user-1"));
    }

    @Test
    public void adminIdsResolveAdminRole() {
        Group group = new Group();
        group.ownerId = "owner";
        group.adminIds = Collections.singletonList("admin");

        assertEquals(GroupPermissionPolicy.ROLE_ADMIN, resolver.resolve(group, Collections.emptyList(), "admin"));
    }

    @Test
    public void memberDocumentResolvesMemberRole() {
        Group group = new Group();
        group.ownerId = "owner";
        group.adminIds = Collections.emptyList();
        GroupMember member = new GroupMember();
        member.userId = "member";
        member.role = GroupPermissionPolicy.ROLE_MEMBER;

        assertEquals(GroupPermissionPolicy.ROLE_MEMBER, resolver.resolve(group, Collections.singletonList(member), "member"));
    }

    @Test
    public void unknownUserDefaultsToMemberForReadOnlyEntry() {
        Group group = new Group();
        group.ownerId = "owner";
        group.adminIds = Arrays.asList("admin-a", "admin-b");

        assertEquals(GroupPermissionPolicy.ROLE_MEMBER, resolver.resolve(group, Collections.emptyList(), "other"));
    }
}
