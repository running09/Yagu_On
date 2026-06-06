package com.example.myapplication.domain;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StoryOverlayFormatterTest {
    private final StoryOverlayFormatter formatter = new StoryOverlayFormatter();

    @Test
    public void blankTextShowsPromptForStoryOverlay() {
        assertEquals("글귀를 입력하세요", formatter.overlayText(""));
        assertEquals("글귀를 입력하세요", formatter.overlayText("   "));
    }

    @Test
    public void enteredTextIsTrimmedForOverlay() {
        assertEquals("오늘도 끝까지 응원!", formatter.overlayText("  오늘도 끝까지 응원!  "));
    }

    @Test
    public void selectedMediaLabelIsStoryFocused() {
        assertEquals("선택한 사진에 글귀를 올려보세요", formatter.mediaSelectedLabel("image"));
        assertEquals("선택한 영상에 글귀를 올려보세요", formatter.mediaSelectedLabel("video"));
    }
}
