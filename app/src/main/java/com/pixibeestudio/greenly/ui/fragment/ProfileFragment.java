package com.pixibeestudio.greenly.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.pixibeestudio.greenly.R;
import com.pixibeestudio.greenly.data.local.SessionManager;

/**
 * Fragment hiển thị thông tin Cá nhân và Menu cài đặt.
 */
public class ProfileFragment extends Fragment {

    private ShapeableImageView ivAvatarProfile;
    private ImageButton btnEditAvatarProfile;
    private TextView tvUserNameProfile;
    private TextView tvUserContactProfile;
    
    private LinearLayout groupShoppingProfile;
    private LinearLayout layoutMyOrders;
    private LinearLayout layoutAddress;
    private LinearLayout layoutReviews;
    private LinearLayout groupAccountProfile;
    private LinearLayout groupSupportProfile;
    
    private LinearLayout btnLogoutProfile;
    private MaterialButton btnLoginPromptProfile;

    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());
        
        initViews(view);
        updateUIBasedOnAuth();
        setupClickListeners(view);
    }

    private void initViews(View view) {
        ivAvatarProfile = view.findViewById(R.id.ivAvatarProfile);
        btnEditAvatarProfile = view.findViewById(R.id.btnEditAvatarProfile);
        tvUserNameProfile = view.findViewById(R.id.tvUserNameProfile);
        tvUserContactProfile = view.findViewById(R.id.tvUserContactProfile);

        groupShoppingProfile = view.findViewById(R.id.groupShoppingProfile);
        layoutMyOrders = view.findViewById(R.id.layoutMyOrders);
        layoutAddress = view.findViewById(R.id.layoutAddress);
        layoutReviews = view.findViewById(R.id.layoutReviews);
        groupAccountProfile = view.findViewById(R.id.groupAccountProfile);
        groupSupportProfile = view.findViewById(R.id.groupSupportProfile);

        btnLogoutProfile = view.findViewById(R.id.btnLogoutProfile);
        btnLoginPromptProfile = view.findViewById(R.id.btnLoginPromptProfile);
    }

    private void updateUIBasedOnAuth() {
        if (sessionManager.isLoggedIn()) {
            // Đã đăng nhập
            tvUserNameProfile.setText(sessionManager.getUserName());
            tvUserContactProfile.setText("nanthaxay.test@gmail.com"); // Mock contact
            
            // Load Avatar
            String avatarUrl = sessionManager.getUserAvatar();
            Glide.with(this)
                 .load(avatarUrl)
                 .placeholder(R.drawable.ic_default_avatar_placeholder)
                 .error(R.drawable.ic_default_avatar_placeholder)
                 .centerCrop()
                 .into(ivAvatarProfile);

            // Hiện các thành phần của User
            btnEditAvatarProfile.setVisibility(View.VISIBLE);
            groupShoppingProfile.setVisibility(View.VISIBLE);
            groupAccountProfile.setVisibility(View.VISIBLE);
            btnLogoutProfile.setVisibility(View.VISIBLE);
            
            // Ẩn nút đăng nhập/đăng ký
            btnLoginPromptProfile.setVisibility(View.GONE);

        } else if (sessionManager.isGuestMode()) {
            // Chế độ Guest
            tvUserNameProfile.setText("Chưa đăng nhập");
            tvUserContactProfile.setText("Chưa cập nhật số điện thoại");
            ivAvatarProfile.setImageResource(R.drawable.ic_default_avatar_placeholder);

            // Ẩn các thành phần của User
            btnEditAvatarProfile.setVisibility(View.GONE);
            groupShoppingProfile.setVisibility(View.GONE);
            groupAccountProfile.setVisibility(View.GONE);
            btnLogoutProfile.setVisibility(View.GONE);
            
            // Hiện nút đăng nhập/đăng ký
            btnLoginPromptProfile.setVisibility(View.VISIBLE);
        }
    }

    private void setupClickListeners(View view) {
        // Nút Đơn hàng của tôi
        layoutMyOrders.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_profileFragment_to_myOrdersFragment);
        });

        // Nút Sổ địa chỉ
        layoutAddress.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Chức năng đang phát triển", Toast.LENGTH_SHORT).show();
        });

        // Nút Đánh giá sản phẩm
        layoutReviews.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Chức năng đang phát triển", Toast.LENGTH_SHORT).show();
        });

        // Nút Đăng xuất
        btnLogoutProfile.setOnClickListener(v -> {
            // Local Logout
            sessionManager.clearSession();
            Toast.makeText(requireContext(), "Đăng xuất thành công", Toast.LENGTH_SHORT).show();

            // Khởi động lại MainActivity để tự động vào WelcomeFragment
            android.content.Intent intent = new android.content.Intent(requireActivity(), com.pixibeestudio.greenly.ui.activity.MainActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });

        // Nút Đăng nhập/Đăng ký cho Guest
        btnLoginPromptProfile.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);
            // Ghi chú: Cần đảm bảo có action từ Profile -> Login trong nav_main.xml
            navController.navigate(R.id.action_profileFragment_to_loginFragment);
        });
    }
}
