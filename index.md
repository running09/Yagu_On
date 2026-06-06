# Yagu_On 문서 색인

야구 팬을 위한 팀 기반 온라인 응원 커뮤니티 앱 `Yagu_On` / `응원ON` 프로젝트 문서 색인이다.

## 기획 문서

- [앱 기획서 요약](docs/app-plan.md)
- [프로젝트 결정 기록](docs/project-decisions.md)
- [그룹 권한 및 회차 기록 설계](docs/group-permissions.md)
- [아키텍처 방향 제안](docs/architecture-direction.md)
- [현재 구조와 제안 구조 비교 HTML](docs/architecture-comparison.html)
- [그룹/회차 화면 HTML 미리보기](docs/group-screen-preview.html)
- [앱 전체 화면 HTML 미리보기](docs/app-overall-preview.html)
- [UI MVP 개선 요약](docs/ui-mvp-update-2026-06-02.md)
- [Firebase MVP Rules Update](docs/firebase-mvp-rules-2026-06-04.md)
- [Firebase Storage Rules Update](docs/firebase-storage-rules-2026-06-05.md)
- [Firebase 설정 적용 기록](docs/firebase-settings-applied-2026-06-06.md)
- [Secret Management Update](docs/secret-management-2026-06-06.md)
- [MVP Build Prompt](docs/mvp-build-prompt-2026-06-05.md)
- [사용자용 오류 문구 정리](docs/user-facing-error-copy-2026-06-05.md)
- [v0 Baseball Fan UI Reference](docs/v0-baseball-fan-ui-reference-2026-06-05.md)
- [v0 UI 비교 HTML](docs/v0-ui-comparison-2026-06-05.html)
- [프로젝트 디렉터리/데이터 흐름 HTML](docs/project-directory-data-flow-2026-06-06.html)

## 관련 개념

- [그룹 권한 및 회차 기록 설계](docs/group-permissions.md): owner, admin, member 권한과 회차별 기록 화면 구성.
- [아키텍처 방향 제안](docs/architecture-direction.md): 현재 코드 구조의 진단과 그룹/회차 기능을 위한 권장 아키텍처.
- [UI MVP 개선 요약](docs/ui-mvp-update-2026-06-02.md): 업로드 흐름, 홈 피드 테스트 이미지, 그룹 폼/마이페이지 UI 조정.
- [Firebase MVP Rules Update](docs/firebase-mvp-rules-2026-06-04.md): 실제 기기에서 확인한 그룹 생성 `PERMISSION_DENIED` 원인과 Firestore rules/indexes 정리.
- [Firebase Storage Rules Update](docs/firebase-storage-rules-2026-06-05.md): 그룹 회차 기록 업로드와 게시글 미디어 경로를 위한 Storage rules 정리.
- [Firebase 설정 적용 기록](docs/firebase-settings-applied-2026-06-06.md): `yagu-on` 프로젝트의 Firestore rules/indexes, 기본 Storage 버킷, Storage rules 배포 및 실기기 그룹 생성 검증 결과.
- [Secret Management Update](docs/secret-management-2026-06-06.md): `app/google-services.json`을 Git 추적 대상에서 제외하고, 예시 템플릿만 저장하는 정책.
- [MVP Build Prompt](docs/mvp-build-prompt-2026-06-05.md): 원본 기획서와 현재 블로커를 반영한 MVP 완성 실행 프롬프트.
- [사용자용 오류 문구 정리](docs/user-facing-error-copy-2026-06-05.md): Firebase/Firestore 같은 개발자 표현을 앱 UI에서 숨기기 위한 문구 정책.
- [v0 Baseball Fan UI Reference](docs/v0-baseball-fan-ui-reference-2026-06-05.md): 공개 v0 참고안을 현재 Android MVP 제약에 맞게 번역한 화면 구조 기준.
- [v0 UI 비교 HTML](docs/v0-ui-comparison-2026-06-05.html): 작업 전 캡처, v0 기준 재현 화면, 현재 APK 캡처를 나란히 비교한 시각 검수 페이지.
- [프로젝트 디렉터리/데이터 흐름 HTML](docs/project-directory-data-flow-2026-06-06.html): Android/Firebase 디렉터리별 역할, 분리 이유, 사용자 입력부터 Firestore/Storage까지의 데이터 흐름 설명.

Firebase, Firestore 데이터 구조, RecyclerView 피드, 미디어 업로드 전략처럼 주제가 더 확장되면 새 요약 문서를 추가하고 여기에서 연결한다.

