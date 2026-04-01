package com.pixibeestudio.greenly.ui.fragment.shipper;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.pixibeestudio.greenly.R;

public class ShipperOrdersFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ShipperOrdersPagerAdapter pagerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shipper_orders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tabLayout = view.findViewById(R.id.tabLayoutShipperOrders);
        viewPager = view.findViewById(R.id.viewPagerShipperOrders);

        // Khởi tạo Adapter và gắn vào ViewPager2
        pagerAdapter = new ShipperOrdersPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // Liên kết TabLayout và ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Chờ lấy hàng");
                    break;
                case 1:
                    tab.setText("Đang giao");
                    break;
            }
        }).attach();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Luôn ép về tab 0 (Chờ lấy hàng) khi mở lại fragment
        if (viewPager != null) {
            viewPager.setCurrentItem(0, false);
        }
    }
}
