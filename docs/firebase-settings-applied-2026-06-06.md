# Firebase 설정 적용 기록 (2026-06-06)

## 요약

`yagu-on` Firebase 프로젝트에 MVP 실행에 필요한 기본 설정을 적용하고 실제 Android 기기에서 확인했다.

- [.firebaserc](../.firebaserc): 기본 Firebase 프로젝트를 `yagu-on`으로 고정했다.
- [firebase.json](../firebase.json): Firestore rules/indexes와 Storage rules 배포 경로를 사용한다.
- [app/google-services.json](../app/google-services.json): Android 패키지 `com.example.myapplication`과 Firebase 프로젝트 `yagu-on` 연결이 유지된다.
- [app/build.gradle.kts](../app/build.gradle.kts): `namespace`와 `applicationId`도 `com.example.myapplication`으로 확인했다.
- Firestore `(default)` 데이터베이스는 `asia-northeast3` 리전의 Native mode로 확인했다.
- [firestore.rules](../firestore.rules)와 [firestore.indexes.json](../firestore.indexes.json)을 실제 프로젝트에 배포했다.
- 기본 Firebase Storage 버킷 `yagu-on.firebasestorage.app`을 `ASIA-NORTHEAST3` 리전에 만들었다.
- [storage.rules](../storage.rules)를 실제 프로젝트에 배포했다.

Firebase CLI는 전역 설치 대신 격리된 npm cache `outputs/npm-cache-firebase`로 `firebase-tools@15.19.1`을 실행했다. 전역 npx cache가 깨져 있었기 때문에 프로젝트 밖 npm cache는 건드리지 않았다.

## 검증 결과

- Firebase CLI 로그인 계정과 `yagu-on` 프로젝트 접근을 확인했다.
- Android 앱 `Yagu_ON`, 앱 ID `1:1033840671036:android:e49295acc76b6dfd990639`, 패키지 `com.example.myapplication`을 확인했다.
- 로컬 rules 검증은 Firebase Emulator Suite로 통과했다. CLI 15 계열은 Java 21 이상이 필요해서 `C:\Users\User\.jdks\openjdk-23`을 사용했다.
- Firestore rules/indexes 배포가 완료됐다.
- Storage 기본 버킷 생성 후 Storage rules 배포가 완료됐다.
- 실제 기기 `RFCW60Z3FAB`에서 회원가입, 선호 구단 선택, 그룹 목록 조회, 그룹 생성 흐름을 확인했다.

실기기 캡처:

- [그룹 목록 조회 정상](../outputs/qa-groups-firestore-ok.png)
- [그룹 생성 후 상세/초대 영역](../outputs/qa-group-create-after-firebase-settings-2.png)
- [그룹 생성 폼과 실제 그룹 카드](../outputs/qa-v0-groups-form-firebase-ok.png)

## 기존 문서와의 충돌/후속 상태

- [Firebase MVP Rules Update](firebase-mvp-rules-2026-06-04.md)의 `PERMISSION_DENIED` 분석은 배포 전 상태로는 맞았다. 2026-06-06 기준 Firestore rules/indexes 배포 후 그룹 생성과 목록 조회는 실제 기기에서 통과했다.
- [Firebase Storage Rules Update](firebase-storage-rules-2026-06-05.md)의 "배포 필요"와 "CLI 미설치로 검증 못함"은 후속 작업으로 해소됐다. 현재는 격리된 npx cache로 Firebase CLI를 실행했고 Storage rules까지 배포했다.
- [MVP Build Prompt](mvp-build-prompt-2026-06-05.md)에 적힌 Firebase rules 미배포 블로커는 더 이상 현재 블로커가 아니다.
- [v0 UI 비교 HTML](v0-ui-comparison-2026-06-05.html)의 그룹 오류 캡처 설명은 최신 Firebase 설정 적용 후 실제 그룹 카드 캡처 기준으로 갱신했다.

## 남은 주의사항

- 검증 과정에서 Firebase Auth 테스트 계정 `qa20260605193323@example.com`과 그룹 `QASettings`가 생성됐다.
- Storage rules는 배포됐지만, 실제 사진/영상 업로드까지는 이번 검증 범위에 포함하지 않았다.
- 기본 Storage 버킷 생성은 Firebase Storage REST API의 `projects.defaultBucket.create` 흐름으로 처리했다. 관련 공식 문서: [Firebase Storage REST defaultBucket.create](https://firebase.google.com/docs/reference/rest/storage/rest/v1alpha/projects.defaultBucket/create)
