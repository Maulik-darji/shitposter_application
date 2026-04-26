package com.android.shitposter;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

final class PostStore {
    private static final String PREFS_NAME = "posts";
    private static final String KEY_POSTS_JSON = "posts_json";

    private PostStore() {
    }

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    static void addPost(@NonNull Context context, @NonNull String text) {
        String trimmed = text.trim();
        if (trimmed.isEmpty()) return;

        List<Post> current = getPosts(context);
        List<Post> updated = new ArrayList<>(current.size() + 1);
        updated.add(new Post(UUID.randomUUID().toString(), trimmed, System.currentTimeMillis()));
        updated.addAll(current);
        save(context, updated);
    }

    @NonNull
    static List<Post> getPosts(@NonNull Context context) {
        String json = prefs(context).getString(KEY_POSTS_JSON, null);
        if (json == null || json.trim().isEmpty()) {
            return seedPosts();
        }
        try {
            JSONArray arr = new JSONArray(json);
            List<Post> posts = new ArrayList<>(arr.length());
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                posts.add(new Post(
                        o.optString("id"),
                        o.optString("text"),
                        o.optLong("createdAtMillis")
                ));
            }
            return posts;
        } catch (Exception ignored) {
            return seedPosts();
        }
    }

    private static void save(@NonNull Context context, @NonNull List<Post> posts) {
        JSONArray arr = new JSONArray();
        try {
            for (Post p : posts) {
                JSONObject o = new JSONObject();
                o.put("id", p.id);
                o.put("text", p.text);
                o.put("createdAtMillis", p.createdAtMillis);
                arr.put(o);
            }
        } catch (Exception ignored) {
        }
        prefs(context).edit().putString(KEY_POSTS_JSON, arr.toString()).apply();
    }

    static Post getPostById(@NonNull Context context, String postId) {
        if (postId == null) return null;
        for (Post p : getPosts(context)) {
            if (postId.equals(p.id)) return p;
        }
        return null;
    }

    @NonNull
    private static List<Post> seedPosts() {
        List<Post> seed = new ArrayList<>();
        seed.add(new Post("seed-1", "Welcome to Shitposter. Text-only chaos starts here.", System.currentTimeMillis() - 60_000));
        seed.add(new Post("seed-2", "Post something. Keep it short. Keep it spicy.", System.currentTimeMillis() - 5 * 60_000));
        seed.add(new Post("seed-3", "For you vs Following — just like Twitter, but for shitposting.", System.currentTimeMillis() - 12 * 60_000));
        return Collections.unmodifiableList(seed);
    }
}
