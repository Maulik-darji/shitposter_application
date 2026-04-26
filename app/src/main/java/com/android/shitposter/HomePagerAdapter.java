package com.android.shitposter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

final class HomePagerAdapter extends FragmentStateAdapter {

    HomePagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return FeedFragment.newInstance(FeedFragment.FEED_FOLLOWING);
        }
        return FeedFragment.newInstance(FeedFragment.FEED_FOR_YOU);
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}

