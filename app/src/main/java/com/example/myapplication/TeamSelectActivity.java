package com.example.myapplication;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.data.AppCallback;
import com.example.myapplication.data.RepositoryProvider;
import com.example.myapplication.data.UserRepository;
import com.example.myapplication.model.Team;
import com.example.myapplication.util.TeamCatalog;
import com.example.myapplication.util.Ui;
import com.google.android.material.card.MaterialCardView;

public class TeamSelectActivity extends AppCompatActivity {
    private final UserRepository userRepository = RepositoryProvider.users();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_select);
        GridLayout grid = findViewById(R.id.team_grid);
        for (Team team : TeamCatalog.all()) {
            grid.addView(teamCard(team));
        }
    }

    private MaterialCardView teamCard(Team team) {
        MaterialCardView card = Ui.card(this);
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = Ui.dp(this, 110);
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.setMargins(Ui.dp(this, 6), Ui.dp(this, 6), Ui.dp(this, 6), Ui.dp(this, 6));
        card.setLayoutParams(params);
        card.setCardBackgroundColor(getColor(R.color.surface_card));
        card.setRadius(Ui.dp(this, 24));
        card.setStrokeColor(getColor(R.color.brand_red));
        card.setStrokeWidth(Ui.dp(this, 1));

        LinearLayout body = new LinearLayout(this);
        body.setGravity(Gravity.CENTER);
        body.setOrientation(LinearLayout.VERTICAL);
        body.setPadding(Ui.dp(this, 10), Ui.dp(this, 12), Ui.dp(this, 10), Ui.dp(this, 12));

        TextView badge = Ui.text(this, team.shortName.substring(0, 1), 22, Typeface.BOLD, R.color.white);
        badge.setGravity(Gravity.CENTER);
        badge.setBackground(Ui.rounded(this, team.colorRes, 24));
        body.addView(badge, new LinearLayout.LayoutParams(Ui.dp(this, 52), Ui.dp(this, 52)));
        TextView title = Ui.text(this, team.name, 15, Typeface.BOLD, R.color.text_primary);
        title.setGravity(Gravity.CENTER);
        body.addView(title);
        card.addView(body);
        card.setOnClickListener(v -> selectTeam(team));
        return card;
    }

    private void selectTeam(Team team) {
        userRepository.updateFavoriteTeam(team.id, new AppCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                startActivity(new Intent(TeamSelectActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(TeamSelectActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
