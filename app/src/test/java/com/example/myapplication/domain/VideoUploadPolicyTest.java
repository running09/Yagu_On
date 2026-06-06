package com.example.myapplication.domain;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class VideoUploadPolicyTest {
    private final VideoUploadPolicy policy = new VideoUploadPolicy();

    @Test
    public void videoAtFiveSecondsIsAllowedButLongerVideoIsRejected() {
        assertTrue(policy.isAllowedVideoDurationMillis(5_000));
        assertFalse(policy.isAllowedVideoDurationMillis(5_001));
    }

    @Test
    public void negativeVideoDurationIsRejected() {
        assertFalse(policy.isAllowedVideoDurationMillis(-1));
    }

    @Test
    public void imagesDoNotNeedVideoDurationValidation() {
        assertTrue(policy.canUpload("image", -1));
        assertTrue(policy.canUpload("image", 60_000));
    }

    @Test
    public void videosUseDurationValidation() {
        assertTrue(policy.canUpload("video", 4_999));
        assertFalse(policy.canUpload("video", 5_001));
    }

    @Test
    public void unknownMediaTypeIsRejected() {
        assertFalse(policy.canUpload("audio", 1_000));
        assertFalse(policy.canUpload(null, 1_000));
    }
}
