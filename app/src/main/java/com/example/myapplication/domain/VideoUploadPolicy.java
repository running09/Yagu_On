package com.example.myapplication.domain;

public class VideoUploadPolicy {
    public static final long MAX_VIDEO_DURATION_MILLIS = 5_000L;
    public static final String MEDIA_TYPE_IMAGE = "image";
    public static final String MEDIA_TYPE_VIDEO = "video";

    public boolean isAllowedVideoDurationMillis(long durationMillis) {
        return durationMillis >= 0 && durationMillis <= MAX_VIDEO_DURATION_MILLIS;
    }

    public boolean canUpload(String mediaType, long durationMillis) {
        if (MEDIA_TYPE_IMAGE.equals(mediaType)) {
            return true;
        }
        if (MEDIA_TYPE_VIDEO.equals(mediaType)) {
            return isAllowedVideoDurationMillis(durationMillis);
        }
        return false;
    }
}
