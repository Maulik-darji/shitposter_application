package com.android.shitposter;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.android.shitposter.databinding.ActivityLegalBinding;

public class TermsActivity extends AppCompatActivity {

    private ActivityLegalBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLegalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        WindowInsetsHelper.applyTopInsetsPadding(binding.toolbar);

        binding.toolbar.setTitle(R.string.terms_title);
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
