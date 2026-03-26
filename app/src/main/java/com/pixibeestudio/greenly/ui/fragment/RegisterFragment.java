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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.pixibeestudio.greenly.R;

public class RegisterFragment extends Fragment {

    private TextInputEditText edtNameRegister;
    private TextInputEditText edtEmailRegister;
    private TextInputEditText edtPasswordRegister;
    private TextInputEditText edtConfirmPasswordRegister;
    private MaterialButton btnRegisterMain;
    private TextView tvLoginPrompt;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ánh xạ View
        edtNameRegister = view.findViewById(R.id.edtNameRegister);
        edtEmailRegister = view.findViewById(R.id.edtEmailRegister);
        edtPasswordRegister = view.findViewById(R.id.edtPasswordRegister);
        edtConfirmPasswordRegister = view.findViewById(R.id.edtConfirmPasswordRegister);
        btnRegisterMain = view.findViewById(R.id.btnRegisterMain);
        tvLoginPrompt = view.findViewById(R.id.tvLoginPrompt);

        // Xử lý sự kiện click Đăng ký
        btnRegisterMain.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Đang xử lý đăng ký...", Toast.LENGTH_SHORT).show();
            // TODO: Gọi API đăng ký ở đây
        });

        // Xử lý sự kiện click Đăng nhập ngay -> Quay lại màn Login mượt mà
        tvLoginPrompt.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);
            navController.popBackStack();
        });
    }
}
