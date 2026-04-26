package com.android.shitposter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.shitposter.databinding.ItemOnboardingPageBinding;

import java.util.List;

final class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.ViewHolder> {
    private final List<OnboardingSlide> slides;

    OnboardingAdapter(List<OnboardingSlide> slides) {
        this.slides = slides;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOnboardingPageBinding binding = ItemOnboardingPageBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OnboardingSlide slide = slides.get(position);
        holder.binding.image.setImageResource(slide.imageResId);
        holder.binding.title.setText(slide.titleResId);
        holder.binding.description.setText(slide.descriptionResId);
    }

    @Override
    public int getItemCount() {
        return slides.size();
    }

    static final class ViewHolder extends RecyclerView.ViewHolder {
        final ItemOnboardingPageBinding binding;

        ViewHolder(ItemOnboardingPageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

