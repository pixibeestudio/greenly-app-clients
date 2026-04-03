package com.pixibeestudio.greenly.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.pixibeestudio.greenly.ui.fragment.MyOrdersPageFragment;

public class MyOrdersPagerAdapter extends FragmentStateAdapter {

    private final String[] statusList = new String[]{
            "ALL", "PENDING", "PROCESSING", "PICKUP", "SHIPPING", "DELIVERED", "CANCELLED"
    };

    public MyOrdersPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return MyOrdersPageFragment.newInstance(statusList[position]);
    }

    @Override
    public int getItemCount() {
        return statusList.length;
    }
}
