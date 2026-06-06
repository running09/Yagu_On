package com.example.myapplication.domain;

public class GroupFormValidator {
    private static final int MAX_GROUP_NAME_LENGTH = 20;

    public Result validateName(String rawName) {
        String value = rawName == null ? "" : rawName.trim();
        if (value.isEmpty()) {
            return Result.invalid("그룹 이름을 입력하세요.");
        }
        if (value.length() > MAX_GROUP_NAME_LENGTH) {
            return Result.invalid("그룹 이름은 20자 이하로 입력하세요.");
        }
        return Result.valid(value);
    }

    public static class Result {
        public final boolean valid;
        public final String value;
        public final String message;

        private Result(boolean valid, String value, String message) {
            this.valid = valid;
            this.value = value;
            this.message = message;
        }

        public static Result valid(String value) {
            return new Result(true, value, "");
        }

        public static Result invalid(String message) {
            return new Result(false, "", message);
        }
    }
}
