package com.example.myapplication.domain;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class FirebaseUiMessageFormatterTest {
    @Test
    public void firebaseDataFailureHidesConsoleInstructions() {
        String message = FirebaseUiMessageFormatter.firebaseDataFailure(
                "PERMISSION_DENIED: open Firebase Console > Firestore Database > Rules");

        assertEquals("지금은 이 요청을 처리할 수 없습니다. 잠시 후 다시 시도해 주세요.", message);
        assertFalse(message.contains("Firebase"));
        assertFalse(message.contains("Firestore"));
        assertFalse(message.contains("Rules"));
    }

    @Test
    public void authFailureHidesConfigurationSteps() {
        String message = FirebaseUiMessageFormatter.authFailure(
                "CONFIGURATION_NOT_FOUND: Firebase Authentication setup missing");

        assertEquals("로그인 준비가 아직 끝나지 않았습니다. 잠시 후 다시 시도해 주세요.", message);
        assertFalse(message.contains("Firebase"));
        assertFalse(message.contains("Authentication"));
    }

    @Test
    public void groupRoleChangeMessageAvoidsInternalRoleNames() {
        String message = FirebaseUiMessageFormatter.groupRoleChangeNotAllowed();

        assertEquals("그룹 운영 역할은 여기까지 조정할 수 있습니다.", message);
        assertFalse(message.contains("owner"));
        assertFalse(message.contains("admin"));
        assertFalse(message.contains("UID"));
    }
}
