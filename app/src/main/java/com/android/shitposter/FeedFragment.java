package com.android.shitposter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.shitposter.databinding.FragmentFeedBinding;

import java.util.List;

public class FeedFragment extends Fragment {
    static final String ARG_FEED_TYPE = "feed_type";
    static final String FEED_FOR_YOU = "for_you";
    static final String FEED_FOLLOWING = "following";

    private FragmentFeedBinding binding;
    private PostAdapter adapter;

    public static FeedFragment newInstance(@NonNull String feedType) {
        FeedFragment f = new FeedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FEED_TYPE, feedType);
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFeedBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new PostAdapter();
        binding.recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recycler.setAdapter(adapter);

        refresh();
    }

    void refresh() {
        if (binding == null) return;
        List<Post> posts = PostStore.getPosts(requireContext());

        String type = getArguments() != null ? getArguments().getString(ARG_FEED_TYPE, FEED_FOR_YOU) : FEED_FOR_YOU;
        if (FEED_FOLLOWING.equals(type)) {
            // For now, show the same text posts. Later we can filter by followed accounts.
            adapter.submitList(posts);
        } else {
            adapter.submitList(posts);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        adapter = null;
    }
}

