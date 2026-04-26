package com.android.shitposter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        View content = findViewById(android.R.id.content);
        content.post(() -> {
            Class<?> target;
            if (AppPrefs.isLoggedIn(this)) {
                target = MainActivity.class;
            } else if (AppPrefs.isOnboardingCompleted(this)) {
                target = AuthLandingActivity.class;
            } else {
                target = OnboardingActivity.class;
            }

            Intent intent = new Intent(this, target);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}
