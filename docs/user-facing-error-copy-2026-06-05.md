# 사용자용 오류 문구 정리 (2026-06-05)

## 요약

- 앱 화면과 토스트에 `Firebase`, `Firestore`, `Firebase Console`, `Rules`, `owner`, `admin`, `UID` 같은 개발자용 표현이 노출되지 않도록 정리했다.
- 인증, 사용자 프로필, 선호 팀 저장, 그룹 권한 관련 실패 문구는 모두 사용자 중심 문장으로 통일한다.
- 원인 추적이 필요한 기술 정보는 문서와 로그에 남기고, UI에는 재시도 안내나 권한 부족 같은 결과만 보여준다.

## 이번 변경에서 확정한 원칙

1. 사용자에게는 원인 추정이 아니라 다음 행동을 안내한다.
2. Firebase 설정 단계나 콘솔 메뉴 경로는 앱 UI에 직접 쓰지 않는다.
3. 그룹 역할도 내부 코드 이름(`owner`, `admin`) 대신 자연어 표현으로 바꾼다.

## 적용 범위

- `FirebaseAuthRepository`
- `FirebaseUserRepository`
- `FirebaseGroupRepository`
- `FirebaseUiMessageFormatter`

## 관련 문서

- [MVP Build Prompt](mvp-build-prompt-2026-06-05.md)
- [Firebase MVP Rules Update](firebase-mvp-rules-2026-06-04.md)
- [그룹 권한 및 회차 기록 설계](group-permissions.md)

## 충돌 여부

- 현재 문서는 [MVP Build Prompt](mvp-build-prompt-2026-06-05.md)의 "개발자용 문구를 앱 화면에 노출하지 말 것" 지침을 구체화한 것이다.
- 기존 문서와 직접 충돌하는 내용은 아직 확인되지 않았다.
