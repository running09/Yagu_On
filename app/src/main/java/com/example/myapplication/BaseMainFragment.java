package com.example.myapplication;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.myapplication.model.Team;
import com.example.myapplication.util.TeamCatalog;
import com.example.myapplication.util.Ui;
import com.google.android.material.card.MaterialCardView;

public abstract class BaseMainFragment extends Fragment {
    protected String teamId;
    protected String nickname;
    protected String email;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            teamId = args.getString("teamId");
            nickname = args.getString("nickname");
            email = args.getString("email");
        }
    }

    protected Team selectedTeam() {
        return TeamCatalog.find(teamId);
    }

    protected MainScreenHost host() {
        return (MainScreenHost) requireActivity();
    }

    protected MaterialCardView infoCard(String label, String title, String bodyText) {
        MaterialCardView card = Ui.card(requireContext());
        LinearLayout body = new LinearLayout(requireContext());
        body.setOrientation(LinearLayout.VERTICAL);
        body.setPadding(Ui.dp(requireContext(), 16), Ui.dp(requireContext(), 16), Ui.dp(requireContext(), 16), Ui.dp(requireContext(), 16));
        TextView labelView = Ui.text(requireContext(), label, 12, Typeface.BOLD, R.color.brand_red);
        TextView titleView = Ui.text(requireContext(), title, 18, Typeface.BOLD, R.color.text_primary);
        TextView bodyView = Ui.text(requireContext(), bodyText, 14, Typeface.NORMAL, R.color.text_secondary);
        body.addView(labelView);
        body.addView(titleView);
        body.addView(bodyView);
        card.addView(body);
        return card;
    }

    protected void addSection(LinearLayout parent, String value) {
        TextView section = Ui.text(requireContext(), value, 18, Typeface.BOLD, R.color.text_primary);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, Ui.dp(requireContext(), 18), 0, Ui.dp(requireContext(), 4));
        parent.addView(section, params);
    }

    protected void addBodyText(LinearLayout parent, String value) {
        parent.addView(Ui.body(requireContext(), value));
    }
}
