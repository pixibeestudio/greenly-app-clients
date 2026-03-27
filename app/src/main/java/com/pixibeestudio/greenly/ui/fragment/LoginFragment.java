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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.pixibeestudio.greenly.R;
import com.pixibeestudio.greenly.data.local.SessionManager;
import com.pixibeestudio.greenly.data.model.ErrorResponse;
import com.pixibeestudio.greenly.data.model.LoginResponse;
import com.pixibeestudio.greenly.ui.viewmodel.AuthViewModel;

import java.util.List;
import java.util.Map;

public class LoginFragment extends Fragment {

    private TextInputLayout tilEmailLogin;
    private TextInputLayout tilPasswordLogin;
    private TextInputEditText edtEmailLogin;
    private TextInputEditText edtPasswordLogin;
    private ImageView ivPasswordToggleLogin;
    private TextView tvForgotPasswordDetail;
    private MaterialButton btnLoginMain;
    private TextView tvRegisterPrompt;

    private boolean isPasswordVisible = false;
    private AuthViewModel authViewModel;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo ViewModel và SessionManager
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        sessionManager = new SessionManager(requireContext());

        // Ánh xạ View
        tilEmailLogin = view.findViewById(R.id.tilEmailLogin);
        tilPasswordLogin = view.findViewById(R.id.tilPasswordLogin);
        edtEmailLogin = view.findViewById(R.id.edtEmailLogin);
        edtPasswordLogin = view.findViewById(R.id.edtPasswordLogin);
        ivPasswordToggleLogin = view.findViewById(R.id.ivPasswordToggleLogin);
        tvForgotPasswordDetail = view.findViewById(R.id.tvForgotPasswordDetail);
        btnLoginMain = view.findViewById(R.id.btnLoginMain);
        tvRegisterPrompt = view.findViewById(R.id.tvRegisterPrompt);

        // Logic toggle hiển thị mật khẩu
        ivPasswordToggleLogin.setOnClickListener(v -> {
            if (isPasswordVisible) {
                // Đang hiển thị -> Chuyển sang ẩn
                edtPasswordLogin.setTransformationMethod(PasswordTransformationMethod.getInstance());
                ivPasswordToggleLogin.setImageResource(R.drawable.ic_eye_hidden);
            } else {
                // Đang ẩn -> Chuyển sang hiển thị
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
            String email = edtEmailLogin.getText() != null ? edtEmailLogin.getText().toString().trim() : "";
            String password = edtPasswordLogin.getText() != null ? edtPasswordLogin.getText().toString().trim() : "";

            // Xóa lỗi cũ
            tilEmailLogin.setError(null);
            tilEmailLogin.setErrorEnabled(false);
            tilPasswordLogin.setError(null);
            tilPasswordLogin.setErrorEnabled(false);

            btnLoginMain.setEnabled(false); // Vô hiệu hóa nút

            authViewModel.login(email, password).observe(getViewLifecycleOwner(), resource -> {
                switch (resource.status) {
                    case LOADING:
                        // Hiển thị loading nếu cần
                        break;
                    case SUCCESS:
                        btnLoginMain.setEnabled(true);
                        if (resource.data != null) {
                            try {
                                // Parse JsonObject thành LoginResponse
                                Gson gson = new Gson();
                                LoginResponse loginResponse = gson.fromJson(resource.data, LoginResponse.class);

                                if (loginResponse != null && loginResponse.isSuccess()) {
                                    // Lưu thông tin vào SessionManager
                                    sessionManager.setLoginState(true);
                                    sessionManager.saveAuthToken(loginResponse.getToken());
                                    
                                    if (loginResponse.getUser() != null) {
                                        sessionManager.saveUser(
                                            loginResponse.getUser().getFullname(), 
                                            loginResponse.getUser().getAvatar()
                                        );
                                    }
                                    
                                    sessionManager.setGuestMode(false);

                                    Toast.makeText(requireContext(), "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                                    
                                    // Điều hướng về Home
                                    NavController navController = Navigation.findNavController(view);
                                    navController.navigate(R.id.action_login_to_home);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(requireContext(), "Lỗi xử lý dữ liệu đăng nhập", Toast.LENGTH_SHORT).show();
                            }
                        }
                        break;
                    case ERROR:
                        btnLoginMain.setEnabled(true);
                        if (resource.errorData != null && resource.errorData.getErrors() != null) {
                            Map<String, List<String>> errors = resource.errorData.getErrors();
                            
                            if (errors.containsKey("email") && !errors.get("email").isEmpty()) {
                                tilEmailLogin.setErrorEnabled(true);
                                tilEmailLogin.setError(errors.get("email").get(0));
                            }
                            
                            if (errors.containsKey("password") && !errors.get("password").isEmpty()) {
                                tilPasswordLogin.setErrorEnabled(true);
                                tilPasswordLogin.setError(errors.get("password").get(0));
                            }
                        } else {
                            Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            });
        });

        // Quên mật khẩu
        tvForgotPasswordDetail.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Sẽ làm màn hình Quên mật khẩu sau.", Toast.LENGTH_SHORT).show();
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_loginFragment_to_forgotPasswordFragment);
        });

        // Đăng ký ngay
        tvRegisterPrompt.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_loginFragment_to_registerFragment);
        });
    }
}
