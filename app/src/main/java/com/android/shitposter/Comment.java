package com.android.shitposter;

final class Comment {
    final String id;
    final String postId;
    final String text;
    final long createdAtMillis;

    Comment(String id, String postId, String text, long createdAtMillis) {
        this.id = id;
        this.postId = postId;
        this.text = text;
        this.createdAtMillis = createdAtMillis;
    }
}

