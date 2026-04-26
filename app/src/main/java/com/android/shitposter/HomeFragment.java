package com.android.shitposter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.android.shitposter.databinding.FragmentHomeBinding;
import com.google.android.material.tabs.TabLayoutMediator;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomePagerAdapter pagerAdapter;
    private ActivityResultLauncher<Intent> composePostLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        composePostLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        refreshFeeds();
                    }
                }
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pagerAdapter = new HomePagerAdapter(requireActivity());
        binding.pager.setAdapter(pagerAdapter);

        new TabLayoutMediator(binding.tabs, binding.pager, (tab, position) -> {
            tab.setText(position == 0 ? R.string.tab_for_you : R.string.tab_following);
        }).attach();

        binding.fabPost.setOnClickListener(v -> composePostLauncher.launch(
                new Intent(requireContext(), ComposePostActivity.class)
        ));
    }

    private void refreshFeeds() {
        for (Fragment f : requireActivity().getSupportFragmentManager().getFragments()) {
            if (f instanceof FeedFragment) {
                ((FeedFragment) f).refresh();
            }
        }
        // Also refresh child fragments in the pager.
        for (Fragment f : getChildFragmentManager().getFragments()) {
            if (f instanceof FeedFragment) {
                ((FeedFragment) f).refresh();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        pagerAdapter = null;
    }
}
