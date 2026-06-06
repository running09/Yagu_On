# Yagu_On 앱 기획서 요약

## 출처

- 원본 문서: `모바일 프로그래밍 기획서 2022081070이재혁 .docx`
- 작성 기준일: 2026-05-27

## 앱 개요

`Yagu_On`은 같은 야구 구단을 응원하는 사용자가 온라인에서 함께 응원하고, 사진, 짧은 영상, 응원 글을 회차별로 공유하는 모바일 앱이다. 오프라인 직관이 어려운 상황에서도 팀 팬끼리 소속감과 현장감을 느낄 수 있게 하는 것이 핵심 목표다.

## 핵심 사용자 경험

사용자는 로그인 후 응원 구단을 선택한다. 선택한 구단을 기준으로 순위, 최근 전적, 전 경기 결과, 선발 선수, 주요 경기 데이터를 확인하고, 같은 구단 팬들과 그룹 커뮤니티에 참여한다. 경기 중에는 1회, 2회, 3회처럼 회차별로 사진, 5초 이하 영상, 응원 글을 업로드하고 수정할 수 있다.

## 주요 기능

- 응원 구단 선택: 사용자가 선호 구단을 고르고 팀 중심 화면을 제공한다.
- 구단 정보 제공: 순위, 최근 전적, 전 경기 결과, 선발 선수, 주요 경기 데이터를 보여준다.
- 그룹 커뮤니티: 같은 구단 팬들이 그룹을 만들고 참여한다.
- 회차별 응원 기록: owner 또는 admin이 야구 1회, 2회, 3회 같은 회차를 추가하고, 구성원은 회차별 사진, 5초 이하 영상, 응원 글을 저장한다.
- 게시물 수정: 사용자가 올린 글과 미디어 게시물을 수정한다.
- 로그인: 초기 구현 범위에서는 소셜 로그인 연동 없이 기본 로그인 흐름을 사용한다.

## 화면 설계

- 로그인 화면: ID/PW 입력, 계정 찾기, 회원가입.
- 로그인 실패 처리: 실패 확인 모달과 회원가입 안내 모달.
- 메인 화면: 상단에 응원 구단 선택 영역 배치, 구단 로고 카드를 슬라이드 형태로 제공.
- 구단 홈 화면: 순위, 최근 전적, 전 경기 내용을 카드 형태로 표시.
- 그룹 화면: 그룹 들어가기, 그룹 만들기, 그룹 목록 제공.
- 그룹 관리 화면: 그룹원 초대, owner/admin 전용 그룹원 관리, owner 전용 권한 부여와 그룹 삭제 기능 제공.
- 회차 화면: owner/admin 회차 추가, 그룹 상세 내 회차 목록 제공.
- 회차 상세 화면: 구성원별 사진/영상/글귀 기록을 표시하고, 자기 기록 작성/수정/삭제와 owner/admin 기록 삭제를 지원.
- 그룹 피드 화면: RecyclerView 기반 세로 리스트로 회차별 게시물을 표시하는 방향으로 확장.
- 회차 상세 화면: 구성원 목록을 기준으로 각 구성원의 짧은 영상 또는 사진, 글귀를 리스트 형태로 표시하고 글귀 삽입, 수정, 삭제를 지원.

## 데이터 설계

Firebase Cloud Firestore 기반의 서버리스 DB를 사용한다. 사용자, 구단, 경기, 그룹, 회차, 게시물 정보를 분리해 저장한다.

주요 컬렉션:

- `/userIds`
- `/users`
- `/teams`
- `/teams/{teamId}/seasonStats`
- `/games`
- `/groups`
- `/groups/{groupId}/members`
- `/groups/{groupId}/innings`
- `/groups/{groupId}/posts`

## 개발 환경

- SDK: Android SDK, Firebase SDK
- 언어: Java, XML
- 서버: Firebase Serverless Architecture, Cloud Functions
- DBMS: Cloud Firestore
- Storage: Firebase Cloud Storage
- Authentication: Firebase Authentication
- Design Tool: Figma
- 외부 데이터: 야구 경기 및 순위 정보는 별도 API 없이 웹 크롤링으로 수집
- IDE: Android Studio

## 일정

- 1주차: 요구사항 정리, UX 와이어프레임, DB/ERD 설계
- 2주차: Firebase 연동, 로그인, 회원가입 구현
- 3주차: 구단 선택, 구단 홈, 순위, 전적, 전 경기 화면 구현
- 4주차: 그룹 생성, 그룹 참여, 그룹 목록 화면 구현
- 5주차: 회차별 피드, 사진, 5초 영상 업로드, 게시물 수정 기능 구현
- 6주차: 오류 수정, UI 보완, 발표 자료 및 최종 제출본 정리

## 현재 코드와의 대응

현재 프로젝트에는 로그인, 회원가입, 팀 선택, 홈, 응원, 그룹, 응원가, 프로필 화면 구조가 존재한다. 업로드는 별도 하단 탭이 아니라 그룹 입장 후 회차 기록 안에서 제공한다. Firebase 관련 Repository와 모델도 분리되어 있어 기획서의 서버리스 Firebase 구조와 방향이 맞다.

확인된 주요 코드 영역:

- 앱 진입 및 인증: `AuthGateActivity`, `LoginActivity`, `SignupActivity`
- 메인 화면: `MainActivity`, `MainScreenHost`, 하단 내비게이션
- 기능 화면: `HomeFragment`, `CheerFragment`, `GroupsFragment`, `SongsFragment`, `ProfileFragment`
- 그룹 화면: `GroupsFragment`
- 그룹 역할 계산: `GroupRoleResolver`
- 회차 기록 입력 검증: `InningRecordFormValidator`
- 데이터 계층: `data`, `data/firebase`
- 모델: `model`
- 팀 정보: `TeamCatalog`

## 확정된 항목

- 앱 표시 이름은 우선 `Yagu_On`으로 통일한다.
- 야구 경기 및 순위 데이터는 별도 API를 사용하지 않고 웹 크롤링으로 가져온다.
- 로그인 연동은 초기 범위에서 제외한다. Kakao, Google 로그인 SDK 연동은 일단 구현하지 않는다.
- 5초 이하 영상 업로드 제한은 빠른 구현을 위해 클라이언트에서 우선 처리한다. 추후 실제 서비스 확장 시 서버 검증을 추가할 수 있다.
- 그룹 권한은 owner/admin/member 구조로 설계한다. 자세한 내용은 [그룹 권한 및 회차 기록 설계](group-permissions.md)를 기준으로 한다.

## 아직 결정이 필요한 항목

- Firestore Security Rules를 실제 Firebase 프로젝트에 어디까지 적용할지 정해야 한다.

## 충돌 표시

- 현재 `strings.xml`의 일부 한글 문자열이 깨져 보인다. 기획서의 화면명과 실제 리소스 문자열을 맞추는 작업이 필요하다.
- 원본 기획서의 앱명은 `응원ON`이지만 현재 기준 앱 이름은 `Yagu_On`이다. Android 리소스와 프로젝트 설정은 `Yagu_On` 기준으로 맞췄다.
