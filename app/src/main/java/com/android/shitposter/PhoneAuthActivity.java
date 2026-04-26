package com.android.shitposter;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.shitposter.databinding.ActivityPhoneAuthBinding;

public class PhoneAuthActivity extends AppCompatActivity {
    static final String EXTRA_MODE = "mode";
    static final String MODE_LOGIN = "login";
    static final String MODE_SIGNUP = "signup";

    private ActivityPhoneAuthBinding binding;

    private boolean otpStep = false;
    private String phoneNumber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (AppPrefs.isLoggedIn(this)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        binding = ActivityPhoneAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        WindowInsetsHelper.applyTopInsetsPadding(binding.toolbar);

        binding.toolbar.inflateMenu(R.menu.menu_auth_landing);
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_skip) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });

        String mode = getIntent().getStringExtra(EXTRA_MODE);
        boolean signup = MODE_SIGNUP.equals(mode);
        binding.toolbar.setTitle(signup ? R.string.phone_auth_title_signup : R.string.phone_auth_title_login);
        binding.toolbar.setNavigationOnClickListener(v -> handleBack());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                handleBack();
            }
        });

        binding.buttonPrimary.setOnClickListener(v -> {
            if (!otpStep) {
                onSubmitPhone();
            } else {
                onSubmitOtp();
            }
        });

        showPhoneStep();
    }

    private void onSubmitPhone() {
        String input = binding.inputPhone.getText() == null ? "" : binding.inputPhone.getText().toString().trim();
        if (!isPhoneValid(input)) {
            binding.layoutPhone.setError(getString(R.string.invalid_phone));
            return;
        }
        binding.layoutPhone.setError(null);
        phoneNumber = input;
        showOtpStep();
    }

    private void onSubmitOtp() {
        String otp = binding.inputOtp.getText() == null ? "" : binding.inputOtp.getText().toString().trim();
        if (!isOtpValid(otp)) {
            binding.layoutOtp.setError(getString(R.string.invalid_otp));
            return;
        }
        binding.layoutOtp.setError(null);

        AppPrefs.setLoggedIn(this, true, phoneNumber);
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void showPhoneStep() {
        otpStep = false;
        binding.stepPhone.setVisibility(View.VISIBLE);
        binding.stepOtp.setVisibility(View.GONE);
        binding.buttonPrimary.setText(R.string.continue_button);
        binding.inputPhone.requestFocus();
    }

    private void showOtpStep() {
        otpStep = true;
        binding.stepPhone.setVisibility(View.GONE);
        binding.stepOtp.setVisibility(View.VISIBLE);
        binding.buttonPrimary.setText(R.string.verify_button);
        binding.textOtpHint.setText(getString(R.string.enter_otp_hint));
        binding.layoutOtp.setError(null);
        binding.inputOtp.setText(null);
        binding.inputOtp.requestFocus();
    }

    private boolean isPhoneValid(@NonNull String input) {
        if (TextUtils.isEmpty(input)) return false;
        int digits = 0;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (Character.isDigit(c)) digits++;
        }
        return digits >= 10;
    }

    private boolean isOtpValid(@NonNull String input) {
        if (input.length() != 6) return false;
        for (int i = 0; i < input.length(); i++) {
            if (!Character.isDigit(input.charAt(i))) return false;
        }
        return true;
    }

    private void handleBack() {
        if (otpStep) {
            showPhoneStep();
            return;
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
