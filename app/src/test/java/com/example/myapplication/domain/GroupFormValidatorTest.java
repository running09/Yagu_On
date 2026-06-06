package com.example.myapplication.domain;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GroupFormValidatorTest {
    private final GroupFormValidator validator = new GroupFormValidator();

    @Test
    public void blankGroupNameIsRejected() {
        GroupFormValidator.Result result = validator.validateName("   ");

        assertFalse(result.valid);
        assertEquals("그룹 이름을 입력하세요.", result.message);
    }

    @Test
    public void groupNameIsTrimmed() {
        GroupFormValidator.Result result = validator.validateName("  잠실 직관팟  ");

        assertTrue(result.valid);
        assertEquals("잠실 직관팟", result.value);
    }

    @Test
    public void longGroupNameIsRejected() {
        GroupFormValidator.Result result = validator.validateName("123456789012345678901");

        assertFalse(result.valid);
        assertEquals("그룹 이름은 20자 이하로 입력하세요.", result.message);
    }
}
