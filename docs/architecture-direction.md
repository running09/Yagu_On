# 아키텍처 방향 제안

## 현재 코드 판단

현재 코드는 빠르게 Firebase 기반 MVP를 만드는 데는 나쁘지 않다. Repository 인터페이스와 Firebase 구현체가 나뉘어 있고, Activity와 Fragment도 화면 단위로 분리되어 있다.

하지만 지금 문서에서 확정한 `그룹 -> 회차 -> 구성원 기록` 구조를 얹기에는 현재의 `posts` 중심 구조가 맞지 않는다. 지금 상태로 계속 기능을 붙이면 화면마다 Firebase 호출, 권한 판단, 업로드 검증, 오류 처리가 흩어질 가능성이 크다.

## 핵심 문제

- Activity와 Fragment가 Repository 구현체를 직접 생성한다.
- 화면 코드가 UI 구성, 입력 검증, Firebase 요청, 이동 처리를 동시에 맡고 있다.
- `posts` 컬렉션은 팀별 피드에는 단순하지만, 그룹/회차/구성원별 기록 요구와 맞지 않는다.
- owner/admin/member 권한 판단을 한 곳에서 관리하는 구조가 없다.
- 영상 5초 제한, 게시글 삭제 기준, 회차 추가 권한 같은 정책이 흩어질 수 있다.
- `LinearLayout`에 카드 View를 계속 추가하는 방식은 목록 기능이 커지면 RecyclerView보다 불리하다.
- 야구 경기/순위 웹 크롤링 데이터가 들어올 위치가 아직 없다.

## 권장 방향

이번 프로젝트는 과제 범위를 생각하면 MVVM까지 무겁게 갈 필요는 없다. 대신 다음과 같은 가벼운 계층형 구조를 추천한다.

```text
ui
- Activity, Fragment, RecyclerView Adapter

domain
- GroupPermissionPolicy
- VideoUploadPolicy
- UseCase 또는 Service

data
- Repository interface
- firebase 구현체
- crawler/cache 데이터 소스

model
- UserProfile, Group, GroupMember, Inning, InningRecord
```

핵심은 화면이 직접 권한을 판단하지 않게 하는 것이다. 예를 들어 `owner만 그룹 삭제 가능`, `owner/admin만 회차 추가 가능`, `member는 자기 기록만 수정 가능` 같은 판단은 `GroupPermissionPolicy` 한 곳에서 처리한다.

## 구현 반영

2026-05-30 기준 다음 아키텍처 기반 코드를 추가했다.

- `domain/GroupPermissionPolicy`: owner/admin/member 권한 판단.
- `domain/VideoUploadPolicy`: 5초 이하 영상 업로드 정책.
- `model/Group`, `GroupMember`, `Inning`, `InningRecord`: 그룹, 구성원, 회차, 회차 기록 모델.
- `data/GroupRepository`, `InningRepository`: 그룹/회차 기능 Repository 계약.
- `data/firebase/FirebaseGroupRepository`, `FirebaseInningRepository`: 문서화한 Firestore 경로 기준 Firebase 구현체.
- `data/RepositoryProvider`: 기존 화면이 Firebase 구현체를 직접 생성하지 않도록 Repository 생성 지점 중앙화.
- `GroupsFragment`: 하단 `그룹` 탭에서 그룹 목록 조회, 그룹 생성, 그룹 입장, 회차 기록 미디어 선택 흐름 구현.
- `GroupsFragment`: 회차 기록에서 영상 선택 시 `VideoUploadPolicy`를 사용해 5초 초과 영상을 클라이언트에서 차단.
- `GroupRoleResolver`: 현재 사용자 uid와 그룹/멤버 데이터를 기준으로 owner/admin/member 역할 계산.
- `GroupsFragment` 상세 영역: 그룹원 초대, 그룹원 관리, owner 권한 부여/회수, owner 그룹 삭제, owner/admin 회차 추가, 회차 목록 표시.
- `InningRecordFormValidator`: 회차 기록 저장 시 글귀 또는 미디어 중 하나 이상 입력되도록 검증.
- `FirebaseInningRepository`: 회차 기록 미디어를 Firebase Storage에 업로드한 뒤 records 문서에 저장하는 메서드 추가.
- `GroupsFragment` 회차 상세 영역: 구성원별 기록 목록, 내 기록 작성/수정/삭제, owner/admin 기록 삭제 연결.
- `GroupsFragment` 그룹 상세 회차 목록: 회차 카드를 가로 스크롤로 보여주고, 각 카드 안에 구성원별 기록 요약을 함께 표시.

아직 회차 상세 화면은 `GroupsFragment` 내부 동적 View 구성이다. 구성원 수가 많아지면 RecyclerView 기반 전용 Fragment로 분리하는 것이 다음 리팩터링 방향이다.

## 데이터 구조 방향

기존 `posts` 중심 구조는 유지하더라도, 그룹 회차 기록 기능은 별도 구조로 분리하는 것이 좋다.

```text
groups/{groupId}
groups/{groupId}/members/{userId}
groups/{groupId}/innings/{inningId}
groups/{groupId}/innings/{inningId}/records/{userId}
```

이 구조가 지금 화면 설계와 가장 잘 맞는다. 회차 상세 화면에서 구성원 목록을 보여주고, 각 구성원별 사진/영상/글귀 기록을 붙이기 쉽다.

## 웹 크롤링 방향

야구 경기 및 순위 정보는 앱 화면에서 바로 웹을 긁어오는 구조로 만들면 위험하다. 사이트 구조가 바뀌면 앱이 바로 깨지고, 네트워크가 느리면 화면도 느려진다.

가능하면 크롤링 결과를 Firestore에 저장하고, 앱은 Firestore의 정리된 데이터만 읽는 방향이 좋다. 빠른 구현이 필요하면 수동 업데이트나 간단한 관리자용 스크립트로 데이터를 넣고, 나중에 Cloud Functions나 별도 크롤러로 자동화할 수 있다.

## HTML 비교 보고서

- [현재 구조와 제안 구조 비교 HTML](architecture-comparison.html)
