package com.android.shitposter;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import java.util.Locale;

final class PostActionsStore {
    private static final String PREFS = "post_actions";

    private PostActionsStore() {
    }

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    private static String k(String prefix, String postId) {
        return prefix + "_" + postId;
    }

    static void ensureInitialized(@NonNull Context context, @NonNull Post post) {
        SharedPreferences p = prefs(context);
        String initKey = k("init", post.id);
        if (p.getBoolean(initKey, false)) return;

        int base = Math.abs(post.id.hashCode());
        int views = 200 + (base % 9800);
        int likes = (base / 10) % 120;
        int reposts = (base / 100) % 40;
        int comments = (base / 1000) % 30;

        p.edit()
                .putBoolean(initKey, true)
                .putInt(k("views", post.id), views)
                .putInt(k("likes", post.id), likes)
                .putInt(k("reposts", post.id), reposts)
                .putInt(k("comments", post.id), comments)
                .putBoolean(k("liked", post.id), false)
                .putBoolean(k("saved", post.id), false)
                .apply();
    }

    static boolean isLiked(@NonNull Context context, @NonNull String postId) {
        return prefs(context).getBoolean(k("liked", postId), false);
    }

    static boolean isSaved(@NonNull Context context, @NonNull String postId) {
        return prefs(context).getBoolean(k("saved", postId), false);
    }

    static int getLikes(@NonNull Context context, @NonNull String postId) {
        return prefs(context).getInt(k("likes", postId), 0);
    }

    static int getReposts(@NonNull Context context, @NonNull String postId) {
        return prefs(context).getInt(k("reposts", postId), 0);
    }

    static int getComments(@NonNull Context context, @NonNull String postId) {
        return prefs(context).getInt(k("comments", postId), 0);
    }

    static int getViews(@NonNull Context context, @NonNull String postId) {
        return prefs(context).getInt(k("views", postId), 0);
    }

    static void toggleLike(@NonNull Context context, @NonNull String postId) {
        SharedPreferences p = prefs(context);
        boolean liked = p.getBoolean(k("liked", postId), false);
        int likes = p.getInt(k("likes", postId), 0);
        int nextLikes = liked ? Math.max(0, likes - 1) : likes + 1;
        p.edit()
                .putBoolean(k("liked", postId), !liked)
                .putInt(k("likes", postId), nextLikes)
                .apply();
    }

    static void toggleSave(@NonNull Context context, @NonNull String postId) {
        SharedPreferences p = prefs(context);
        boolean saved = p.getBoolean(k("saved", postId), false);
        p.edit().putBoolean(k("saved", postId), !saved).apply();
    }

    @NonNull
    static String formatCount(int value) {
        if (value < 1000) return String.valueOf(value);
        if (value < 1_000_000) return String.format(Locale.US, "%.1fK", value / 1000.0);
        return String.format(Locale.US, "%.1fM", value / 1_000_000.0);
    }
}

