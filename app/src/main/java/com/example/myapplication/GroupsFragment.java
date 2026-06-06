package com.example.myapplication;

import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.FileProvider;

import com.example.myapplication.data.AppCallback;
import com.example.myapplication.data.GroupRepository;
import com.example.myapplication.data.InningRepository;
import com.example.myapplication.data.ListCallback;
import com.example.myapplication.data.RepositoryProvider;
import com.example.myapplication.domain.GroupFormValidator;
import com.example.myapplication.domain.GroupPermissionPolicy;
import com.example.myapplication.domain.GroupRoleResolver;
import com.example.myapplication.domain.InningRecordFormValidator;
import com.example.myapplication.domain.StoryOverlayFormatter;
import com.example.myapplication.domain.VideoUploadPolicy;
import com.example.myapplication.model.Group;
import com.example.myapplication.model.GroupMember;
import com.example.myapplication.model.Inning;
import com.example.myapplication.model.InningRecord;
import com.example.myapplication.model.Team;
import com.example.myapplication.util.Ui;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupsFragment extends BaseMainFragment {
    private final GroupRepository groupRepository = RepositoryProvider.groups();
    private final InningRepository inningRepository = RepositoryProvider.innings();
    private final GroupFormValidator groupFormValidator = new GroupFormValidator();
    private final GroupPermissionPolicy permissionPolicy = new GroupPermissionPolicy();
    private final GroupRoleResolver roleResolver = new GroupRoleResolver();
    private final InningRecordFormValidator inningRecordFormValidator = new InningRecordFormValidator();
    private final VideoUploadPolicy videoUploadPolicy = new VideoUploadPolicy();
    private final StoryOverlayFormatter storyOverlayFormatter = new StoryOverlayFormatter();

    private String currentUserId;
    private Uri selectedRecordMediaUri;
    private String selectedRecordMediaType;
    private Uri pendingRecordCameraUri;
    private ImageView selectedRecordMediaPreview;
    private TextView selectedRecordMediaLabel;
    private TextView selectedRecordOverlayText;
    private TextInputLayout selectedRecordTextInput;
    private TextInputLayout groupNameInput;
    private View groupFormContainer;
    private MaterialButton showGroupFormButton;
    private MaterialButton createGroupButton;
    private MaterialButton cancelGroupFormButton;
    private LinearLayout groupsListContainer;
    private LinearLayout groupEntryContainer;

    private final ActivityResultLauncher<String> recordMediaPicker = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri == null) {
                    return;
                }
                String mediaType = detectMediaType(uri);
                long durationMillis = "video".equals(mediaType) ? readVideoDurationMillis(uri) : -1;
                if (!videoUploadPolicy.canUpload(mediaType, durationMillis)) {
                    selectedRecordMediaUri = null;
                    selectedRecordMediaType = null;
                    if (selectedRecordMediaLabel != null) {
                        selectedRecordMediaLabel.setText("선택된 미디어가 없습니다.");
                    }
                    Toast.makeText(requireContext(), "영상은 5초 이하만 업로드할 수 있습니다.", Toast.LENGTH_LONG).show();
                    return;
                }
                applySelectedRecordMedia(uri, mediaType);
            });

    private final ActivityResultLauncher<Uri> recordCameraLauncher = registerForActivityResult(
            new ActivityResultContracts.TakePicture(),
            success -> {
                if (success && pendingRecordCameraUri != null) {
                    applySelectedRecordMedia(pendingRecordCameraUri, "image");
                }
            });

    public static GroupsFragment newInstance(String teamId, String nickname, String email, String userId) {
        GroupsFragment fragment = new GroupsFragment();
        Bundle args = new Bundle();
        args.putString("teamId", teamId);
        args.putString("nickname", nickname);
        args.putString("email", email);
        args.putString("userId", userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            currentUserId = args.getString("userId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_groups, container, false);
        groupNameInput = view.findViewById(R.id.group_name_input);
        groupFormContainer = view.findViewById(R.id.group_form_container);
        showGroupFormButton = view.findViewById(R.id.show_group_form_button);
        createGroupButton = view.findViewById(R.id.create_group_button);
        cancelGroupFormButton = view.findViewById(R.id.cancel_group_form_button);
        groupsListContainer = view.findViewById(R.id.groups_list_container);
        groupEntryContainer = view.findViewById(R.id.group_entry_container);
        showGroupFormButton.setOnClickListener(v -> showGroupForm(true));
        cancelGroupFormButton.setOnClickListener(v -> showGroupForm(false));
        createGroupButton.setOnClickListener(v -> createGroup());
        render();
        loadGroups();
        return view;
    }

    private void render() {
        Team team = selectedTeam();
        host().setScreenHeader("응원 그룹", team.name + " 팬들과 함께 회차별 응원을 남깁니다.");
        groupsListContainer.removeAllViews();
        groupEntryContainer.removeAllViews();
        groupsListContainer.addView(infoCard("응원 그룹", "그룹을 불러오는 중입니다", "같은 팀 팬들과 오늘의 응원을 기록해 보세요."));
    }

    private void loadGroups() {
        groupRepository.getGroupsByTeam(teamId, new ListCallback<Group>() {
            @Override
            public void onSuccess(List<Group> groups) {
                if (!isAdded()) {
                    return;
                }
                groupsListContainer.removeAllViews();
                addSection(groupsListContainer, "그룹 목록");
                if (groups.isEmpty()) {
                    groupsListContainer.addView(infoCard("시작하기", "아직 그룹이 없습니다", "첫 그룹을 만들고 함께 응원할 팬을 초대하세요."));
                    return;
                }
                for (Group group : groups) {
                    groupsListContainer.addView(groupCard(group));
                }
            }

            @Override
            public void onError(String message) {
                if (isAdded()) {
                    groupsListContainer.removeAllViews();
                    groupsListContainer.addView(infoCard("오류", message, "지금은 그룹 목록을 불러올 수 없습니다."));
                }
            }
        });
    }

    private View groupCard(Group group) {
        com.google.android.material.card.MaterialCardView card = Ui.card(requireContext());
        LinearLayout body = new LinearLayout(requireContext());
        body.setOrientation(LinearLayout.VERTICAL);
        body.setPadding(Ui.dp(requireContext(), 16), Ui.dp(requireContext(), 16), Ui.dp(requireContext(), 16), Ui.dp(requireContext(), 16));

        String title = group.name == null || group.name.isEmpty() ? "이름 없는 그룹" : group.name;
        TextView labelView = Ui.text(requireContext(), "응원 그룹", 12, Typeface.BOLD, R.color.brand_blue);
        TextView titleView = Ui.text(requireContext(), title, 18, Typeface.BOLD, R.color.text_primary);
        TextView bodyView = Ui.text(requireContext(), "입장해서 멤버를 초대하고 회차별 응원 기록을 남겨 보세요.", 14, Typeface.NORMAL, R.color.text_secondary);
        MaterialButton enterButton = Ui.button(requireContext(), "입장", R.color.brand_red);
        enterButton.setOnClickListener(v -> enterGroup(group));

        body.addView(labelView);
        body.addView(titleView);
        body.addView(bodyView);
        body.addView(enterButton);
        card.addView(body);
        return card;
    }

    private void createGroup() {
        GroupFormValidator.Result result = groupFormValidator.validateName(Ui.value(groupNameInput));
        if (!result.valid) {
            groupNameInput.setError(result.message);
            Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show();
            return;
        }
        groupNameInput.setError(null);

        setCreating(true);
        groupRepository.createGroup(result.value, teamId, new AppCallback<Group>() {
            @Override
            public void onSuccess(Group group) {
                if (!isAdded()) {
                    return;
                }
                setCreating(false);
                if (groupNameInput.getEditText() != null) {
                    groupNameInput.getEditText().setText("");
                }
                showGroupForm(false);
                Toast.makeText(requireContext(), "그룹을 만들었습니다.", Toast.LENGTH_SHORT).show();
                loadGroups();
                enterGroup(group);
            }

            @Override
            public void onError(String message) {
                if (isAdded()) {
                    setCreating(false);
                    groupsListContainer.addView(infoCard("그룹 생성 실패", "그룹을 만들지 못했습니다.", message));
                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void enterGroup(Group group) {
        groupEntryContainer.removeAllViews();
        String title = group.name == null || group.name.isEmpty() ? "그룹" : group.name;
        groupEntryContainer.addView(infoCard("입장", title, "그룹 정보를 불러오는 중입니다."));
        Toast.makeText(requireContext(), title + "에 입장했습니다.", Toast.LENGTH_SHORT).show();
        loadGroupDetail(group);
    }

    private void loadGroupDetail(Group group) {
        groupRepository.getMembers(group.id, new ListCallback<GroupMember>() {
            @Override
            public void onSuccess(List<GroupMember> members) {
                if (!isAdded()) {
                    return;
                }
                String role = roleResolver.resolve(group, members, currentUserId);
                renderGroupDetail(group, members, role);
            }

            @Override
            public void onError(String message) {
                if (isAdded()) {
                    groupEntryContainer.removeAllViews();
                    groupEntryContainer.addView(infoCard("그룹 오류", message, "멤버 정보를 불러오지 못했습니다."));
                }
            }
        });
    }

    private void renderGroupDetail(Group group, List<GroupMember> members, String role) {
        groupEntryContainer.removeAllViews();
        String title = group.name == null || group.name.isEmpty() ? "그룹" : group.name;
        groupEntryContainer.addView(infoCard("응원 그룹", title, "내 역할: " + roleLabel(role)));

        if (permissionPolicy.canInviteMembers(role)) {
            addInviteSection(group, role);
        }
        if (permissionPolicy.canCreateInning(role)) {
            addInningSection(group, role);
        }
        addInningListSection(group, members, role);
        if (permissionPolicy.canManageMembers(role)) {
            addMemberManagementSection(group, members, role);
        } else {
            addMemberReadOnlySection(members);
        }
        if (permissionPolicy.canDeleteGroup(role)) {
            addDeleteGroupSection(group, role);
        }
    }

    private void addInviteSection(Group group, String role) {
        addSection(groupEntryContainer, "함께 응원할 멤버");
        TextInputLayout inviteInput = Ui.input(requireContext(), "초대할 사용자 코드", false);
        MaterialButton inviteButton = Ui.button(requireContext(), "멤버 초대", R.color.brand_blue);
        inviteButton.setOnClickListener(v -> {
            String userId = Ui.value(inviteInput);
            if (userId.isEmpty()) {
                Toast.makeText(requireContext(), "초대할 사용자 코드를 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            Ui.setEnabled(false, inviteInput, inviteButton);
            groupRepository.addMember(group.id, userId, role, new AppCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    if (isAdded()) {
                        Toast.makeText(requireContext(), "멤버를 초대했습니다.", Toast.LENGTH_SHORT).show();
                        loadGroupDetail(group);
                    }
                }

                @Override
                public void onError(String message) {
                    if (isAdded()) {
                        Ui.setEnabled(true, inviteInput, inviteButton);
                        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
                    }
                }
            });
        });
        groupEntryContainer.addView(inviteInput);
        groupEntryContainer.addView(inviteButton);
    }

    private void addInningSection(Group group, String role) {
        addSection(groupEntryContainer, "새 회차 만들기");
        TextInputLayout inningInput = Ui.input(requireContext(), "회차 번호", false);
        MaterialButton inningButton = Ui.button(requireContext(), "회차 만들기", R.color.brand_red);
        inningButton.setOnClickListener(v -> {
            int inningNumber;
            try {
                inningNumber = Integer.parseInt(Ui.value(inningInput));
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), "회차 번호를 숫자로 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            Ui.setEnabled(false, inningInput, inningButton);
            inningRepository.createInning(group.id, inningNumber, role, new AppCallback<Inning>() {
                @Override
                public void onSuccess(Inning result) {
                    if (isAdded()) {
                        Toast.makeText(requireContext(), result.inningNumber + "회차를 추가했습니다.", Toast.LENGTH_SHORT).show();
                        loadGroupDetail(group);
                    }
                }

                @Override
                public void onError(String message) {
                    if (isAdded()) {
                        Ui.setEnabled(true, inningInput, inningButton);
                        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
                    }
                }
            });
        });
        groupEntryContainer.addView(inningInput);
        groupEntryContainer.addView(inningButton);
    }

    private void addInningListSection(Group group, List<GroupMember> members, String role) {
        addSection(groupEntryContainer, "응원 회차");
        HorizontalScrollView scrollView = new HorizontalScrollView(requireContext());
        scrollView.setHorizontalScrollBarEnabled(false);
        scrollView.setClipToPadding(false);
        scrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        scrollView.setPadding(0, 0, Ui.dp(requireContext(), 18), 0);
        LinearLayout inningList = new LinearLayout(requireContext());
        inningList.setOrientation(LinearLayout.HORIZONTAL);
        scrollView.addView(inningList);
        inningList.addView(infoCard("응원 회차", "회차를 불러오는 중입니다", "함께 기록할 회차가 표시됩니다."));
        groupEntryContainer.addView(scrollView);

        inningRepository.getInnings(group.id, new ListCallback<Inning>() {
            @Override
            public void onSuccess(List<Inning> innings) {
                if (!isAdded()) {
                    return;
                }
                inningList.removeAllViews();
                if (innings.isEmpty()) {
                    inningList.addView(infoCard("응원 회차", "아직 회차가 없습니다", "운영 멤버가 1회, 2회처럼 회차를 만들 수 있습니다."));
                    return;
                }
                for (Inning inning : innings) {
                    inningList.addView(inningSummaryCard(group, inning, members, role));
                }
            }

            @Override
            public void onError(String message) {
                if (isAdded()) {
                    inningList.removeAllViews();
                    inningList.addView(infoCard("회차 오류", message, "회차 정보를 불러오지 못했습니다."));
                }
            }
        });
    }

    private View inningSummaryCard(Group group, Inning inning, List<GroupMember> members, String role) {
        com.google.android.material.card.MaterialCardView card = Ui.card(requireContext());
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(inningCarouselWidth(), LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(0, Ui.dp(requireContext(), 8), Ui.dp(requireContext(), 12), Ui.dp(requireContext(), 8));
        card.setLayoutParams(cardParams);
        LinearLayout body = new LinearLayout(requireContext());
        body.setOrientation(LinearLayout.VERTICAL);
        body.setPadding(Ui.dp(requireContext(), 18), Ui.dp(requireContext(), 18), Ui.dp(requireContext(), 18), Ui.dp(requireContext(), 18));

        TextView recordCount = Ui.text(requireContext(), "0명 기록", 14, Typeface.NORMAL, R.color.brand_blue);
        body.addView(inningSummaryHeader(inning, recordCount));
        body.addView(Ui.text(requireContext(), "구성원별 사진/영상/글귀 기록을 확인합니다.", 14, Typeface.NORMAL, R.color.text_secondary));

        LinearLayout recordPreview = new LinearLayout(requireContext());
        recordPreview.setOrientation(LinearLayout.VERTICAL);
        body.addView(recordPreview);
        loadInningSummaryRecords(group, inning, members, role, recordPreview, recordCount);
        card.addView(body);
        return card;
    }

    private View inningSummaryHeader(Inning inning, TextView recordCount) {
        LinearLayout header = new LinearLayout(requireContext());
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.TOP);
        LinearLayout.LayoutParams headerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        headerParams.setMargins(0, 0, 0, Ui.dp(requireContext(), 8));
        header.setLayoutParams(headerParams);

        LinearLayout titleBlock = new LinearLayout(requireContext());
        titleBlock.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        titleBlock.setLayoutParams(titleParams);
        titleBlock.addView(Ui.text(requireContext(), "응원 회차", 13, Typeface.BOLD, R.color.brand_red));
        TextView inningTitle = Ui.text(requireContext(), inning.inningNumber + "회", 42, Typeface.BOLD, R.color.text_primary);
        titleBlock.addView(inningTitle);

        recordCount.setGravity(Gravity.END);
        header.addView(titleBlock);
        header.addView(recordCount);
        return header;
    }

    private void loadInningSummaryRecords(Group group, Inning inning, List<GroupMember> members, String role,
                                          LinearLayout recordPreview, TextView recordCount) {
        inningRepository.getRecords(group.id, inning.id, new ListCallback<InningRecord>() {
            @Override
            public void onSuccess(List<InningRecord> records) {
                if (!isAdded()) {
                    return;
                }
                recordPreview.removeAllViews();
                Map<String, InningRecord> recordMap = new HashMap<>();
                for (InningRecord record : records) {
                    if (record != null && record.authorId != null) {
                        recordMap.put(record.authorId, record);
                    }
                }
                recordCount.setText(recordMap.size() + "명 기록");
                if (members.isEmpty()) {
                    recordPreview.addView(Ui.body(requireContext(), "아직 구성원이 없습니다."));
                    return;
                }
                for (GroupMember member : members) {
                    recordPreview.addView(memberRecordSummary(group, inning, member, recordMap.get(member.userId), role));
                }
            }

            @Override
            public void onError(String message) {
                if (isAdded()) {
                    recordPreview.removeAllViews();
                    recordPreview.addView(Ui.body(requireContext(), "기록을 불러오지 못했습니다."));
                }
            }
        });
    }

    private View memberRecordSummary(Group group, Inning inning, GroupMember member, InningRecord record, String role) {
        LinearLayout wrapper = new LinearLayout(requireContext());
        wrapper.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams wrapperParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        wrapperParams.setMargins(0, Ui.dp(requireContext(), 10), 0, 0);
        wrapper.setLayoutParams(wrapperParams);
        wrapper.setPadding(Ui.dp(requireContext(), 14), Ui.dp(requireContext(), 14), Ui.dp(requireContext(), 14), Ui.dp(requireContext(), 14));
        wrapper.setBackground(Ui.rounded(requireContext(), R.color.surface_muted, 8));
        wrapper.setOnClickListener(v -> openInningDetail(group, inning));

        LinearLayout memberHead = new LinearLayout(requireContext());
        memberHead.setOrientation(LinearLayout.HORIZONTAL);
        memberHead.setGravity(Gravity.CENTER_VERTICAL);
        memberHead.addView(recordAvatar(record));
        TextView memberName = Ui.text(requireContext(), displayMember(member), 18, Typeface.BOLD, R.color.text_primary);
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        nameParams.setMargins(Ui.dp(requireContext(), 10), 0, 0, 0);
        memberName.setLayoutParams(nameParams);
        memberHead.addView(memberName);
        wrapper.addView(memberHead);

        TextView time = Ui.text(requireContext(), record == null ? "19:00" : "기록 완료", 38, Typeface.BOLD, R.color.border_soft);
        time.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams timeParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        timeParams.setMargins(0, Ui.dp(requireContext(), 20), 0, Ui.dp(requireContext(), 8));
        time.setLayoutParams(timeParams);
        wrapper.addView(time);

        if (record == null) {
            TextView shoot = Ui.text(requireContext(), "눌러서 촬영", 17, Typeface.BOLD, R.color.text_primary);
            shoot.setGravity(Gravity.CENTER);
            shoot.setPadding(Ui.dp(requireContext(), 14), Ui.dp(requireContext(), 9), Ui.dp(requireContext(), 14), Ui.dp(requireContext(), 9));
            shoot.setBackground(Ui.rounded(requireContext(), R.color.surface_card, 999));
            LinearLayout.LayoutParams shootParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            shootParams.gravity = Gravity.CENTER_HORIZONTAL;
            shootParams.setMargins(0, 0, 0, Ui.dp(requireContext(), 14));
            shoot.setLayoutParams(shootParams);
            wrapper.addView(shoot);
        }

        TextView summary = Ui.text(requireContext(), recordSummaryText(record), 12, Typeface.NORMAL, R.color.text_secondary);
        summary.setGravity(Gravity.CENTER_VERTICAL);
        summary.setBackground(Ui.rounded(requireContext(), R.color.surface_card, 8));
        summary.setPadding(Ui.dp(requireContext(), 12), Ui.dp(requireContext(), 10), Ui.dp(requireContext(), 12), Ui.dp(requireContext(), 10));
        wrapper.addView(summary);

        if (record != null && permissionPolicy.canDeleteRecord(role, currentUserId, record.authorId)) {
            MaterialButton deleteButton = Ui.textButton(requireContext(), "기록 삭제");
            deleteButton.setOnClickListener(v -> deleteRecord(group, inning, record.authorId, role));
            wrapper.addView(deleteButton);
        }
        return wrapper;
    }

    private TextView recordAvatar(InningRecord record) {
        TextView avatar = Ui.text(requireContext(), record == null ? "◎" : "IMG", 12, Typeface.BOLD,
                record == null ? R.color.text_primary : R.color.white);
        avatar.setGravity(Gravity.CENTER);
        avatar.setBackground(Ui.rounded(requireContext(), record == null ? R.color.brand_gold : R.color.brand_blue, 999));
        LinearLayout.LayoutParams avatarParams = new LinearLayout.LayoutParams(Ui.dp(requireContext(), 36), Ui.dp(requireContext(), 36));
        avatar.setLayoutParams(avatarParams);
        return avatar;
    }

    private int inningCarouselWidth() {
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        return Math.max(Ui.dp(requireContext(), 300), screenWidth - Ui.dp(requireContext(), 74));
    }

    private String recordSummaryText(InningRecord record) {
        if (record == null) {
            return "아직 기록 없음";
        }
        if (record.text != null && !record.text.isEmpty()) {
            return record.text;
        }
        if (record.mediaType != null && !record.mediaType.isEmpty()) {
            return mediaLabel(record.mediaType);
        }
        return "기록 있음";
    }

    private void openInningDetail(Group group, Inning inning) {
        groupRepository.getMembers(group.id, new ListCallback<GroupMember>() {
            @Override
            public void onSuccess(List<GroupMember> members) {
                if (!isAdded()) {
                    return;
                }
                String role = roleResolver.resolve(group, members, currentUserId);
                inningRepository.getRecords(group.id, inning.id, new ListCallback<InningRecord>() {
                    @Override
                    public void onSuccess(List<InningRecord> records) {
                        if (!isAdded()) {
                            return;
                        }
                        renderInningDetail(group, inning, members, records, role);
                    }

                    @Override
                    public void onError(String message) {
                        if (isAdded()) {
                            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }

            @Override
            public void onError(String message) {
                if (isAdded()) {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void renderInningDetail(Group group, Inning inning, List<GroupMember> members,
                                    List<InningRecord> records, String role) {
        selectedRecordMediaUri = null;
        selectedRecordMediaType = null;
        groupEntryContainer.removeAllViews();
        groupEntryContainer.addView(infoCard("회차 기록", inning.inningNumber + "회", "사진, 영상, 글귀로 응원을 남깁니다."));
        MaterialButton backButton = Ui.button(requireContext(), "그룹 상세로 돌아가기", R.color.brand_blue);
        backButton.setOnClickListener(v -> loadGroupDetail(group));
        groupEntryContainer.addView(backButton);

        Map<String, InningRecord> recordMap = new HashMap<>();
        for (InningRecord record : records) {
            if (record != null && record.authorId != null) {
                recordMap.put(record.authorId, record);
            }
        }

        addSection(groupEntryContainer, "내 회차 기록");
        InningRecord currentRecord = recordMap.get(currentUserId);
        groupEntryContainer.addView(myRecordEditor(group, inning, currentRecord, role));

        addSection(groupEntryContainer, "구성원 기록");
        if (members.isEmpty()) {
            groupEntryContainer.addView(infoCard("멤버", "아직 멤버가 없습니다", "멤버를 초대한 뒤 회차 기록을 남길 수 있습니다."));
            return;
        }
        for (GroupMember member : members) {
            groupEntryContainer.addView(recordCard(group, inning, member, recordMap.get(member.userId), role));
        }
    }

    private View myRecordEditor(Group group, Inning inning, InningRecord currentRecord, String role) {
        com.google.android.material.card.MaterialCardView card = Ui.card(requireContext());
        LinearLayout body = new LinearLayout(requireContext());
        body.setOrientation(LinearLayout.VERTICAL);
        body.setPadding(Ui.dp(requireContext(), 16), Ui.dp(requireContext(), 16), Ui.dp(requireContext(), 16), Ui.dp(requireContext(), 16));

        body.addView(Ui.text(requireContext(), "내 응원", 12, Typeface.BOLD, R.color.brand_blue));
        TextInputLayout textInput = Ui.input(requireContext(), "글귀", false);
        selectedRecordTextInput = textInput;
        if (currentRecord != null && currentRecord.text != null && textInput.getEditText() != null) {
            textInput.getEditText().setText(currentRecord.text);
        }
        View mediaPanel = recordMediaPanel(currentRecord);
        MaterialButton cameraButton = Ui.button(requireContext(), "카메라", R.color.brand_blue);
        MaterialButton folderButton = Ui.button(requireContext(), "폴더", R.color.brand_blue);
        MaterialButton saveButton = Ui.button(requireContext(), "기록 저장", R.color.brand_red);
        cameraButton.setOnClickListener(v -> startRecordCameraCapture());
        folderButton.setOnClickListener(v -> showRecordFolderOptions());
        saveButton.setOnClickListener(v -> saveMyRecord(group, inning, currentRecord, textInput, role));
        bindRecordStoryOverlay(textInput);

        body.addView(textInput);
        body.addView(mediaPanel);
        body.addView(cameraButton);
        body.addView(folderButton);
        body.addView(saveButton);
        if (currentRecord != null) {
            MaterialButton deleteButton = Ui.button(requireContext(), "내 기록 삭제", R.color.brand_red);
            deleteButton.setOnClickListener(v -> deleteRecord(group, inning, currentUserId, role));
            body.addView(deleteButton);
        }
        card.addView(body);
        return card;
    }

    private View recordMediaPanel(InningRecord currentRecord) {
        FrameLayout panel = new FrameLayout(requireContext());
        panel.setBackgroundColor(getResources().getColor(R.color.surface_muted, requireContext().getTheme()));
        panel.setClickable(true);
        panel.setFocusable(true);
        panel.setOnClickListener(v -> showRecordMediaOptions());
        LinearLayout.LayoutParams panelParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                Ui.dp(requireContext(), 260));
        panelParams.setMargins(0, Ui.dp(requireContext(), 8), 0, Ui.dp(requireContext(), 8));
        panel.setLayoutParams(panelParams);

        selectedRecordMediaPreview = new ImageView(requireContext());
        selectedRecordMediaPreview.setScaleType(ImageView.ScaleType.CENTER_CROP);
        selectedRecordMediaPreview.setContentDescription("선택한 응원 사진 미리보기");
        selectedRecordMediaPreview.setVisibility(View.GONE);
        panel.addView(selectedRecordMediaPreview, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        selectedRecordMediaLabel = Ui.text(requireContext(),
                currentRecord == null || currentRecord.mediaUrl == null || currentRecord.mediaUrl.isEmpty()
                        ? "터치해서 카메라 또는 폴더에서 선택"
                        : "기존 " + mediaLabel(currentRecord.mediaType),
                16,
                Typeface.BOLD,
                R.color.brand_red);
        selectedRecordMediaLabel.setGravity(Gravity.CENTER);
        selectedRecordMediaLabel.setPadding(Ui.dp(requireContext(), 18), Ui.dp(requireContext(), 18), Ui.dp(requireContext(), 18), Ui.dp(requireContext(), 18));
        panel.addView(selectedRecordMediaLabel, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        selectedRecordOverlayText = Ui.text(requireContext(), storyOverlayFormatter.overlayText(Ui.value(selectedRecordTextInput)), 22, Typeface.BOLD, R.color.white);
        selectedRecordOverlayText.setGravity(Gravity.CENTER);
        selectedRecordOverlayText.setBackgroundColor(0x99000000);
        selectedRecordOverlayText.setPadding(Ui.dp(requireContext(), 16), Ui.dp(requireContext(), 10), Ui.dp(requireContext(), 16), Ui.dp(requireContext(), 10));
        FrameLayout.LayoutParams overlayParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER);
        overlayParams.setMargins(Ui.dp(requireContext(), 18), Ui.dp(requireContext(), 18), Ui.dp(requireContext(), 18), Ui.dp(requireContext(), 18));
        selectedRecordOverlayText.setVisibility(currentRecord == null || currentRecord.mediaUrl == null || currentRecord.mediaUrl.isEmpty()
                ? View.GONE
                : View.VISIBLE);
        panel.addView(selectedRecordOverlayText, overlayParams);
        return panel;
    }

    private void showRecordMediaOptions() {
        new MaterialAlertDialogBuilder(requireContext())
                .setItems(new String[]{"카메라로 촬영", "폴더에서 사진 선택", "폴더에서 영상 선택"}, (dialog, which) -> {
                    if (which == 0) {
                        startRecordCameraCapture();
                    } else if (which == 1) {
                        recordMediaPicker.launch("image/*");
                    } else {
                        recordMediaPicker.launch("video/*");
                    }
                })
                .show();
    }

    private void showRecordFolderOptions() {
        new MaterialAlertDialogBuilder(requireContext())
                .setItems(new String[]{"사진 선택", "영상 선택"}, (dialog, which) -> recordMediaPicker.launch(which == 0 ? "image/*" : "video/*"))
                .show();
    }

    private void startRecordCameraCapture() {
        try {
            File directory = new File(requireContext().getCacheDir(), "camera");
            if (!directory.exists() && !directory.mkdirs()) {
                Toast.makeText(requireContext(), "카메라 파일을 준비하지 못했습니다.", Toast.LENGTH_SHORT).show();
                return;
            }
            File file = File.createTempFile("group_record_", ".jpg", directory);
            pendingRecordCameraUri = FileProvider.getUriForFile(
                    requireContext(),
                    requireContext().getPackageName() + ".fileprovider",
                    file);
            recordCameraLauncher.launch(pendingRecordCameraUri);
        } catch (IOException e) {
            Toast.makeText(requireContext(), "카메라를 열 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void applySelectedRecordMedia(Uri uri, String mediaType) {
        selectedRecordMediaUri = uri;
        selectedRecordMediaType = mediaType;
        if (selectedRecordMediaLabel != null) {
            selectedRecordMediaLabel.setText(storyOverlayFormatter.mediaSelectedLabel(mediaType));
            selectedRecordMediaLabel.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        }
        if (selectedRecordOverlayText != null) {
            selectedRecordOverlayText.setVisibility(View.VISIBLE);
            updateRecordStoryOverlay();
        }
        if (selectedRecordMediaPreview == null) {
            return;
        }
        if ("image".equals(mediaType)) {
            selectedRecordMediaPreview.setVisibility(View.VISIBLE);
            selectedRecordMediaPreview.setImageURI(uri);
        } else {
            selectedRecordMediaPreview.setVisibility(View.GONE);
            if (selectedRecordMediaLabel != null) {
                selectedRecordMediaLabel.setText("선택한 영상에 글귀를 올려보세요\n" + safeFileName(uri));
            }
        }
    }

    private void bindRecordStoryOverlay(TextInputLayout textInput) {
        if (textInput.getEditText() == null) {
            return;
        }
        textInput.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateRecordStoryOverlay();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void updateRecordStoryOverlay() {
        if (selectedRecordOverlayText == null || selectedRecordTextInput == null) {
            return;
        }
        selectedRecordOverlayText.setText(storyOverlayFormatter.overlayText(Ui.value(selectedRecordTextInput)));
    }

    private View recordCard(Group group, Inning inning, GroupMember member, InningRecord record, String actorRole) {
        String memberName = displayMember(member);
        if (record == null) {
            return infoCard("기록 대기", memberName, "아직 이 회차 기록이 없습니다.");
        }

        com.google.android.material.card.MaterialCardView card = Ui.card(requireContext());
        LinearLayout body = new LinearLayout(requireContext());
        body.setOrientation(LinearLayout.VERTICAL);
        body.setPadding(Ui.dp(requireContext(), 16), Ui.dp(requireContext(), 16), Ui.dp(requireContext(), 16), Ui.dp(requireContext(), 16));
        body.addView(Ui.text(requireContext(), memberName, 18, Typeface.BOLD, R.color.text_primary));
        body.addView(Ui.text(requireContext(), mediaLabel(record.mediaType), 13, Typeface.BOLD, R.color.brand_blue));
        body.addView(Ui.text(requireContext(), record.text == null || record.text.isEmpty() ? "글귀 없음" : record.text, 14, Typeface.NORMAL, R.color.text_secondary));
        if (permissionPolicy.canDeleteRecord(actorRole, currentUserId, record.authorId)) {
            MaterialButton deleteButton = Ui.button(requireContext(), "기록 삭제", R.color.brand_red);
            deleteButton.setOnClickListener(v -> deleteRecord(group, inning, record.authorId, actorRole));
            body.addView(deleteButton);
        }
        card.addView(body);
        return card;
    }

    private void saveMyRecord(Group group, Inning inning, InningRecord currentRecord, TextInputLayout textInput, String role) {
        boolean hasMedia = selectedRecordMediaUri != null || (currentRecord != null && currentRecord.mediaUrl != null && !currentRecord.mediaUrl.isEmpty());
        InningRecordFormValidator.Result validation = inningRecordFormValidator.validate(Ui.value(textInput), hasMedia);
        if (!validation.valid) {
            Toast.makeText(requireContext(), validation.message, Toast.LENGTH_SHORT).show();
            return;
        }

        InningRecord record = currentRecord == null ? new InningRecord() : currentRecord;
        record.authorId = currentUserId;
        record.authorNickname = nickname;
        record.text = validation.text;
        if (selectedRecordMediaType != null) {
            record.mediaType = selectedRecordMediaType;
        }

        inningRepository.createOrUpdateRecordWithMedia(group.id, inning.id, record, selectedRecordMediaUri, currentUserId, role, new AppCallback<InningRecord>() {
            @Override
            public void onSuccess(InningRecord result) {
                if (isAdded()) {
                    selectedRecordMediaUri = null;
                    selectedRecordMediaType = null;
                    Toast.makeText(requireContext(), "회차 기록을 저장했습니다.", Toast.LENGTH_SHORT).show();
                    openInningDetail(group, inning);
                }
            }

            @Override
            public void onError(String message) {
                if (isAdded()) {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void deleteRecord(Group group, Inning inning, String recordUserId, String role) {
        inningRepository.deleteRecord(group.id, inning.id, recordUserId, currentUserId, role, new AppCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                if (isAdded()) {
                    Toast.makeText(requireContext(), "회차 기록을 삭제했습니다.", Toast.LENGTH_SHORT).show();
                    openInningDetail(group, inning);
                }
            }

            @Override
            public void onError(String message) {
                if (isAdded()) {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void addMemberManagementSection(Group group, List<GroupMember> members, String role) {
        addSection(groupEntryContainer, "멤버");
        if (members.isEmpty()) {
            groupEntryContainer.addView(infoCard("멤버", "아직 멤버가 없습니다", "초대 기능으로 함께 응원할 멤버를 추가하세요."));
            return;
        }
        for (GroupMember member : members) {
            groupEntryContainer.addView(memberCard(group, member, role));
        }
    }

    private void addMemberReadOnlySection(List<GroupMember> members) {
        addSection(groupEntryContainer, "구성원");
        if (members.isEmpty()) {
            groupEntryContainer.addView(infoCard("멤버", "멤버 정보가 없습니다", "초대된 멤버가 이곳에 표시됩니다."));
            return;
        }
        for (GroupMember member : members) {
            groupEntryContainer.addView(infoCard("멤버", displayMember(member), "역할: " + roleLabel(member.role)));
        }
    }

    private View memberCard(Group group, GroupMember member, String actorRole) {
        com.google.android.material.card.MaterialCardView card = Ui.card(requireContext());
        LinearLayout body = new LinearLayout(requireContext());
        body.setOrientation(LinearLayout.VERTICAL);
        body.setPadding(Ui.dp(requireContext(), 16), Ui.dp(requireContext(), 16), Ui.dp(requireContext(), 16), Ui.dp(requireContext(), 16));

        body.addView(Ui.text(requireContext(), displayMember(member), 17, Typeface.BOLD, R.color.text_primary));
        body.addView(Ui.text(requireContext(), "역할: " + roleLabel(member.role), 14, Typeface.NORMAL, R.color.text_secondary));

        if (permissionPolicy.canGrantAdmin(actorRole) && !GroupPermissionPolicy.ROLE_OWNER.equals(member.role)) {
            String nextRole = GroupPermissionPolicy.ROLE_ADMIN.equals(member.role)
                    ? GroupPermissionPolicy.ROLE_MEMBER
                    : GroupPermissionPolicy.ROLE_ADMIN;
            String label = GroupPermissionPolicy.ROLE_ADMIN.equals(member.role) ? "운영 도우미 해제" : "운영 도우미로 지정";
            MaterialButton roleButton = Ui.button(requireContext(), label, R.color.brand_blue);
            roleButton.setOnClickListener(v -> updateMemberRole(group, member, nextRole, actorRole));
            body.addView(roleButton);
        }

        if (!GroupPermissionPolicy.ROLE_OWNER.equals(member.role)) {
            MaterialButton removeButton = Ui.button(requireContext(), "멤버 내보내기", R.color.brand_red);
            removeButton.setOnClickListener(v -> removeMember(group, member, actorRole));
            body.addView(removeButton);
        }

        card.addView(body);
        return card;
    }

    private void updateMemberRole(Group group, GroupMember member, String nextRole, String actorRole) {
        groupRepository.updateMemberRole(group.id, member.userId, nextRole, actorRole, new AppCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                if (isAdded()) {
                    Toast.makeText(requireContext(), "멤버 역할을 변경했습니다.", Toast.LENGTH_SHORT).show();
                    loadGroupDetail(group);
                }
            }

            @Override
            public void onError(String message) {
                if (isAdded()) {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void removeMember(Group group, GroupMember member, String actorRole) {
        groupRepository.removeMember(group.id, member.userId, actorRole, new AppCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                if (isAdded()) {
                    Toast.makeText(requireContext(), "멤버를 내보냈습니다.", Toast.LENGTH_SHORT).show();
                    loadGroupDetail(group);
                }
            }

            @Override
            public void onError(String message) {
                if (isAdded()) {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void addDeleteGroupSection(Group group, String role) {
        addSection(groupEntryContainer, "그룹 정리");
        MaterialButton deleteButton = Ui.button(requireContext(), "그룹 삭제", R.color.brand_red);
        deleteButton.setOnClickListener(v -> {
            deleteButton.setEnabled(false);
            groupRepository.deleteGroup(group.id, role, new AppCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    if (isAdded()) {
                        Toast.makeText(requireContext(), "그룹을 삭제했습니다.", Toast.LENGTH_SHORT).show();
                        groupEntryContainer.removeAllViews();
                        loadGroups();
                    }
                }

                @Override
                public void onError(String message) {
                    if (isAdded()) {
                        deleteButton.setEnabled(true);
                        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
                    }
                }
            });
        });
        groupEntryContainer.addView(deleteButton);
    }

    private void showGroupForm(boolean visible) {
        groupFormContainer.setVisibility(visible ? View.VISIBLE : View.GONE);
        showGroupFormButton.setVisibility(visible ? View.GONE : View.VISIBLE);
        if (!visible) {
            groupNameInput.setError(null);
            if (groupNameInput.getEditText() != null) {
                groupNameInput.getEditText().setText("");
            }
        } else if (groupNameInput.getEditText() != null) {
            groupNameInput.requestFocus();
        }
    }

    private String displayMember(GroupMember member) {
        if (member.nickname != null && !member.nickname.isEmpty()) {
            return member.nickname;
        }
        return member.userId == null || member.userId.isEmpty() ? "알 수 없는 구성원" : member.userId;
    }

    public static String roleLabel(String role) {
        if (GroupPermissionPolicy.ROLE_OWNER.equals(role)) {
            return "그룹 만든 사람";
        }
        if (GroupPermissionPolicy.ROLE_ADMIN.equals(role)) {
            return "운영 도우미";
        }
        return "그룹원";
    }

    public static String mediaLabel(String mediaType) {
        if (mediaType == null || mediaType.isEmpty()) {
            return "미디어 없음";
        }
        if (mediaType.startsWith("image")) {
            return "사진 기록";
        }
        if (mediaType.startsWith("video")) {
            return "영상 기록";
        }
        return "미디어 기록";
    }

    private String detectMediaType(Uri uri) {
        String type = requireContext().getContentResolver().getType(uri);
        if (type == null) {
            String extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type != null && type.startsWith("video") ? "video" : "image";
    }

    private long readVideoDurationMillis(Uri uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(requireContext(), uri);
            String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            return duration == null ? -1 : Long.parseLong(duration);
        } catch (RuntimeException e) {
            return -1;
        } finally {
            try {
                retriever.release();
            } catch (Exception ignored) {
            }
        }
    }

    private String safeFileName(Uri uri) {
        String value = uri == null ? "" : uri.getLastPathSegment();
        return value == null || value.isEmpty() ? "선택 완료" : value;
    }

    private void setCreating(boolean creating) {
        createGroupButton.setEnabled(!creating);
        showGroupFormButton.setEnabled(!creating);
        cancelGroupFormButton.setEnabled(!creating);
        createGroupButton.setText(creating ? "만드는 중..." : "만들기");
        groupNameInput.setEnabled(!creating);
    }
}
