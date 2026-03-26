package com.pixibeestudio.greenly.ui.fragment;

import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.pixibeestudio.greenly.R;

public class LoginFragment extends Fragment {

    private TextInputEditText edtEmailLogin;
    private TextInputEditText edtPasswordLogin;
    private MaterialButton btnLoginMain;
    private TextView tvRegisterPrompt;
    private TextView tvForgotPasswordDetail;
    private ImageView ivPasswordToggleLogin;
    
    private boolean isPasswordVisible = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ánh xạ các view
        edtEmailLogin = view.findViewById(R.id.edtEmailLogin);
        edtPasswordLogin = view.findViewById(R.id.edtPasswordLogin);
        btnLoginMain = view.findViewById(R.id.btnLoginMain);
        tvRegisterPrompt = view.findViewById(R.id.tvRegisterPrompt);
        tvForgotPasswordDetail = view.findViewById(R.id.tvForgotPasswordDetail);
        ivPasswordToggleLogin = view.findViewById(R.id.ivPasswordToggleLogin);

        // Logic toggle ẩn/hiện mật khẩu
        ivPasswordToggleLogin.setOnClickListener(v -> {
            if (isPasswordVisible) {
                // Đang hiện -> Ẩn đi
                edtPasswordLogin.setTransformationMethod(PasswordTransformationMethod.getInstance());
                ivPasswordToggleLogin.setImageResource(R.drawable.ic_eye_hidden);
            } else {
                // Đang ẩn -> Hiện lên
                edtPasswordLogin.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                ivPasswordToggleLogin.setImageResource(R.drawable.ic_eye_visible);
            }
            isPasswordVisible = !isPasswordVisible;
            // Di chuyển con trỏ về cuối văn bản
            if (edtPasswordLogin.getText() != null) {
                edtPasswordLogin.setSelection(edtPasswordLogin.getText().length());
            }
        });

        // Nút Đăng nhập
        btnLoginMain.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Đang xử lý đăng nhập...", Toast.LENGTH_SHORT).show();
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_login_to_home);
        });

        // Quên mật khẩu
        tvForgotPasswordDetail.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Sẽ làm màn hình Quên mật khẩu sau.", Toast.LENGTH_SHORT).show();
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_loginFragment_to_forgotPasswordFragment);
        });

        // Đăng ký ngay
        tvRegisterPrompt.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Sẽ thiết kế màn hình Đăng ký sau.", Toast.LENGTH_SHORT).show();
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_loginFragment_to_registerFragment);
        });
    }
}