## 충돌 및 변경 기록

- 2026-05-27: 원본 기획서 기준으로 초기 문서화했다.
- 2026-05-30: 앱명을 `Yagu_On`으로 우선 통일하고, 야구 경기/순위 정보는 별도 API 없이 수동 입력 기반으로 우선 구성하는 방향을 기록했다.
- 2026-05-30: 영상 길이는 MVP 구현을 위해 클라이언트에서 5초 이하로 제한하고, 그룹 권한은 owner/admin/member 구조로 정리했다.
- 2026-05-30: 현재 Android/Firebase 코드 구조를 검토하고 그룹/회차/권한 기능을 위한 아키텍처 방향을 제안했다.
- 2026-05-30: `domain` 정책 계층, 그룹/회차 모델, Repository 계약, Firebase 구현체 기반을 추가했다.
- 2026-05-30: 하단 탭에 `그룹` 화면을 연결하고 그룹 목록 조회, 그룹 생성, 그룹 입장 기본 흐름을 구현했다.
- 2026-05-30: 그룹 상세 영역에 그룹원 초대/관리, owner 권한 부여/회수, owner 그룹 삭제, owner/admin 회차 추가, 회차 목록 표시를 연결했다.
- 2026-05-30: 회차 상세 영역에 구성원별 기록 목록, 내 글귀/사진/영상 저장, 내 기록 삭제, owner/admin 기록 삭제를 연결했다.
- 2026-05-30: 구현한 그룹/회차 화면 흐름을 확인할 수 있는 HTML 미리보기를 추가했다.
- 2026-05-30: 회차 목록을 가로 스크롤 카드 형태로 조정하고, 각 회차 카드 안에 구성원별 기록 요약을 함께 표시하도록 변경했다.
- 2026-05-30: 팀 응원탭과 그룹/회차 카메라 변경사항을 한 번에 확인할 수 있는 앱 전체 HTML 미리보기를 추가했다.
- 2026-06-02: MVP 기준으로 업로드는 별도 하단 탭이 아니라 그룹 입장 뒤 회차 기록 안에서만 제공하도록 정리했다. 회차 기록에는 카메라/폴더 선택과 스토리형 글귀 오버레이를 붙이고, 홈 테스트 이미지 카드와 그룹 문구 단순화, 응원탭 입력창 고정을 반영했다.
- 2026-06-04: 마이페이지 상단 팀 선택 칩을 숨기고 그룹 생성 폼을 `응원 그룹 만들기` 버튼 뒤로 이동했다. 실제 기기 검증에서 그룹 생성 실패 원인이 Firestore `PERMISSION_DENIED`임을 확인했고 MVP rules/indexes 파일과 문서를 추가했다.
- 2026-06-05: 원본 기획서와 현재 블로커를 함께 반영한 MVP 완성 실행 프롬프트를 정리했다.
- 2026-06-05: 앱 UI의 Firebase/Firestore/owner/admin 노출을 줄이기 위한 사용자용 오류 문구 정책과 formatter 적용 내용을 문서화했다.
- 2026-06-05: 그룹 회차 기록 업로드와 게시글 미디어 경로에 맞는 Firebase Storage rules를 추가하고 문서화했다.
- 2026-06-05: 공개 v0 `Baseball fan app` 참고안을 현재 응원ON MVP 제약에 맞춰 해석한 UI 기준 문서를 추가했다.
- 2026-06-05: v0 기준 UI 반영 여부를 작업 전/기준/현재 캡처로 비교하는 HTML 검수 페이지를 추가했다. Firebase rules 미배포로 그룹 샘플 카드가 현재 캡처에서 오류 카드로 보이는 차이를 명시했다.
- 2026-06-06: `yagu-on` Firebase 프로젝트에 Firestore rules/indexes와 Storage rules를 배포하고 기본 Storage 버킷을 생성했다. 실기기에서 회원가입, 팀 선택, 그룹 목록 조회, `QASettings` 그룹 생성을 확인했으므로 이전의 rules 미배포 블로커 기록은 배포 전 상태로만 남긴다.
- 2026-06-06: 프로젝트 디렉터리별 역할과 Android 화면 -> Repository -> Firebase -> UI 반영 데이터 흐름을 설명하는 HTML 문서를 추가했다.
- 2026-06-06: GitHub에 노출된 Firebase client config를 제거하기 위해 `app/google-services.json`을 추적 대상에서 제외하고 placeholder 템플릿과 secret 관리 문서를 추가했다.
