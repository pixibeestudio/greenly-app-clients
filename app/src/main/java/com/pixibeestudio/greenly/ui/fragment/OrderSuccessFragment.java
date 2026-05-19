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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.LinearLayout;

import com.google.android.material.button.MaterialButton;
import com.pixibeestudio.greenly.R;
import com.pixibeestudio.greenly.data.model.Product;
import com.pixibeestudio.greenly.data.model.ProductResponse;
import com.pixibeestudio.greenly.data.network.RetrofitClient;
import com.pixibeestudio.greenly.data.local.SessionManager;
import com.pixibeestudio.greenly.ui.adapter.ProductHorizontalAdapter;
import com.pixibeestudio.greenly.ui.viewmodel.CartViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderSuccessFragment extends Fragment {

    private MaterialButton btnViewOrders;
    private TextView tvContinueShopping;
    private RecyclerView rvSuggestedProducts;
    private LinearLayout layoutOrderRecommendations;

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
        layoutOrderRecommendations = view.findViewById(R.id.layoutOrderRecommendations);

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
            // Chuyển hướng sang MyOrdersFragment
            try {
                NavOptions navOptions = new NavOptions.Builder()
                        .setPopUpTo(R.id.homeFragment, false)
                        .build();
                Navigation.findNavController(view).navigate(R.id.action_orderSuccessFragment_to_myOrdersFragment, null, navOptions);
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Màn hình Đơn hàng đang phát triển. Quay về Trang chủ.", Toast.LENGTH_SHORT).show();
                navigateToHome(view);
            }
        });

        tvContinueShopping.setOnClickListener(v -> {
            navigateToHome(view);
        });

        // Tải gợi ý sản phẩm cá nhân hóa
        loadSuggestedProducts();
    }

    /**
     * Tải danh sách gợi ý sản phẩm từ API Recommender System.
     */
    private void loadSuggestedProducts() {
        SessionManager sessionManager = new SessionManager(requireContext());
        if (!sessionManager.isLoggedIn()) return;

        RetrofitClient.getApiService(requireContext()).getRecommendationsForYou()
                .enqueue(new Callback<ProductResponse>() {
                    @Override
                    public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                        if (!isAdded()) return;
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()
                                && response.body().getData() != null
                                && !response.body().getData().isEmpty()) {
                            layoutOrderRecommendations.setVisibility(View.VISIBLE);
                            rvSuggestedProducts.setLayoutManager(
                                    new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
                            CartViewModel cartVM = new ViewModelProvider(OrderSuccessFragment.this).get(CartViewModel.class);
                            ProductHorizontalAdapter adapter = new ProductHorizontalAdapter(
                                    response.body().getData(), product -> {
                                        cartVM.addToCart(product.getId(), 1);
                                        Toast.makeText(getContext(), "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                                    });
                            adapter.setSectionType(ProductHorizontalAdapter.SECTION_FOR_YOU);
                            rvSuggestedProducts.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onFailure(Call<ProductResponse> call, Throwable t) {
                        // Im lặng nếu lỗi — không ảnh hưởng trải nghiệm chính
                    }
                });
    }

    private void navigateToHome(View view) {
        NavOptions navOptions = new NavOptions.Builder()
                .setPopUpTo(R.id.homeFragment, false)
                .build();
        Navigation.findNavController(view).navigate(R.id.homeFragment, null, navOptions);
    }
}
