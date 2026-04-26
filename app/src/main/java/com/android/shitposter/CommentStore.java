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

final class CommentStore {
    private static final String PREFS_NAME = "comments";

    private CommentStore() {
    }

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    private static String key(String postId) {
        return "comments_json_" + postId;
    }

    static void addComment(@NonNull Context context, @NonNull String postId, @NonNull String text) {
        String trimmed = text.trim();
        if (trimmed.isEmpty()) return;

        List<Comment> current = getComments(context, postId);
        List<Comment> updated = new ArrayList<>(current.size() + 1);
        updated.add(new Comment(UUID.randomUUID().toString(), postId, trimmed, System.currentTimeMillis()));
        updated.addAll(current);
        save(context, postId, updated);
    }

    @NonNull
    static List<Comment> getComments(@NonNull Context context, @NonNull String postId) {
        String json = prefs(context).getString(key(postId), null);
        if (json == null || json.trim().isEmpty()) {
            return seedComments(postId);
        }
        try {
            JSONArray arr = new JSONArray(json);
            List<Comment> comments = new ArrayList<>(arr.length());
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                comments.add(new Comment(
                        o.optString("id"),
                        postId,
                        o.optString("text"),
                        o.optLong("createdAtMillis")
                ));
            }
            return comments;
        } catch (Exception ignored) {
            return seedComments(postId);
        }
    }

    private static void save(@NonNull Context context, @NonNull String postId, @NonNull List<Comment> comments) {
        JSONArray arr = new JSONArray();
        try {
            for (Comment c : comments) {
                JSONObject o = new JSONObject();
                o.put("id", c.id);
                o.put("text", c.text);
                o.put("createdAtMillis", c.createdAtMillis);
                arr.put(o);
            }
        } catch (Exception ignored) {
        }
        prefs(context).edit().putString(key(postId), arr.toString()).apply();
    }

    @NonNull
    private static List<Comment> seedComments(@NonNull String postId) {
        List<Comment> seed = new ArrayList<>();
        long now = System.currentTimeMillis();
        seed.add(new Comment("seed-c1-" + postId, postId, "First comment. Absolute cinema.", now - 2 * 60_000));
        seed.add(new Comment("seed-c2-" + postId, postId, "This is the kind of shitposting I signed up for.", now - 10 * 60_000));
        return Collections.unmodifiableList(seed);
    }
}

