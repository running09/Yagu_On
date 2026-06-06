# Firebase MVP Rules Update

## 요약

2026-06-04 실제 Android 기기 `RFCW60Z3FAB`에서 그룹 화면을 검증했다.

- `응원 그룹 만들기` 버튼을 누르면 새 그룹 이름 입력 폼이 열린다.
- `QAGroup` 입력 후 만들기를 누르면 앱은 Firestore 쓰기를 시도한다.
- Firestore가 `groups/{groupId}` 쓰기를 `PERMISSION_DENIED`로 거절한다.
- 따라서 현재 생성 실패의 남은 원인은 Android UI가 아니라 Firebase Firestore Security Rules 배포 상태다.

## 반영한 클라이언트 기준

- 그룹 문서는 `memberIds` 배열을 가진다.
- 그룹 목록은 `groups.whereArrayContains("memberIds", currentUserId)`로 조회한다.
- 생성자는 `ownerId`이며 동시에 `members/{uid}`의 `owner`로 저장된다.
- 권한 에러의 실제 원인은 문서와 로그에 Firestore rules로 남기되, 앱 화면에는 개발자용 용어를 노출하지 않고 "관리자에게 문의" 흐름으로 보여준다.

## 추가한 Firebase 설정 파일

- [firebase.json](../firebase.json): Firestore rules와 indexes 파일 위치.
- [firestore.rules](../firestore.rules): `users`, `posts`, `cheerRooms`, `cheerSongs`, `groups`, `members`, `innings`, `records`의 MVP rules.
- [firestore.indexes.json](../firestore.indexes.json): 홈 피드 `posts where teamId == ... order by createdAt desc`용 composite index.

## 배포 필요

파일만 추가해도 운영 Firebase 프로젝트에는 적용되지 않는다.

`yagu-on` 프로젝트에 로그인된 Firebase CLI 환경에서 다음 배포가 필요하다.

```bash
firebase deploy --only firestore:rules,firestore:indexes --project yagu-on
```

배포 전에는 Firebase Console에서 기존 rules와 충돌하지 않는지 확인해야 한다.

## 관련 기존 문서

- [그룹 권한 및 회차 기록 설계](group-permissions.md)
- [UI MVP 개선 요약](ui-mvp-update-2026-06-02.md)

## 2026-06-06 후속 상태

Firestore rules/indexes는 `yagu-on` 프로젝트에 배포됐다. 실제 Android 기기 `RFCW60Z3FAB`에서 회원가입, 선호 구단 선택, 그룹 목록 조회, `QASettings` 그룹 생성을 확인했으므로 이 문서의 `PERMISSION_DENIED`는 배포 전 상태의 원인 기록으로 본다.

자세한 배포/검증 기록은 [Firebase 설정 적용 기록](firebase-settings-applied-2026-06-06.md)에 남겼다.
