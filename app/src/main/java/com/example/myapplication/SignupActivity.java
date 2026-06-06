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

public class SignupActivity extends AppCompatActivity {
    private final AuthRepository authRepository = RepositoryProvider.auth();
    private TextInputLayout emailInput;
    private TextInputLayout passwordInput;
    private TextInputLayout passwordConfirmInput;
    private TextInputLayout nicknameInput;
    private MaterialButton signupButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        passwordConfirmInput = findViewById(R.id.password_confirm_input);
        nicknameInput = findViewById(R.id.nickname_input);
        signupButton = findViewById(R.id.signup_submit_button);
        progressBar = findViewById(R.id.progress_bar);

        signupButton.setOnClickListener(v -> signup());
    }

    private void signup() {
        String email = Ui.value(emailInput);
        String password = Ui.value(passwordInput);
        String passwordConfirm = Ui.value(passwordConfirmInput);
        String nickname = Ui.value(nicknameInput);
        if (email.isEmpty() || password.isEmpty() || nickname.isEmpty()) {
            Toast.makeText(this, "이메일, 비밀번호, 닉네임을 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(passwordConfirm)) {
            Toast.makeText(this, "비밀번호 확인이 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);
        authRepository.signup(email, password, nickname, new AppCallback<UserProfile>() {
            @Override
            public void onSuccess(UserProfile profile) {
                setLoading(false);
                startActivity(new Intent(SignupActivity.this, TeamSelectActivity.class));
                finish();
            }

            @Override
            public void onError(String message) {
                setLoading(false);
                Toast.makeText(SignupActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? android.view.View.VISIBLE : android.view.View.GONE);
        Ui.setEnabled(!loading, emailInput, passwordInput, passwordConfirmInput, nicknameInput, signupButton);
    }
}
