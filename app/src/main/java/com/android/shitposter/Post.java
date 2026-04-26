package com.android.shitposter;

final class Post {
    final String id;
    final String text;
    final long createdAtMillis;

    Post(String id, String text, long createdAtMillis) {
        this.id = id;
        this.text = text;
        this.createdAtMillis = createdAtMillis;
    }
}

