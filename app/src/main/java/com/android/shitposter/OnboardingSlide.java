package com.android.shitposter;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

final class OnboardingSlide {
    @DrawableRes
    final int imageResId;
    @StringRes
    final int titleResId;
    @StringRes
    final int descriptionResId;

    OnboardingSlide(@DrawableRes int imageResId, @StringRes int titleResId, @StringRes int descriptionResId) {
        this.imageResId = imageResId;
        this.titleResId = titleResId;
        this.descriptionResId = descriptionResId;
    }
}

