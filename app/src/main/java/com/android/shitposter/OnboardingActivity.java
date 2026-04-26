package com.android.shitposter;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.android.shitposter.databinding.ActivityOnboardingBinding;

import java.util.Arrays;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    public static final String EXTRA_FORCE_SHOW = "force_show";

    private ActivityOnboardingBinding binding;
    private List<OnboardingSlide> slides;
    private ImageView[] dots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (AppPrefs.isLoggedIn(this)) {
            goToMain();
            return;
        }

        boolean forceShow = getIntent() != null && getIntent().getBooleanExtra(EXTRA_FORCE_SHOW, false);
        if (!forceShow && AppPrefs.isOnboardingCompleted(this)) {
            startActivity(new Intent(this, AuthLandingActivity.class));
            finish();
            return;
        }

        binding = ActivityOnboardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        slides = Arrays.asList(
                new OnboardingSlide(
                        R.drawable.slide_1,
                        R.string.onboarding_title_1,
                        R.string.onboarding_desc_1
                ),
                new OnboardingSlide(
                        R.drawable.create_share_repeat,
                        R.string.onboarding_title_2,
                        R.string.onboarding_desc_2
                ),
                new OnboardingSlide(
                        R.drawable.ear_from_shitposting,
                        R.string.onboarding_title_3,
                        R.string.onboarding_desc_3
                )
        );

        binding.viewPager.setAdapter(new OnboardingAdapter(slides));
        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateUi(position);
            }
        });

        setupDots(slides.size());
        updateUi(0);

        binding.buttonBack.setOnClickListener(v -> {
            int current = binding.viewPager.getCurrentItem();
            if (current > 0) {
                binding.viewPager.setCurrentItem(current - 1, true);
            }
        });

        binding.buttonNext.setOnClickListener(v -> {
            int current = binding.viewPager.getCurrentItem();
            if (current < slides.size() - 1) {
                binding.viewPager.setCurrentItem(current + 1, true);
            } else {
                completeAndGoToMain();
            }
        });

        binding.buttonSkip.setOnClickListener(v -> completeAndGoToMain());
    }

    private void setupDots(int count) {
        dots = new ImageView[count];
        binding.dots.removeAllViews();

        int sizePx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                8,
                getResources().getDisplayMetrics()
        );
        int marginPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                6,
                getResources().getDisplayMetrics()
        );

        for (int i = 0; i < count; i++) {
            ImageView dot = new ImageView(this);
            dot.setImageResource(R.drawable.onboarding_dot_unselected);
            dot.setLayoutParams(new android.widget.LinearLayout.LayoutParams(sizePx, sizePx));
            android.widget.LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) dot.getLayoutParams();
            lp.setMargins(marginPx, marginPx, marginPx, marginPx);
            dot.setLayoutParams(lp);
            dots[i] = dot;
            binding.dots.addView(dot);
        }
    }

    private void updateUi(int position) {
        for (int i = 0; i < dots.length; i++) {
            dots[i].setImageResource(i == position
                    ? R.drawable.onboarding_dot_selected
                    : R.drawable.onboarding_dot_unselected
            );
        }

        boolean isFirst = position == 0;
        boolean isLast = position == slides.size() - 1;

        binding.buttonBack.setVisibility(isFirst ? View.INVISIBLE : View.VISIBLE);
        binding.buttonSkip.setVisibility(isLast ? View.INVISIBLE : View.VISIBLE);
        binding.buttonNext.setText(isLast ? R.string.get_started : R.string.next);
    }

    private void completeAndGoToMain() {
        AppPrefs.setOnboardingCompleted(this, true);
        Intent intent = new Intent(this, AuthLandingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void goToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
