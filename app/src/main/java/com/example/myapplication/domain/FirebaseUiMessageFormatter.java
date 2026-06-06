package com.example.myapplication.domain;

public final class FirebaseUiMessageFormatter {
    private FirebaseUiMessageFormatter() {
    }

    public static String loginRequired() {
        return "로그인이 필요합니다.";
    }

    public static String signupProfileUnavailable() {
        return "회원 정보를 준비하지 못했습니다. 잠시 후 다시 시도해 주세요.";
    }

    public static String authFailure(String raw) {
        if (raw == null || raw.isEmpty()) {
            return "로그인에 문제가 생겼습니다. 잠시 후 다시 시도해 주세요.";
        }
        if (raw.contains("CONFIGURATION_NOT_FOUND")) {
            return "로그인 준비가 아직 끝나지 않았습니다. 잠시 후 다시 시도해 주세요.";
        }
        return raw;
    }

    public static String createProfileTimeout() {
        return "가입 정보를 저장하는 데 시간이 걸리고 있습니다. 잠시 후 다시 시도해 주세요.";
    }

    public static String loadProfileTimeout() {
        return "내 정보를 불러오는 데 시간이 걸리고 있습니다. 잠시 후 다시 시도해 주세요.";
    }

    public static String saveFavoriteTeamTimeout() {
        return "선호 팀을 저장하는 데 시간이 걸리고 있습니다. 잠시 후 다시 시도해 주세요.";
    }

    public static String userProfileMissing() {
        return "내 정보를 찾지 못했습니다. 다시 로그인해 주세요.";
    }

    public static String firebaseDataFailure(String raw) {
        if (raw == null || raw.isEmpty()) {
            return "요청을 처리하지 못했습니다. 잠시 후 다시 시도해 주세요.";
        }
        if (raw.contains("PERMISSION_DENIED")) {
            return "지금은 이 요청을 처리할 수 없습니다. 잠시 후 다시 시도해 주세요.";
        }
        if (raw.contains("NOT_FOUND") || raw.contains("Cloud Firestore API")) {
            return "서비스 준비가 아직 끝나지 않았습니다. 잠시 후 다시 시도해 주세요.";
        }
        return raw;
    }

    public static String groupPermissionDenied() {
        return "이 그룹 작업을 진행할 권한이 없습니다.";
    }

    public static String groupRoleChangeNotAllowed() {
        return "그룹 운영 역할은 여기까지 조정할 수 있습니다.";
    }
}
