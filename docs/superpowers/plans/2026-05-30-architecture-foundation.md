# Architecture Foundation Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add the architecture foundation for Yagu_On group permissions, inning records, and client-side video upload policy.

**Architecture:** Keep the current Java/XML Android MVP intact while adding a small `domain` layer for policy decisions, new `model` classes for groups/innings/records, and repository contracts for future Firebase implementation. This avoids a risky full package migration and gives the next screens stable APIs.

**Tech Stack:** Android Java, JUnit4 local unit tests, Firebase-ready repository interfaces.

---

### Task 1: Domain Policies

**Files:**
- Create: `app/src/test/java/com/example/myapplication/domain/GroupPermissionPolicyTest.java`
- Create: `app/src/test/java/com/example/myapplication/domain/VideoUploadPolicyTest.java`
- Create: `app/src/main/java/com/example/myapplication/domain/GroupPermissionPolicy.java`
- Create: `app/src/main/java/com/example/myapplication/domain/VideoUploadPolicy.java`

- [ ] **Step 1: Write failing permission policy tests**

```java
@Test
public void ownerCanGrantAdminAndDeleteGroup() {
    GroupPermissionPolicy policy = new GroupPermissionPolicy();
    assertTrue(policy.canGrantAdmin("owner"));
    assertTrue(policy.canDeleteGroup("owner"));
}
```

- [ ] **Step 2: Run permission tests and verify RED**

Run: `./gradlew.bat :app:testDebugUnitTest --tests "com.example.myapplication.domain.GroupPermissionPolicyTest"`

Expected: FAIL because `GroupPermissionPolicy` does not exist.

- [ ] **Step 3: Implement minimal permission policy**

Create `GroupPermissionPolicy` with role constants `owner`, `admin`, `member` and methods for group management, admin grant/revoke, group deletion, inning creation, record write/update/delete.

- [ ] **Step 4: Write failing video upload policy tests**

```java
@Test
public void videoAtFiveSecondsIsAllowed() {
    VideoUploadPolicy policy = new VideoUploadPolicy();
    assertTrue(policy.isAllowedDurationMillis(5000));
    assertFalse(policy.isAllowedDurationMillis(5001));
}
```

- [ ] **Step 5: Run video tests and verify RED**

Run: `./gradlew.bat :app:testDebugUnitTest --tests "com.example.myapplication.domain.VideoUploadPolicyTest"`

Expected: FAIL because `VideoUploadPolicy` does not exist.

- [ ] **Step 6: Implement minimal video policy**

Create `VideoUploadPolicy` with `MAX_VIDEO_DURATION_MILLIS = 5000` and helper methods for duration validation and media type validation.

- [ ] **Step 7: Run domain tests and verify GREEN**

Run: `./gradlew.bat :app:testDebugUnitTest --tests "com.example.myapplication.domain.*"`

Expected: PASS.

### Task 2: Group/Inning Models and Repository Contracts

**Files:**
- Create: `app/src/main/java/com/example/myapplication/model/Group.java`
- Create: `app/src/main/java/com/example/myapplication/model/GroupMember.java`
- Create: `app/src/main/java/com/example/myapplication/model/Inning.java`
- Create: `app/src/main/java/com/example/myapplication/model/InningRecord.java`
- Create: `app/src/main/java/com/example/myapplication/data/GroupRepository.java`
- Create: `app/src/main/java/com/example/myapplication/data/InningRepository.java`

- [ ] **Step 1: Add simple Firebase-mappable model classes**

Create public no-arg model classes with public fields so Firestore `toObject()` can map them.

- [ ] **Step 2: Add repository contracts**

Create `GroupRepository` for group, member, role, and deletion operations. Create `InningRepository` for inning creation and user record operations.

- [ ] **Step 3: Run tests/build**

Run: `./gradlew.bat :app:testDebugUnitTest`

Expected: PASS.

### Task 3: Firebase Implementation Skeleton

**Files:**
- Create: `app/src/main/java/com/example/myapplication/data/firebase/FirebaseGroupRepository.java`
- Create: `app/src/main/java/com/example/myapplication/data/firebase/FirebaseInningRepository.java`
- Create: `app/src/main/java/com/example/myapplication/data/RepositoryProvider.java`

- [ ] **Step 1: Implement Firebase collection paths**

Use these paths:

```text
groups/{groupId}
groups/{groupId}/members/{userId}
groups/{groupId}/innings/{inningId}
groups/{groupId}/innings/{inningId}/records/{userId}
```

- [ ] **Step 2: Reuse policy checks before write operations**

Use `GroupPermissionPolicy` for caller-side checks where caller role is provided.

- [ ] **Step 3: Centralize repository creation**

Use `RepositoryProvider` from Activity and Fragment classes instead of importing Firebase repository implementations directly.

- [ ] **Step 4: Run full app unit tests**

Run: `./gradlew.bat :app:testDebugUnitTest`

Expected: PASS.

### Task 4: Documentation Update

**Files:**
- Modify: `docs/architecture-direction.md`
- Modify: `docs/group-permissions.md`
- Modify: `index.md`

- [ ] **Step 1: Mark implemented architecture foundation**

Add a note that domain policies, models, and repository contracts now exist in code.

- [ ] **Step 2: Run verification**

Run: `./gradlew.bat :app:testDebugUnitTest`

Expected: PASS.
