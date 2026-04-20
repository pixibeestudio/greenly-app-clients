package com.pixibeestudio.greenly.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.pixibeestudio.greenly.ui.fragment.CompletedReviewsFragment;
import com.pixibeestudio.greenly.ui.fragment.PendingReviewsFragment;

/**
 * Pager adapter cho 2 tab trong MyReviewsFragment:
 * - 0: Chưa đánh giá (PendingReviewsFragment)
 * - 1: Đã đánh giá (CompletedReviewsFragment)
 */
public class MyReviewsPagerAdapter extends FragmentStateAdapter {

    public MyReviewsPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new PendingReviewsFragment();
        }
        return new CompletedReviewsFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
