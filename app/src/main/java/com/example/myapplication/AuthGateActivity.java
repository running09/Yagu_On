package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.data.AppCallback;
import com.example.myapplication.data.AuthRepository;
import com.example.myapplication.data.RepositoryProvider;
import com.example.myapplication.data.UserRepository;
import com.example.myapplication.model.UserProfile;
import com.example.myapplication.util.FirebaseSetup;
import com.google.android.material.button.MaterialButton;

public class AuthGateActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!FirebaseSetup.isReady(this)) {
            showFirebaseSetupGuide();
            return;
        }

        AuthRepository authRepository = RepositoryProvider.auth();
        if (!authRepository.isSignedIn()) {
            open(LoginActivity.class);
            return;
        }

        UserRepository userRepository = RepositoryProvider.users();
        userRepository.getCurrentUser(new AppCallback<UserProfile>() {
            @Override
            public void onSuccess(UserProfile profile) {
                open(profile.hasFavoriteTeam() ? MainActivity.class : TeamSelectActivity.class);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(AuthGateActivity.this, message, Toast.LENGTH_LONG).show();
                open(LoginActivity.class);
            }
        });
    }

    private void showFirebaseSetupGuide() {
        setContentView(R.layout.activity_auth_gate);
        MaterialButton retry = findViewById(R.id.retry_button);
        retry.setOnClickListener(v -> recreate());
    }

    private void open(Class<?> target) {
        Intent intent = new Intent(this, target);
        startActivity(intent);
        finish();
    }
}
