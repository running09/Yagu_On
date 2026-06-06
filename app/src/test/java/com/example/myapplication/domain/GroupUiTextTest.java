package com.example.myapplication.domain;

import com.example.myapplication.GroupsFragment;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GroupUiTextTest {
    @Test
    public void roleLabelUsesFriendlyKoreanCopy() {
        assertEquals("그룹 만든 사람", GroupsFragment.roleLabel(GroupPermissionPolicy.ROLE_OWNER));
        assertEquals("운영 도우미", GroupsFragment.roleLabel(GroupPermissionPolicy.ROLE_ADMIN));
        assertEquals("그룹원", GroupsFragment.roleLabel(GroupPermissionPolicy.ROLE_MEMBER));
        assertEquals("그룹원", GroupsFragment.roleLabel(null));
    }

    @Test
    public void mediaLabelUsesUserFacingKoreanCopy() {
        assertEquals("사진 기록", GroupsFragment.mediaLabel("image"));
        assertEquals("영상 기록", GroupsFragment.mediaLabel("video"));
        assertEquals("미디어 기록", GroupsFragment.mediaLabel("audio"));
        assertEquals("미디어 없음", GroupsFragment.mediaLabel(""));
    }
}
