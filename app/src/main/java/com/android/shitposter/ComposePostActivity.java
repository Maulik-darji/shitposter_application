package com.android.shitposter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.shitposter.databinding.ActivityComposePostBinding;

public class ComposePostActivity extends AppCompatActivity {

    private ActivityComposePostBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityComposePostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        WindowInsetsHelper.applyTopInsetsPadding(binding.topBar);
        WindowInsetsHelper.applyBottomInsetsPadding(binding.bottomRow, true);

        binding.closeButton.setOnClickListener(v -> finish());

        binding.postInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(280)});
        binding.postInput.requestFocus();
        binding.postInput.post(() -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(binding.postInput, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        binding.postButton.setOnClickListener(v -> {
            String text = binding.postInput.getText() == null ? "" : binding.postInput.getText().toString();
            PostStore.addPost(this, text);
            setResult(Activity.RESULT_OK);
            finish();
        });

        binding.postInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                updatePostEnabledState();
            }
        });

        updatePostEnabledState();
    }

    private void updatePostEnabledState() {
        boolean enabled = binding.postInput.getText() != null && !binding.postInput.getText().toString().trim().isEmpty();
        binding.postButton.setEnabled(enabled);
        binding.postButton.setAlpha(enabled ? 1f : 0.5f);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}

