package com.pixibeestudio.greenly.ui.fragment.shipper;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ShipperOrdersPagerAdapter extends FragmentStateAdapter {

    public ShipperOrdersPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new ShipperShippingFragment();
        }
        return new ShipperPickupFragment();
    }

    @Override
    public int getItemCount() {
        return 2; // Chờ lấy hàng, Đang giao
    }
}
