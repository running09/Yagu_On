package com.example.myapplication.domain;

public class StoryOverlayFormatter {
    public String overlayText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "글귀를 입력하세요";
        }
        return text.trim();
    }

    public String mediaSelectedLabel(String mediaType) {
        if ("video".equals(mediaType)) {
            return "선택한 영상에 글귀를 올려보세요";
        }
        return "선택한 사진에 글귀를 올려보세요";
    }
}
