package com.android.shitposter;

import android.content.Context;
import android.content.SharedPreferences;

final class AppPrefs {
    private static final String PREFS_NAME = "app_prefs";

    private static final String KEY_ONBOARDING_COMPLETED = "onboarding_completed";
    private static final String KEY_LOGGED_IN = "logged_in";
    private static final String KEY_PHONE_NUMBER = "phone_number";

    private AppPrefs() {
    }

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    static boolean isOnboardingCompleted(Context context) {
        return prefs(context).getBoolean(KEY_ONBOARDING_COMPLETED, false);
    }

    static void setOnboardingCompleted(Context context, boolean completed) {
        prefs(context).edit().putBoolean(KEY_ONBOARDING_COMPLETED, completed).apply();
    }

    static boolean isLoggedIn(Context context) {
        return prefs(context).getBoolean(KEY_LOGGED_IN, false);
    }

    static void setLoggedIn(Context context, boolean loggedIn, String phoneNumber) {
        prefs(context)
                .edit()
                .putBoolean(KEY_LOGGED_IN, loggedIn)
                .putString(KEY_PHONE_NUMBER, phoneNumber)
                .apply();
    }

    static String getPhoneNumber(Context context) {
        return prefs(context).getString(KEY_PHONE_NUMBER, null);
    }
}

