package com.pixibeestudio.greenly.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.pixibeestudio.greenly.R;
import com.pixibeestudio.greenly.data.local.SessionManager;

public class SplashFragment extends Fragment {

    // Thời gian hiển thị màn hình Splash (2 giây)
    private static final int SPLASH_DELAY_MS = 2000;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());

        // Sau 2 giây, chuyển sang màn hình phù hợp
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Kiểm tra Fragment còn gắn với NavController không trước khi điều hướng
            if (isAdded()) {
                NavController navController = Navigation.findNavController(view);
                
                if (sessionManager.isLoggedIn() || sessionManager.isGuestMode()) {
                    // Đã đăng nhập hoặc đã từng chọn Guest -> Vào thẳng Home
                    navController.navigate(R.id.action_splashFragment_to_homeFragment);
                } else {
                    // Lần đầu tải app -> Vào Welcome
                    navController.navigate(R.id.action_splashFragment_to_welcomeFragment);
                }
            }
        }, SPLASH_DELAY_MS);
    }
}
