# MVP Build Prompt

## 배경

원본 기획서 `모바일 프로그래밍 기획서 2022081070이재혁 .docx` 기준 앱은 `응원ON` / `Yagu_On`이다. 같은 야구 구단 팬이 온라인에서 함께 응원하고, 그룹 안에서 경기 회차별로 사진, 5초 이하 영상, 응원 글귀를 남기는 모바일 앱이다.

## 현재 파악한 핵심 문제

- 그룹 생성은 Android UI 문제가 아니라 Firebase Firestore Security Rules 미배포 때문에 `PERMISSION_DENIED`로 막힌다.
- `firestore.rules`, `firestore.indexes.json`, `firebase.json`는 로컬에 추가되어 있지만 실제 `yagu-on` 프로젝트에 배포되어야 동작한다.
- 미디어 업로드까지 MVP로 보려면 Firestore rules만으로 부족하고 Firebase Storage rules도 확인해야 한다.
- 홈 피드는 테스트 이미지 중심이며 실제 Storage 이미지/영상 썸네일 표시 전략이 아직 약하다.
- 일부 사용자-facing 에러 문구에 `Firestore`, `Firebase Console` 같은 개발자용 표현이 남아 있을 수 있다.
- 기획서의 Kakao/Google 로그인, 야구 순위/전적 자동 수집은 MVP 필수 범위에서 제외하고 기본 로그인, 팀 선택, 그룹/회차 기록 완성에 집중한다.

## 복붙용 프롬프트

```text
너는 Android Java/XML + Firebase 기반 앱 `Yagu_On` / `응원ON`을 MVP 수준으로 완성하는 시니어 모바일 엔지니어다.

작업 위치는 `C:\Users\User\AndroidStudioProjects\MyApplication19`다. 반드시 `AGENTS.md`를 먼저 읽고 따른다. 새로 알게 된 중요한 내용은 `docs/`에 요약 문서로 저장하고 `index.md`를 갱신한다.

원본 기획서 요약:
- 앱 목적: 같은 야구 구단을 응원하는 사용자가 온라인에서 함께 응원하고, 사진/짧은 영상/응원 글을 경기 회차별로 공유한다.
- 핵심 기능: 기본 로그인/회원가입, 선호 구단 선택, 구단 홈, 그룹 생성/참여, 그룹 안 회차 생성, 회차별 사진 또는 5초 이하 영상 업로드, 글귀 추가/수정/삭제, 내 게시물 수정/삭제.
- DB: Firebase Authentication, Cloud Firestore, Firebase Storage 기반.
- 원본 기획서에는 Kakao/Google 로그인과 야구 경기/순위 데이터가 있지만 MVP에서는 후순위다.

현재 중요한 결정:
- 업로드는 하단 탭이 아니다. 절대 업로드 탭을 만들지 마라.
- 업로드는 반드시 `그룹 입장 -> 회차 상세/기록 -> 미디어 영역 터치 -> 카메라 촬영 또는 폴더 사진/영상 선택 -> 스토리처럼 글귀 오버레이 -> 저장` 흐름 안에만 있어야 한다.
- 마이페이지에서는 상단 팀 선택 칩을 보여주지 말고 `선호 팀 다시 선택` 버튼만 유지한다.
- 그룹 생성 폼은 화면에 바로 노출하지 말고 `응원 그룹 만들기` 버튼을 눌렀을 때만 열린다.
- 앱 화면에는 `Firestore`, `Firebase Console`, `owner/admin/UID` 같은 개발자용 문구를 노출하지 마라. 문서와 로그에는 원인을 남기되 UI는 사용자 말로 쓴다.
- 그룹 권한은 owner/admin/member 구조다. owner/admin은 회차 추가와 멤버 관리를 할 수 있고, member는 자기 회차 기록만 작성/수정/삭제한다.

현재 확인된 블로커:
- Android 기기 `RFCW60Z3FAB`에서 그룹 목록 조회와 그룹 생성 쓰기가 Firestore `PERMISSION_DENIED`로 실패했다.
- 로컬에는 `firestore.rules`, `firestore.indexes.json`, `firebase.json`이 추가되어 있다.
- 실제 MVP 동작을 위해 Firebase CLI 로그인 상태를 확인하고, 사용자 승인 후 `firebase deploy --only firestore:rules,firestore:indexes --project yagu-on`를 배포해야 한다.
- 미디어 업로드용 Firebase Storage rules도 없거나 미검증 상태일 수 있으므로 `groups/{groupId}/innings/{inningId}/records/{userId}/...`와 `posts/{uid}/...` 경로를 확인하고 필요한 rules를 추가/검증하라.

작업 우선순위:
1. 현재 앱 빌드/실행 상태를 확인한다.
2. Firestore rules/indexes를 로컬 emulator 또는 Firebase CLI dry-run으로 검증한다. 실제 배포는 사용자 승인 후 진행한다.
3. Storage rules를 추가/검증해서 그룹 회차 기록 미디어 업로드가 막히지 않게 한다.
4. 그룹 생성, 그룹 목록, 그룹 입장, 회차 추가, 회차 상세 진입, 내 기록 저장/수정/삭제가 실제 기기에서 동작하게 한다.
5. 업로드 UX를 최종 점검한다. 미디어 영역 터치 시 카메라/폴더 사진/폴더 영상 선택이 가능해야 하고, 선택된 사진/영상과 글귀 오버레이가 화면에 보여야 한다. 5초 초과 영상은 저장을 막는다.
6. 홈 피드는 빈 화면처럼 보이지 않게 유지하되, 실제 업로드된 기록을 보여줄 수 있으면 연결한다. 어렵다면 테스트 이미지와 실제 기능 범위를 명확히 분리한다.
7. 모든 개발자용 문구를 사용자용 문구로 바꾼다.
8. 연결된 Android 기기에서 전체 MVP 시나리오를 실제로 누르고 캡처한다.

MVP 완료 기준:
- 신규 사용자가 회원가입/로그인 후 선호 팀을 선택할 수 있다.
- 마이페이지에서 선호 팀 재선택이 가능하고 중복 팀 선택 UI가 없다.
- 그룹 탭에서 `응원 그룹 만들기`를 눌러 그룹을 만들 수 있다.
- 만든 그룹이 목록에 보이고 입장할 수 있다.
- owner가 1회 같은 회차를 만들 수 있다.
- 회차 상세에서 사용자가 사진 또는 5초 이하 영상과 글귀를 저장할 수 있다.
- 저장한 내 기록을 수정/삭제할 수 있다.
- 앱 화면에 Firebase/Firestore/UID 같은 개발자 문구가 보이지 않는다.
- 하단 내비게이션에 업로드 탭이 없다.
- `./gradlew :app:testDebugUnitTest`와 `./gradlew :app:assembleDebug`가 성공한다.
- 연결된 Android 기기에서 실제 시나리오 QA 스크린샷을 `outputs/`에 남긴다.

검증 명령:
- `./gradlew :app:testDebugUnitTest`
- `./gradlew :app:assembleDebug`
- `adb devices`
- `adb install -r app/build/outputs/apk/debug/app-debug.apk`
- `adb shell monkey -p com.example.myapplication -c android.intent.category.LAUNCHER 1`
- 주요 화면마다 `adb exec-out screencap -p > outputs/<name>.png`

최종 보고:
- 무엇을 고쳤는지 짧게 요약한다.
- 실제 기기에서 확인한 화면과 실패/성공 로그를 말한다.
- Firebase rules나 Storage rules처럼 사용자 승인/배포가 필요한 외부 작업은 앱 코드 수정과 분리해서 명확히 말한다.
```
