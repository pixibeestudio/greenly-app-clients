package com.pixibeestudio.greenly.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.pixibeestudio.greenly.R;
import com.pixibeestudio.greenly.data.model.ErrorResponse;
import com.pixibeestudio.greenly.ui.viewmodel.AuthViewModel;

import java.util.List;
import java.util.Map;

public class RegisterFragment extends Fragment {

    private TextInputLayout tilNameRegister;
    private TextInputLayout tilEmailRegister;
    private TextInputLayout tilPasswordRegister;
    private TextInputLayout tilConfirmPasswordRegister;

    private TextInputEditText edtNameRegister;
    private TextInputEditText edtEmailRegister;
    private TextInputEditText edtPasswordRegister;
    private TextInputEditText edtConfirmPasswordRegister;

    private MaterialButton btnRegisterMain;
    private TextView tvLoginPrompt;

    private AuthViewModel authViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo ViewModel
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Ánh xạ TextInputLayout (Để hiển thị lỗi)
        tilNameRegister = view.findViewById(R.id.tilNameRegister);
        tilEmailRegister = view.findViewById(R.id.tilEmailRegister);
        tilPasswordRegister = view.findViewById(R.id.tilPasswordRegister);
        tilConfirmPasswordRegister = view.findViewById(R.id.tilConfirmPasswordRegister);

        // Ánh xạ TextInputEditText (Để lấy dữ liệu)
        edtNameRegister = view.findViewById(R.id.edtNameRegister);
        edtEmailRegister = view.findViewById(R.id.edtEmailRegister);
        edtPasswordRegister = view.findViewById(R.id.edtPasswordRegister);
        edtConfirmPasswordRegister = view.findViewById(R.id.edtConfirmPasswordRegister);

        btnRegisterMain = view.findViewById(R.id.btnRegisterMain);
        tvLoginPrompt = view.findViewById(R.id.tvLoginPrompt);

        // Xử lý sự kiện click Đăng ký
        btnRegisterMain.setOnClickListener(v -> {
            String name = edtNameRegister.getText() != null ? edtNameRegister.getText().toString().trim() : "";
            String email = edtEmailRegister.getText() != null ? edtEmailRegister.getText().toString().trim() : "";
            String password = edtPasswordRegister.getText() != null ? edtPasswordRegister.getText().toString().trim() : "";
            String confirmPassword = edtConfirmPasswordRegister.getText() != null ? edtConfirmPasswordRegister.getText().toString().trim() : "";

            // Xóa lỗi cũ
            clearErrors();

            btnRegisterMain.setEnabled(false); // Vô hiệu hóa nút trong lúc chờ API
            
            authViewModel.register(name, email, password, confirmPassword).observe(getViewLifecycleOwner(), resource -> {
                switch (resource.status) {
                    case LOADING:
                        // Có thể hiển thị ProgressBar ở đây
                        break;
                    case SUCCESS:
                        btnRegisterMain.setEnabled(true);
                        Toast.makeText(requireContext(), "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                        NavController navController = Navigation.findNavController(view);
                        navController.popBackStack(); // Quay lại màn hình Login
                        break;
                    case ERROR:
                        btnRegisterMain.setEnabled(true);
                        if (resource.errorData != null) {
                            handleValidationErrors(resource.errorData);
                        } else {
                            Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            });
        });

        // Xử lý sự kiện click Đăng nhập ngay -> Quay lại màn Login mượt mà
        tvLoginPrompt.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);
            navController.popBackStack();
        });
    }

    private void clearErrors() {
        tilNameRegister.setError(null);
        tilNameRegister.setErrorEnabled(false);
        tilEmailRegister.setError(null);
        tilEmailRegister.setErrorEnabled(false);
        tilPasswordRegister.setError(null);
        tilPasswordRegister.setErrorEnabled(false);
        tilConfirmPasswordRegister.setError(null);
        tilConfirmPasswordRegister.setErrorEnabled(false);
    }

    private void handleValidationErrors(ErrorResponse errorData) {
        if (errorData.getErrors() == null) return;

        Map<String, List<String>> errors = errorData.getErrors();

        if (errors.containsKey("fullname") && !errors.get("fullname").isEmpty()) {
            tilNameRegister.setErrorEnabled(true);
            tilNameRegister.setError(errors.get("fullname").get(0));
        }

        if (errors.containsKey("email") && !errors.get("email").isEmpty()) {
            tilEmailRegister.setErrorEnabled(true);
            tilEmailRegister.setError(errors.get("email").get(0));
        }

        if (errors.containsKey("password") && !errors.get("password").isEmpty()) {
            tilPasswordRegister.setErrorEnabled(true);
            tilPasswordRegister.setError(errors.get("password").get(0));
            // Do Laravel validation có thể gom cả lỗi confirm vào "password" nên báo lên ô mật khẩu
        }
    }
}
