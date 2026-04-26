package com.android.shitposter;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.shitposter.databinding.ActivityPostDetailBinding;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class PostDetailActivity extends AppCompatActivity {
    public static final String EXTRA_POST_ID = "post_id";

    private ActivityPostDetailBinding binding;
    private CommentAdapter commentAdapter;
    private final DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);

    private Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPostDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        WindowInsetsHelper.applyTopInsetsPadding(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        String postId = getIntent().getStringExtra(EXTRA_POST_ID);
        post = PostStore.getPostById(this, postId);
        if (post == null) {
            Toast.makeText(this, "Post not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        bindPostHeader();

        commentAdapter = new CommentAdapter();
        binding.recyclerComments.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerComments.setAdapter(commentAdapter);
        refreshComments();

        binding.buttonSend.setOnClickListener(v -> onSendComment());
    }

    private void bindPostHeader() {
        PostActionsStore.ensureInitialized(this, post);

        String meta = "@" + getString(R.string.app_name).toLowerCase() + " · " + timeFormat.format(new Date(post.createdAtMillis));
        binding.postHeader.meta.setText(meta);
        binding.postHeader.content.setText(post.text);

        updateActions();

        binding.postHeader.actionLike.setOnClickListener(v -> {
            PostActionsStore.toggleLike(this, post.id);
            updateActions();
        });
        binding.postHeader.actionSave.setOnClickListener(v -> {
            PostActionsStore.toggleSave(this, post.id);
            updateActions();
        });
        binding.postHeader.actionRepost.setOnClickListener(v ->
                Toast.makeText(this, "Repost coming soon", Toast.LENGTH_SHORT).show()
        );
        binding.postHeader.actionComment.setOnClickListener(v -> binding.inputComment.requestFocus());
        binding.postHeader.actionAnalytics.setOnClickListener(v ->
                Toast.makeText(this, "Analytics coming soon", Toast.LENGTH_SHORT).show()
        );
        binding.postHeader.actionShare.setOnClickListener(v -> {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, post.text);
            startActivity(Intent.createChooser(share, getString(R.string.share)));
        });
    }

    private void updateActions() {
        boolean liked = PostActionsStore.isLiked(this, post.id);
        boolean saved = PostActionsStore.isSaved(this, post.id);

        binding.postHeader.iconLike.setImageResource(liked ? R.drawable.ic_shit_filled : R.drawable.ic_shit_outline);
        binding.postHeader.textLike.setText(PostActionsStore.formatCount(PostActionsStore.getLikes(this, post.id)));

        binding.postHeader.textRepost.setText(PostActionsStore.formatCount(PostActionsStore.getReposts(this, post.id)));
        binding.postHeader.textComment.setText(PostActionsStore.formatCount(PostActionsStore.getComments(this, post.id)));
        binding.postHeader.textAnalytics.setText(PostActionsStore.formatCount(PostActionsStore.getViews(this, post.id)));

        binding.postHeader.iconSave.setImageResource(saved ? R.drawable.ic_action_save_filled : R.drawable.ic_action_save_outline);
    }

    private void refreshComments() {
        List<Comment> comments = CommentStore.getComments(this, post.id);
        commentAdapter.submitList(comments);
        binding.textCommentsTitle.setText(getString(R.string.comments_count, comments.size()));
    }

    private void onSendComment() {
        String text = binding.inputComment.getText() == null ? "" : binding.inputComment.getText().toString();
        if (TextUtils.isEmpty(text.trim())) return;

        CommentStore.addComment(this, post.id, text.trim());
        binding.inputComment.setText(null);
        refreshComments();
        binding.recyclerComments.scrollToPosition(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}

