package com.example.myapplication.domain;

public class InningRecordFormValidator {
    public Result validate(String rawText, boolean hasMedia) {
        String text = rawText == null ? "" : rawText.trim();
        if (text.isEmpty() && !hasMedia) {
            return Result.invalid("글귀나 사진/영상을 하나 이상 입력하세요.");
        }
        return Result.valid(text);
    }

    public static class Result {
        public final boolean valid;
        public final String text;
        public final String message;

        private Result(boolean valid, String text, String message) {
            this.valid = valid;
            this.text = text;
            this.message = message;
        }

        public static Result valid(String text) {
            return new Result(true, text, "");
        }

        public static Result invalid(String message) {
            return new Result(false, "", message);
        }
    }
}
