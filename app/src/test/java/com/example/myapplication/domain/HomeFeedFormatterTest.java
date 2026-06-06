package com.example.myapplication.domain;

import com.example.myapplication.model.Post;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HomeFeedFormatterTest {
    private final HomeFeedFormatter formatter = new HomeFeedFormatter();

    @Test
    public void postBodyDoesNotExposeStorageUrl() {
        Post post = new Post();
        post.caption = "오늘 응원석 분위기 좋습니다.";
        post.mediaUrl = "https://storage.example.com/raw-upload.jpg";

        String body = formatter.body(post);

        assertEquals("오늘 응원석 분위기 좋습니다.", body);
        assertFalse(body.contains("https://"));
        assertFalse(body.contains("storage.example.com"));
    }

    @Test
    public void mediaSummaryUsesKoreanLabels() {
        Post imagePost = new Post();
        imagePost.mediaType = "image";
        imagePost.likeCount = 12;

        Post videoPost = new Post();
        videoPost.mediaType = "video";
        videoPost.likeCount = 3;

        assertEquals("사진 · 좋아요 12", formatter.mediaSummary(imagePost));
        assertEquals("영상 · 좋아요 3", formatter.mediaSummary(videoPost));
    }

    @Test
    public void sampleFeedCopyLooksLikeUserContent() {
        assertTrue(formatter.sampleCaption().contains("응원"));
        assertFalse(formatter.sampleCaption().contains("Firestore"));
        assertFalse(formatter.sampleCaption().contains("컬렉션"));
    }

    @Test
    public void teamHomeSummaryMatchesV0ReferenceCopy() {
        assertEquals("6위 / 10개 구단", formatter.rankingSummary("lotte"));
        assertEquals("패 · 패 · 승 · 패 · 승", formatter.recentRecordSummary("lotte"));
        assertEquals("롯데 4:6 KIA", formatter.lastGameSummary("lotte", "롯데 자이언츠"));
    }
}
