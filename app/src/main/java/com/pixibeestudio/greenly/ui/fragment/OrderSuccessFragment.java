package com.pixibeestudio.greenly.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.pixibeestudio.greenly.R;

public class OrderSuccessFragment extends Fragment {

    private MaterialButton btnViewOrders;
    private TextView tvContinueShopping;
    private RecyclerView rvSuggestedProducts;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_success, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ánh xạ View
        btnViewOrders = view.findViewById(R.id.btnViewOrders);
        tvContinueShopping = view.findViewById(R.id.tvContinueShopping);
        rvSuggestedProducts = view.findViewById(R.id.rvSuggestedProducts);

        // Override nút Back của hệ điều hành
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Khách bấm Back -> Quay thẳng về Home, xóa sạch stack
                navigateToHome(view);
            }
        });

        // Xử lý sự kiện click
        btnViewOrders.setOnClickListener(v -> {
            // Chuyển hướng sang MyOrdersFragment (Nếu đã có)
            // Nếu chưa có MyOrdersFragment, tạm thời quay về Home hoặc báo Toast
            try {
                NavOptions navOptions = new NavOptions.Builder()
                        .setPopUpTo(R.id.homeFragment, false)
                        .build();
                Navigation.findNavController(view).navigate(R.id.action_orderSuccessFragment_to_homeFragment, null, navOptions);
            } catch (Exception e) {
                // Tạm thời nếu MyOrders chưa có trong NavGraph
                Toast.makeText(requireContext(), "Màn hình Đơn hàng đang phát triển. Quay về Trang chủ.", Toast.LENGTH_SHORT).show();
                navigateToHome(view);
            }
        });

        tvContinueShopping.setOnClickListener(v -> {
            navigateToHome(view);
        });

        // TODO: Xử lý hiển thị gợi ý sản phẩm cho rvSuggestedProducts sau (nếu cần)
    }

    private void navigateToHome(View view) {
        NavOptions navOptions = new NavOptions.Builder()
                .setPopUpTo(R.id.homeFragment, false)
                .build();
        Navigation.findNavController(view).navigate(R.id.homeFragment, null, navOptions);
    }
}
