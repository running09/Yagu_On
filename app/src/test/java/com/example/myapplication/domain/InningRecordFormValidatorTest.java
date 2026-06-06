package com.example.myapplication.domain;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class InningRecordFormValidatorTest {
    private final InningRecordFormValidator validator = new InningRecordFormValidator();

    @Test
    public void emptyTextAndNoMediaIsRejected() {
        InningRecordFormValidator.Result result = validator.validate("   ", false);

        assertFalse(result.valid);
        assertEquals("글귀나 사진/영상을 하나 이상 입력하세요.", result.message);
    }

    @Test
    public void textOnlyRecordIsAllowedAndTrimmed() {
        InningRecordFormValidator.Result result = validator.validate("  역전 가자  ", false);

        assertTrue(result.valid);
        assertEquals("역전 가자", result.text);
    }

    @Test
    public void mediaOnlyRecordIsAllowed() {
        InningRecordFormValidator.Result result = validator.validate("", true);

        assertTrue(result.valid);
        assertEquals("", result.text);
    }
}
