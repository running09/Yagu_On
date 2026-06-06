package com.example.myapplication;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.myapplication.data.AppCallback;
import com.example.myapplication.data.AuthRepository;
import com.example.myapplication.data.RepositoryProvider;
import com.example.myapplication.data.UserRepository;
import com.example.myapplication.model.Team;
import com.example.myapplication.model.UserProfile;
import com.example.myapplication.util.TeamCatalog;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class MainActivity extends AppCompatActivity implements MainScreenHost {
    private final AuthRepository authRepository = RepositoryProvider.auth();
    private final UserRepository userRepository = RepositoryProvider.users();

    private TextView screenTitle;
    private TextView screenSubtitle;
    private View teamScroll;
    private ChipGroup teamChipGroup;
    private BottomNavigationView bottomNavigation;
    private UserProfile currentProfile;
    private String selectedTeamId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        screenTitle = findViewById(R.id.screen_title);
        screenSubtitle = findViewById(R.id.screen_subtitle);
        teamScroll = findViewById(R.id.team_scroll);
        teamChipGroup = findViewById(R.id.team_chip_group);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        setupBottomNavigation();
        loadProfile();
    }

    private void loadProfile() {
        setScreenHeader("불러오는 중", "회원 정보와 선호 팀을 확인하고 있습니다.");
        userRepository.getCurrentUser(new AppCallback<UserProfile>() {
            @Override
            public void onSuccess(UserProfile profile) {
                currentProfile = profile;
                if (!profile.hasFavoriteTeam()) {
                    openTeamSelect();
                    finish();
                    return;
                }
                selectedTeamId = profile.favoriteTeamId;
                setupTeamChips();
                bottomNavigation.setSelectedItemId(R.id.nav_home);
                showFragment(HomeFragment.newInstance(selectedTeamId, profile.nickname, profile.email));
            }

            @Override
            public void onError(String message) {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                authRepository.logout();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            if (selectedTeamId == null || currentProfile == null) {
                return true;
            }
            int id = item.getItemId();
            setTeamSelectorVisible(id != R.id.nav_profile);
            if (id == R.id.nav_home) {
                showFragment(HomeFragment.newInstance(selectedTeamId, currentProfile.nickname, currentProfile.email));
            } else if (id == R.id.nav_groups) {
                showFragment(GroupsFragment.newInstance(selectedTeamId, currentProfile.nickname, currentProfile.email, currentProfile.uid));
            } else if (id == R.id.nav_songs) {
                showFragment(SongsFragment.newInstance(selectedTeamId, currentProfile.nickname, currentProfile.email));
            } else if (id == R.id.nav_profile) {
                showFragment(ProfileFragment.newInstance(selectedTeamId, currentProfile.nickname, currentProfile.email));
            }
            return true;
        });
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void setTeamSelectorVisible(boolean visible) {
        if (teamScroll != null) {
            teamScroll.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    private void setupTeamChips() {
        teamChipGroup.removeAllViews();
        for (Team team : TeamCatalog.all()) {
            Chip chip = new Chip(this);
            chip.setText(team.name);
            chip.setCheckable(true);
            chip.setChecked(team.id.equals(selectedTeamId));
            chip.setChipCornerRadius(getResources().getDisplayMetrics().density * 24);
            chip.setChipStrokeWidth(getResources().getDisplayMetrics().density);
            chip.setOnClickListener(v -> changeTeam(team.id));
            teamChipGroup.addView(chip);
        }
        updateTeamChipStyles();
    }

    private void changeTeam(String teamId) {
        if (teamId.equals(selectedTeamId)) {
            return;
        }
        selectedTeamId = teamId;
        updateTeamChipStyles();
        userRepository.updateFavoriteTeam(teamId, new AppCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                currentProfile.favoriteTeamId = teamId;
                refreshCurrentTab();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                refreshCurrentTab();
            }
        });
    }

    private void refreshCurrentTab() {
        int selectedItemId = bottomNavigation.getSelectedItemId();
        if (selectedItemId == 0) {
            selectedItemId = R.id.nav_home;
        }
        goToTab(selectedItemId);
    }

    private void updateTeamChipStyles() {
        for (int i = 0; i < teamChipGroup.getChildCount(); i++) {
            Chip chip = (Chip) teamChipGroup.getChildAt(i);
            Team team = TeamCatalog.all().get(i);
            boolean selected = team.id.equals(selectedTeamId);
            chip.setChecked(selected);
            chip.setTextColor(getColor(selected ? R.color.white : R.color.text_secondary));
            chip.setChipBackgroundColor(ColorStateList.valueOf(getColor(selected ? R.color.brand_red : R.color.surface_card)));
            chip.setChipStrokeColor(ColorStateList.valueOf(getColor(selected ? R.color.brand_red : R.color.border_soft)));
        }
    }

    @Override
    public void setScreenHeader(String title, String subtitle) {
        screenTitle.setText(title);
        screenSubtitle.setText(subtitle);
    }

    @Override
    public void goToTab(int menuItemId) {
        bottomNavigation.setSelectedItemId(menuItemId);
    }

    @Override
    public void openTeamSelect() {
        startActivity(new Intent(this, TeamSelectActivity.class));
    }

    @Override
    public void logout() {
        authRepository.logout();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
