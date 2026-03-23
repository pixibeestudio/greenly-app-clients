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

public class SplashFragment extends Fragment {

    // Thời gian hiển thị màn hình Splash (2 giây)
    private static final int SPLASH_DELAY_MS = 2000;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Sau 2 giây, chuyển sang màn hình Đăng nhập
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Kiểm tra Fragment còn gắn với NavController không trước khi điều hướng
            if (isAdded()) {
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_splash_to_login);
            }
        }, SPLASH_DELAY_MS);
    }
}
