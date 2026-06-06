package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.data.AppCallback;
import com.example.myapplication.data.AuthRepository;
import com.example.myapplication.data.RepositoryProvider;
import com.example.myapplication.model.UserProfile;
import com.example.myapplication.util.Ui;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {
    private final AuthRepository authRepository = RepositoryProvider.auth();
    private TextInputLayout emailInput;
    private TextInputLayout passwordInput;
    private MaterialButton loginButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.login_button);
        MaterialButton signupButton = findViewById(R.id.signup_button);
        progressBar = findViewById(R.id.progress_bar);

        loginButton.setOnClickListener(v -> login());
        signupButton.setOnClickListener(v -> startActivity(new Intent(this, SignupActivity.class)));
    }

    private void login() {
        String email = Ui.value(emailInput);
        String password = Ui.value(passwordInput);
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "이메일과 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);
        authRepository.login(email, password, new AppCallback<UserProfile>() {
            @Override
            public void onSuccess(UserProfile profile) {
                setLoading(false);
                startActivity(new Intent(LoginActivity.this, profile.hasFavoriteTeam() ? MainActivity.class : TeamSelectActivity.class));
                finish();
            }

            @Override
            public void onError(String message) {
                setLoading(false);
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? android.view.View.VISIBLE : android.view.View.GONE);
        Ui.setEnabled(!loading, emailInput, passwordInput, loginButton);
    }
}
