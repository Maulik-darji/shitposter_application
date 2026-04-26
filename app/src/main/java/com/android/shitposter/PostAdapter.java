package com.android.shitposter;

import android.content.Intent;
import android.widget.Toast;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.shitposter.databinding.ItemPostBinding;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

final class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private final DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
    private final List<Post> posts = new ArrayList<>();

    void submitList(@NonNull List<Post> newPosts) {
        posts.clear();
        posts.addAll(newPosts);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPostBinding binding = ItemPostBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);

        PostActionsStore.ensureInitialized(holder.binding.getRoot().getContext(), post);

        holder.binding.content.setText(post.text);
        String meta = "@" + holder.binding.getRoot().getContext().getString(R.string.app_name).toLowerCase()
                + " · " + timeFormat.format(new Date(post.createdAtMillis));
        holder.binding.meta.setText(meta);

        bindActions(holder, post);

        holder.binding.getRoot().setOnClickListener(v -> {
            android.content.Context context = v.getContext();
            Intent intent = new Intent(context, PostDetailActivity.class);
            intent.putExtra(PostDetailActivity.EXTRA_POST_ID, post.id);
            context.startActivity(intent);
        });
    }

    private void bindActions(@NonNull ViewHolder holder, @NonNull Post post) {
        android.content.Context context = holder.binding.getRoot().getContext();

        boolean liked = PostActionsStore.isLiked(context, post.id);
        boolean saved = PostActionsStore.isSaved(context, post.id);

        holder.binding.iconLike.setImageResource(liked ? R.drawable.ic_shit_filled : R.drawable.ic_shit_outline);
        holder.binding.textLike.setText(PostActionsStore.formatCount(PostActionsStore.getLikes(context, post.id)));

        holder.binding.textRepost.setText(PostActionsStore.formatCount(PostActionsStore.getReposts(context, post.id)));
        holder.binding.textComment.setText(PostActionsStore.formatCount(PostActionsStore.getComments(context, post.id)));
        holder.binding.textAnalytics.setText(PostActionsStore.formatCount(PostActionsStore.getViews(context, post.id)));

        holder.binding.iconSave.setImageResource(saved ? R.drawable.ic_action_save_filled : R.drawable.ic_action_save_outline);

        holder.binding.actionLike.setOnClickListener(v -> {
            PostActionsStore.toggleLike(context, post.id);
            notifyItemChanged(holder.getBindingAdapterPosition());
        });

        holder.binding.actionSave.setOnClickListener(v -> {
            PostActionsStore.toggleSave(context, post.id);
            notifyItemChanged(holder.getBindingAdapterPosition());
        });

        holder.binding.actionRepost.setOnClickListener(v ->
                Toast.makeText(context, "Repost coming soon", Toast.LENGTH_SHORT).show()
        );
        holder.binding.actionComment.setOnClickListener(v ->
                Toast.makeText(context, "Comments coming soon", Toast.LENGTH_SHORT).show()
        );
        holder.binding.actionAnalytics.setOnClickListener(v ->
                Toast.makeText(context, "Analytics coming soon", Toast.LENGTH_SHORT).show()
        );

        holder.binding.actionShare.setOnClickListener(v -> {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, post.text);
            context.startActivity(Intent.createChooser(share, context.getString(R.string.share)));
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    static final class ViewHolder extends RecyclerView.ViewHolder {
        final ItemPostBinding binding;

        ViewHolder(ItemPostBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
