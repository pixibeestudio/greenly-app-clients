package com.pixibeestudio.greenly.ui.fragment.shipper;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.imageview.ShapeableImageView;
import com.pixibeestudio.greenly.R;
import com.pixibeestudio.greenly.data.local.SessionManager;
import com.pixibeestudio.greenly.data.model.WalletProfileResponse;
import com.pixibeestudio.greenly.ui.activity.MainActivity;
import com.pixibeestudio.greenly.ui.viewmodel.ShipperDashboardViewModel;

public class ShipperProfileFragment extends Fragment {

    private ShapeableImageView ivShipperAvatar;
    private TextView tvShipperName;
    private TextView tvShipperPhone;
    private TextView tvRatingScore;
    private TextView tvCompletedOrders;

    private LinearLayout btnOrderHistory;
    private LinearLayout btnCustomerReviews;
    private LinearLayout btnDeliveryRules;
    private LinearLayout btnAccountInfo;
    private LinearLayout btnAppSettings;
    private LinearLayout btnLogout;

    private SessionManager sessionManager;
    private ShipperDashboardViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shipper_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ánh xạ View
        ivShipperAvatar = view.findViewById(R.id.ivShipperAvatar);
        tvShipperName = view.findViewById(R.id.tvShipperName);
        tvShipperPhone = view.findViewById(R.id.tvShipperPhone);
        tvRatingScore = view.findViewById(R.id.tvRatingScore);
        tvCompletedOrders = view.findViewById(R.id.tvCompletedOrders);

        btnOrderHistory = view.findViewById(R.id.btnOrderHistory);
        btnCustomerReviews = view.findViewById(R.id.btnCustomerReviews);
        btnDeliveryRules = view.findViewById(R.id.btnDeliveryRules);
        btnAccountInfo = view.findViewById(R.id.btnAccountInfo);
        btnAppSettings = view.findViewById(R.id.btnAppSettings);
        btnLogout = view.findViewById(R.id.btnLogout);

        sessionManager = new SessionManager(requireContext());
        viewModel = new ViewModelProvider(requireActivity()).get(ShipperDashboardViewModel.class);

        // Hiển thị thông tin cá nhân từ SessionManager
        loadUserInfo();

        // Lắng nghe dữ liệu Wallet Profile từ ViewModel
        viewModel.getWalletProfileLiveData().observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == com.pixibeestudio.greenly.data.network.Resource.Status.SUCCESS && resource.data != null) {
                updateStatsUI(resource.data);
            }
        });

        // Thiết lập sự kiện click
        setupClickListeners();

        // Cập nhật dữ liệu nếu chưa có
        if (viewModel.getWalletProfileLiveData().getValue() == null) {
            viewModel.fetchWalletProfile();
        }
    }

    private void loadUserInfo() {
        String name = sessionManager.getUserName();
        String phone = sessionManager.getShippingPhone();
        if (name != null) {
            tvShipperName.setText(name);
        }
        tvShipperPhone.setText(phone != null ? phone : "Chưa cập nhật số điện thoại");
    }

    private void updateStatsUI(WalletProfileResponse data) {
        tvRatingScore.setText(String.valueOf(data.getRating()));
        tvCompletedOrders.setText(String.valueOf(data.getCompletedOrders()));
    }

    private void setupClickListeners() {
        btnOrderHistory.setOnClickListener(v -> showToast("Lịch sử đơn hàng"));
        btnCustomerReviews.setOnClickListener(v -> showToast("Đánh giá khách hàng"));
        btnDeliveryRules.setOnClickListener(v -> showToast("Quy định giao hàng"));
        btnAccountInfo.setOnClickListener(v -> showToast("Thông tin tài khoản"));
        btnAppSettings.setOnClickListener(v -> showToast("Cài đặt ứng dụng"));

        btnLogout.setOnClickListener(v -> {
            sessionManager.clearSession();
            Intent intent = new Intent(requireActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message + " đang phát triển", Toast.LENGTH_SHORT).show();
    }
}
