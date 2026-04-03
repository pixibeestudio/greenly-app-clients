package com.pixibeestudio.greenly.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.pixibeestudio.greenly.R;
import com.pixibeestudio.greenly.ui.adapter.MyOrdersPagerAdapter;
import com.pixibeestudio.greenly.ui.viewmodel.MyOrdersViewModel;

public class MyOrdersFragment extends Fragment {

    private ImageButton btnBack;
    private TabLayout tlMyOrders;
    private ViewPager2 vpMyOrders;
    private MyOrdersViewModel viewModel;

    private final String[] tabTitles = new String[]{
            "Tất cả", "Chờ xác nhận", "Đang xử lý", "Chờ lấy hàng", "Đang giao", "Đã giao", "Đã hủy"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_orders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo ViewModel (SharedViewModel cho các Fragment con)
        viewModel = new ViewModelProvider(this).get(MyOrdersViewModel.class);

        // Ánh xạ View
        btnBack = view.findViewById(R.id.btnBack);
        tlMyOrders = view.findViewById(R.id.tlMyOrders);
        vpMyOrders = view.findViewById(R.id.vpMyOrders);

        // Nút Back - Quay về Profile
        btnBack.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        // Cài đặt ViewPager2 và TabLayout

        // Gọi API tải danh sách đơn hàng
        viewModel.fetchMyOrders();
        MyOrdersPagerAdapter adapter = new MyOrdersPagerAdapter(this);
        vpMyOrders.setAdapter(adapter);

        // Liên kết TabLayout và ViewPager2
        new TabLayoutMediator(tlMyOrders, vpMyOrders,
                (tab, position) -> tab.setText(tabTitles[position])
        ).attach();
    }
}
