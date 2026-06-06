# Firebase Storage Rules Update

## 요약

2026-06-05 기준 앱의 실제 Firebase Storage 업로드 경로를 코드에서 다시 확인했다.

- 그룹 회차 기록 미디어 업로드 경로는 `groups/{groupId}/innings/{inningId}/records/{userId}/{timestamp}.jpg|.mp4`다.
- 홈 피드 게시글 미디어 업로드 경로는 `posts/{uid}/{timestamp}.jpg|.mp4`다.
- 따라서 Firestore rules만 배포해도 그룹 생성은 풀 수 있지만, 회차 기록 업로드와 홈 피드 미디어 업로드까지 MVP로 보려면 Storage rules가 추가로 필요하다.

## 코드 기준 경로

- [FirebaseInningRepository](../app/src/main/java/com/example/myapplication/data/firebase/FirebaseInningRepository.java): 그룹 회차 기록 미디어 업로드 경로를 만든다.
- [FirebasePostRepository](../app/src/main/java/com/example/myapplication/data/firebase/FirebasePostRepository.java): 홈 피드 게시글 미디어 업로드 경로를 만든다.

## 추가한 Storage rules

- [storage.rules](../storage.rules): 기본 deny 규칙 위에 두 경로만 허용했다.
- [firebase.json](../firebase.json): Storage rules 파일 경로를 함께 연결했다.

허용 정책은 다음과 같다.

- `groups/{groupId}/innings/{inningId}/records/{userId}/...`
  - 읽기: 해당 그룹의 `memberIds`에 포함된 로그인 사용자만 가능
  - 쓰기/수정/삭제: 해당 그룹 구성원이면서 경로의 `userId`가 자기 UID와 같을 때만 가능
- `posts/{uid}/...`
  - 읽기: 로그인 사용자면 가능
  - 쓰기/수정/삭제: 경로의 `uid`가 자기 UID와 같을 때만 가능
- 업로드 파일은 `image/*` 또는 `video/*` content type만 허용한다.

## Firestore 연동 근거

Storage rules는 Firestore의 `groups/{groupId}` 문서를 읽어 `memberIds`를 확인한다. 따라서 그룹 멤버십 판단은 앱 코드가 아니라 Firebase 보안 규칙에서 다시 강제된다.

Firebase 공식 문서도 Cloud Storage rules에서 Firestore 문서를 읽는 `firestore.get()` / `firestore.exists()` 패턴과 `request.resource.contentType` 검증을 지원한다고 안내한다.

## 남은 외부 작업

이 변경은 로컬 파일에만 반영됐다. 실제 `yagu-on` Firebase 프로젝트에 적용하려면 rules 배포가 필요하다.

```bash
firebase deploy --only firestore:rules,firestore:indexes,storage --project yagu-on
```

현재 작업 환경에서는 `firebase` CLI가 설치되어 있지 않아 로컬 dry-run 검증은 하지 못했다. 운영 반영 전에는 Firebase CLI 로그인 상태와 기존 Storage rules 충돌 여부를 함께 확인해야 한다.

## 2026-06-06 후속 상태

격리된 npm cache로 `firebase-tools@15.19.1`을 실행해 Storage rules를 `yagu-on` 프로젝트에 배포했다. 기본 Storage 버킷 `yagu-on.firebasestorage.app`도 `ASIA-NORTHEAST3` 리전에 생성됐다.

자세한 배포/검증 기록은 [Firebase 설정 적용 기록](firebase-settings-applied-2026-06-06.md)에 남겼다.

## 기존 주장과의 충돌 표시

- 2026-06-04 문서는 그룹 생성 실패의 직접 원인을 Firestore rules 배포 상태로 정리했다. 그 판단은 여전히 맞다.
- 다만 미디어 업로드까지 포함한 MVP 완료 기준에서는 Firestore rules만으로 충분하지 않다. Storage rules도 함께 배포해야 한다.

## 관련 문서

- [Firebase MVP Rules Update](firebase-mvp-rules-2026-06-04.md)
- [그룹 권한 및 회차 기록 설계](group-permissions.md)
- [MVP Build Prompt](mvp-build-prompt-2026-06-05.md)
