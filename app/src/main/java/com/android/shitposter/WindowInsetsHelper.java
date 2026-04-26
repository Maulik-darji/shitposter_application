package com.android.shitposter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

final class WindowInsetsHelper {
    private WindowInsetsHelper() {
    }

    static void applyTopInsetsPadding(@NonNull View view) {
        int initialTop = view.getPaddingTop();
        int initialLeft = view.getPaddingLeft();
        int initialRight = view.getPaddingRight();
        int initialBottom = view.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            int topInset = insets.getInsets(
                    WindowInsetsCompat.Type.statusBars() | WindowInsetsCompat.Type.displayCutout()
            ).top;
            v.setPadding(initialLeft, initialTop + topInset, initialRight, initialBottom);
            return insets;
        });

        ViewCompat.requestApplyInsets(view);
    }

    static void applyBottomInsetsPadding(@NonNull View view, boolean includeIme) {
        int initialTop = view.getPaddingTop();
        int initialLeft = view.getPaddingLeft();
        int initialRight = view.getPaddingRight();
        int initialBottom = view.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            int types = WindowInsetsCompat.Type.navigationBars();
            if (includeIme) types |= WindowInsetsCompat.Type.ime();

            int bottomInset = insets.getInsets(types).bottom;
            v.setPadding(initialLeft, initialTop, initialRight, initialBottom + bottomInset);
            return insets;
        });

        ViewCompat.requestApplyInsets(view);
    }
}
