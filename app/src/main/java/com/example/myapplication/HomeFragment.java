package com.example.myapplication;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myapplication.data.ListCallback;
import com.example.myapplication.data.PostRepository;
import com.example.myapplication.data.RepositoryProvider;
import com.example.myapplication.domain.HomeFeedFormatter;
import com.example.myapplication.model.Post;
import com.example.myapplication.model.Team;
import com.example.myapplication.util.Ui;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class HomeFragment extends BaseMainFragment {
    private final PostRepository postRepository = RepositoryProvider.posts();
    private final HomeFeedFormatter feedFormatter = new HomeFeedFormatter();
    private LinearLayout content;

    public static HomeFragment newInstance(String teamId, String nickname, String email) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString("teamId", teamId);
        args.putString("nickname", nickname);
        args.putString("email", email);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        content = view.findViewById(R.id.home_content);
        render();
        return view;
    }

    private void render() {
        Team team = selectedTeam();
        host().setScreenHeader("오늘의 응원 홈", team.name + " 팬들이 올린 응원 콘텐츠를 확인하세요.");
        addTeamOverview(content, team);
        content.addView(infoCard("TODAY", team.name + " 응원 피드", "사진과 짧은 영상으로 오늘의 응원을 남겨보세요."));
        addSection(content, "팬 피드");
        content.addView(samplePostCard(team.name));
        addBodyText(content, "새 게시글을 불러오는 중입니다...");

        postRepository.getPostsByTeam(team.id, new ListCallback<Post>() {
            @Override
            public void onSuccess(List<Post> posts) {
                if (!isAdded()) {
                    return;
                }
                content.removeAllViews();
                addTeamOverview(content, team);
                content.addView(infoCard("TODAY", team.name + " 응원 피드", "사진과 짧은 영상으로 오늘의 응원을 남겨보세요."));
                addSection(content, "팬 피드");
                content.addView(samplePostCard(team.name));
                if (posts.isEmpty()) {
                    content.addView(infoCard("첫 게시글을 기다리는 중", "그룹 회차에서 응원을 남겨보세요.", "그룹에 들어가 회차별 사진, 영상, 글귀를 기록할 수 있습니다."));
                    return;
                }
                for (Post post : posts) {
                    content.addView(postCard(post));
                }
            }

            @Override
            public void onError(String message) {
                if (isAdded()) {
                    content.addView(infoCard("피드를 불러오지 못했습니다", "잠시 후 다시 시도해 주세요.", "네트워크 상태를 확인하면 도움이 됩니다."));
                }
            }
        });
    }

    private void addTeamOverview(LinearLayout target, Team team) {
        target.addView(teamHeroCard(team));
        target.addView(recentRecordCard(team));
        target.addView(lastGameCard(team));
    }

    private View teamHeroCard(Team team) {
        MaterialCardView card = Ui.card(requireContext());
        LinearLayout body = new LinearLayout(requireContext());
        body.setGravity(Gravity.CENTER_HORIZONTAL);
        body.setOrientation(LinearLayout.VERTICAL);
        body.setPadding(Ui.dp(requireContext(), 18), Ui.dp(requireContext(), 24), Ui.dp(requireContext(), 18), Ui.dp(requireContext(), 24));

        TextView badge = Ui.text(requireContext(), badgeText(team), 28, Typeface.BOLD, R.color.white);
        badge.setGravity(Gravity.CENTER);
        badge.setBackground(Ui.rounded(requireContext(), R.color.brand_blue, 24));
        LinearLayout.LayoutParams badgeParams = new LinearLayout.LayoutParams(Ui.dp(requireContext(), 74), Ui.dp(requireContext(), 74));
        badgeParams.gravity = Gravity.CENTER_HORIZONTAL;
        badge.setLayoutParams(badgeParams);

        TextView logoHint = Ui.text(requireContext(), "[로고]", 13, Typeface.NORMAL, R.color.text_secondary);
        logoHint.setGravity(Gravity.CENTER);
        TextView name = Ui.text(requireContext(), team.name, 22, Typeface.BOLD, R.color.text_primary);
        name.setGravity(Gravity.CENTER);
        TextView ranking = Ui.text(requireContext(), feedFormatter.rankingSummary(team.id), 20, Typeface.BOLD, R.color.text_primary);
        ranking.setGravity(Gravity.CENTER);
        ranking.setBackground(Ui.rounded(requireContext(), R.color.surface_blush, 24));
        ranking.setPadding(Ui.dp(requireContext(), 18), Ui.dp(requireContext(), 10), Ui.dp(requireContext(), 18), Ui.dp(requireContext(), 10));

        body.addView(badge);
        body.addView(logoHint);
        body.addView(name);
        body.addView(ranking);
        card.addView(body);
        return card;
    }

    private View recentRecordCard(Team team) {
        MaterialCardView card = Ui.card(requireContext());
        LinearLayout body = new LinearLayout(requireContext());
        body.setOrientation(LinearLayout.VERTICAL);
        body.setPadding(Ui.dp(requireContext(), 18), Ui.dp(requireContext(), 18), Ui.dp(requireContext(), 18), Ui.dp(requireContext(), 18));
        body.addView(Ui.text(requireContext(), "최근 전적", 18, Typeface.BOLD, R.color.text_primary));

        LinearLayout row = new LinearLayout(requireContext());
        row.setGravity(Gravity.CENTER);
        row.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        rowParams.setMargins(0, Ui.dp(requireContext(), 14), 0, 0);
        row.setLayoutParams(rowParams);

        String[] records = feedFormatter.recentRecordSummary(team.id).split(" · ");
        for (String record : records) {
            TextView chip = Ui.text(requireContext(), record, 14, Typeface.BOLD, R.color.white);
            chip.setGravity(Gravity.CENTER);
            chip.setBackground(Ui.rounded(requireContext(), "승".equals(record) ? R.color.record_win : R.color.record_loss, 999));
            LinearLayout.LayoutParams chipParams = new LinearLayout.LayoutParams(Ui.dp(requireContext(), 42), Ui.dp(requireContext(), 42));
            chipParams.setMargins(Ui.dp(requireContext(), 4), 0, Ui.dp(requireContext(), 4), 0);
            row.addView(chip, chipParams);
        }
        body.addView(row);
        card.addView(body);
        return card;
    }

    private View lastGameCard(Team team) {
        MaterialCardView card = Ui.card(requireContext());
        LinearLayout body = new LinearLayout(requireContext());
        body.setOrientation(LinearLayout.VERTICAL);
        body.setPadding(Ui.dp(requireContext(), 18), Ui.dp(requireContext(), 18), Ui.dp(requireContext(), 18), Ui.dp(requireContext(), 18));
        body.addView(Ui.text(requireContext(), "직전 경기", 18, Typeface.BOLD, R.color.text_primary));
        boolean hasReferenceGame = "lotte".equals(team.id);
        addGameRow(body, "상대", feedFormatter.lastGameSummary(team.id, team.name));
        addGameRow(body, "결과", hasReferenceGame ? "패" : "경기 전");
        addGameRow(body, "선발", hasReferenceGame ? "스트레일리" : "발표 전");
        addGameRow(body, "안타", hasReferenceGame ? "6 / 홈런 1" : "기록 준비 중");
        addGameRow(body, "오늘의 MVP", hasReferenceGame ? "전준우" : "응원 대기");
        card.addView(body);
        return card;
    }

    private void addGameRow(LinearLayout body, String label, String value) {
        LinearLayout row = new LinearLayout(requireContext());
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        rowParams.setMargins(0, Ui.dp(requireContext(), 10), 0, 0);
        row.setLayoutParams(rowParams);

        TextView labelView = Ui.text(requireContext(), label, 14, Typeface.NORMAL, R.color.text_secondary);
        TextView valueView = Ui.text(requireContext(), value, 14, Typeface.BOLD, "결과".equals(label) ? R.color.brand_red : R.color.text_primary);
        valueView.setGravity(Gravity.END);
        row.addView(labelView, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        row.addView(valueView, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        body.addView(row);
    }

    private String badgeText(Team team) {
        if (team.shortName == null || team.shortName.isEmpty()) {
            return "팀";
        }
        return team.shortName.substring(0, 1);
    }

    private View postCard(Post post) {
        String title = post.authorNickname == null || post.authorNickname.isEmpty() ? "익명 응원러" : post.authorNickname;
        return feedCard(title, feedFormatter.mediaSummary(post), feedFormatter.body(post));
    }

    private View samplePostCard(String teamName) {
        return feedCard("응원ON 샘플", "사진 · 좋아요 12", teamName + " 팬들의 응원 순간입니다.");
    }

    private View feedCard(String label, String title, String bodyText) {
        MaterialCardView card = Ui.card(requireContext());
        LinearLayout body = new LinearLayout(requireContext());
        body.setOrientation(LinearLayout.VERTICAL);

        ImageView image = new ImageView(requireContext());
        image.setImageResource(R.drawable.sample_feed_photo);
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        image.setContentDescription("야구장 응원 사진");
        body.addView(image, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                Ui.dp(requireContext(), 210)));

        LinearLayout textBlock = new LinearLayout(requireContext());
        textBlock.setOrientation(LinearLayout.VERTICAL);
        textBlock.setPadding(Ui.dp(requireContext(), 16), Ui.dp(requireContext(), 14), Ui.dp(requireContext(), 16), Ui.dp(requireContext(), 16));
        TextView labelView = Ui.text(requireContext(), label, 12, Typeface.BOLD, R.color.brand_red);
        TextView titleView = Ui.text(requireContext(), title, 18, Typeface.BOLD, R.color.text_primary);
        TextView bodyView = Ui.text(requireContext(), bodyText, 14, Typeface.NORMAL, R.color.text_secondary);
        textBlock.addView(labelView);
        textBlock.addView(titleView);
        textBlock.addView(bodyView);
        body.addView(textBlock);
        card.addView(body);
        return card;
    }
}
