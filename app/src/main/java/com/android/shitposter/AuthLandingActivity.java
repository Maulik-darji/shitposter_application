package com.android.shitposter;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.shitposter.databinding.ActivityAuthLandingBinding;

public class AuthLandingActivity extends AppCompatActivity {

    private ActivityAuthLandingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (AppPrefs.isLoggedIn(this)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        binding = ActivityAuthLandingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        WindowInsetsHelper.applyTopInsetsPadding(binding.toolbar);

        binding.toolbar.setNavigationOnClickListener(v -> goBackToOnboarding());
        binding.toolbar.inflateMenu(R.menu.menu_auth_landing);
        binding.toolbar.setOnMenuItemClickListener(this::onToolbarMenuItem);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                goBackToOnboarding();
            }
        });

        binding.buttonGoogle.setOnClickListener(v -> openGoogleAuth());
        binding.buttonPhone.setOnClickListener(v -> openPhoneAuth(PhoneAuthActivity.MODE_LOGIN));
        binding.buttonCreate.setOnClickListener(v -> openPhoneAuth(PhoneAuthActivity.MODE_SIGNUP));
        binding.linkLogin.setOnClickListener(v -> openPhoneAuth(PhoneAuthActivity.MODE_LOGIN));

        setupLegalLinks();
    }

    private boolean onToolbarMenuItem(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_skip) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        return false;
    }

    private void goBackToOnboarding() {
        Intent intent = new Intent(this, OnboardingActivity.class);
        intent.putExtra(OnboardingActivity.EXTRA_FORCE_SHOW, true);
        startActivity(intent);
        finish();
    }

    private void openGoogleAuth() {
        // Placeholder for Google Sign-In logic
        // Typically involves using GoogleSignInClient and launching an intent
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void openPhoneAuth(@NonNull String mode) {
        Intent intent = new Intent(this, PhoneAuthActivity.class);
        intent.putExtra(PhoneAuthActivity.EXTRA_MODE, mode);
        startActivity(intent);
    }

    private void setupLegalLinks() {
        String full = getString(R.string.auth_legal_text);
        SpannableString spannable = new SpannableString(full);

        String terms = getString(R.string.terms);
        String privacy = getString(R.string.privacy_policy);

        int termsStart = full.indexOf(terms);
        int privacyStart = full.indexOf(privacy);

        if (termsStart >= 0) {
            spannable.setSpan(new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    startActivity(new Intent(AuthLandingActivity.this, TermsActivity.class));
                }
            }, termsStart, termsStart + terms.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if (privacyStart >= 0) {
            spannable.setSpan(new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    startActivity(new Intent(AuthLandingActivity.this, PrivacyPolicyActivity.class));
                }
            }, privacyStart, privacyStart + privacy.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        binding.textLegal.setText(spannable);
        binding.textLegal.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
