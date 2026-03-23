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

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.pixibeestudio.greenly.R;

public class LoginFragment extends Fragment {

    private TextInputEditText edtEmail;
    private TextInputEditText edtPassword;
    private MaterialButton btnLogin;
    private TextView tvRegister;

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
        edtEmail = view.findViewById(R.id.edt_email);
        edtPassword = view.findViewById(R.id.edt_password);
        btnLogin = view.findViewById(R.id.btn_login);
        tvRegister = view.findViewById(R.id.tv_register);

        // Xử lý sự kiện nhấn nút Đăng nhập
        btnLogin.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Đang xử lý đăng nhập...", Toast.LENGTH_SHORT).show();
        });

        // Xử lý sự kiện nhấn dòng chữ Đăng ký
        tvRegister.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Chức năng đăng ký sẽ được cập nhật sau.", Toast.LENGTH_SHORT).show();
        });
    }
}
