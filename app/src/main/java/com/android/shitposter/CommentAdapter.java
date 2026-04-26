package com.android.shitposter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.shitposter.databinding.ItemCommentBinding;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

final class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private final DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
    private final List<Comment> comments = new ArrayList<>();

    void submitList(@NonNull List<Comment> newComments) {
        comments.clear();
        comments.addAll(newComments);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCommentBinding binding = ItemCommentBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = comments.get(position);
        String meta = "@shitposter · " + timeFormat.format(new Date(comment.createdAtMillis));
        holder.binding.meta.setText(meta);
        holder.binding.content.setText(comment.text);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    static final class ViewHolder extends RecyclerView.ViewHolder {
        final ItemCommentBinding binding;

        ViewHolder(ItemCommentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

