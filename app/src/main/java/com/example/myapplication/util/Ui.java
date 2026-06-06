package com.example.myapplication.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public final class Ui {
    private Ui() {
    }

    public static LinearLayout page(Context context) {
        LinearLayout root = new LinearLayout(context);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(context.getColor(R.color.surface_background));
        root.setPadding(dp(context, 20), dp(context, 32), dp(context, 20), dp(context, 24));
        return root;
    }

    public static ScrollView scrollPage(Context context, LinearLayout content) {
        ScrollView scrollView = new ScrollView(context);
        scrollView.setFillViewport(true);
        scrollView.addView(content);
        return scrollView;
    }

    public static TextView title(Context context, String text) {
        TextView view = text(context, text, 26, Typeface.BOLD, R.color.text_primary);
        view.setGravity(Gravity.START);
        return view;
    }

    public static TextView body(Context context, String text) {
        TextView view = text(context, text, 14, Typeface.NORMAL, R.color.text_secondary);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, dp(context, 8), 0, dp(context, 14));
        view.setLayoutParams(params);
        return view;
    }

    public static TextView text(Context context, String text, int size, int style, int colorRes) {
        TextView view = new TextView(context);
        view.setText(text);
        view.setTextSize(size);
        view.setTypeface(Typeface.DEFAULT, style);
        view.setTextColor(context.getColor(colorRes));
        view.setLineSpacing(dp(context, 2), 1.0f);
        return view;
    }

    public static TextInputLayout input(Context context, String hint, boolean password) {
        TextInputLayout layout = new TextInputLayout(context);
        layout.setHint(hint);
        layout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
        layout.setBoxBackgroundColor(context.getColor(R.color.surface_card));
        layout.setBoxStrokeColor(context.getColor(R.color.border_soft));
        layout.setHintTextColor(ColorStateList.valueOf(context.getColor(R.color.text_secondary)));
        layout.setBoxCornerRadii(dp(context, 24), dp(context, 24), dp(context, 24), dp(context, 24));
        TextInputEditText editText = new TextInputEditText(context);
        editText.setSingleLine(true);
        if (password) {
            editText.setInputType(0x00000081);
        }
        layout.addView(editText);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, dp(context, 8), 0, dp(context, 8));
        layout.setLayoutParams(params);
        return layout;
    }

    public static String value(TextInputLayout layout) {
        if (layout.getEditText() == null) {
            return "";
        }
        return layout.getEditText().getText() == null ? "" : layout.getEditText().getText().toString().trim();
    }

    public static MaterialButton button(Context context, String label, int colorRes) {
        MaterialButton button = new MaterialButton(context);
        button.setText(label);
        button.setAllCaps(false);
        button.setTextColor(context.getColor(R.color.white));
        button.setTextSize(15);
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        button.setBackgroundTintList(ColorStateList.valueOf(context.getColor(colorRes)));
        button.setCornerRadius(dp(context, 24));
        button.setElevation(dp(context, 2));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(context, 50));
        params.setMargins(0, dp(context, 8), 0, dp(context, 8));
        button.setLayoutParams(params);
        return button;
    }

    public static MaterialButton textButton(Context context, String label) {
        MaterialButton button = new MaterialButton(context);
        button.setText(label);
        button.setAllCaps(false);
        button.setTextColor(context.getColor(R.color.text_secondary));
        button.setBackgroundTintList(ColorStateList.valueOf(context.getColor(android.R.color.transparent)));
        button.setStrokeWidth(0);
        button.setCornerRadius(dp(context, 24));
        return button;
    }

    public static MaterialCardView card(Context context) {
        MaterialCardView card = new MaterialCardView(context);
        card.setCardBackgroundColor(context.getColor(R.color.surface_card));
        card.setRadius(dp(context, 24));
        card.setCardElevation(dp(context, 2));
        card.setStrokeWidth(dp(context, 1));
        card.setStrokeColor(context.getColor(R.color.border_soft));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, dp(context, 8), 0, dp(context, 8));
        card.setLayoutParams(params);
        return card;
    }

    public static GradientDrawable rounded(Context context, int colorRes, int radiusDp) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(context.getColor(colorRes));
        drawable.setCornerRadius(dp(context, radiusDp));
        return drawable;
    }

    public static int dp(Context context, int value) {
        return Math.round(value * context.getResources().getDisplayMetrics().density);
    }

    public static void setEnabled(boolean enabled, View... views) {
        for (View view : views) {
            view.setEnabled(enabled);
        }
    }
}
