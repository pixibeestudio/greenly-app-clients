package com.pixibeestudio.greenly.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.pixibeestudio.greenly.R;

public class SearchFragment extends Fragment {

    private ImageButton btnBack;
    private ImageButton btnDeleteHistory;
    private EditText edtSearch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnBack = view.findViewById(R.id.btnBack);
        btnDeleteHistory = view.findViewById(R.id.btnDeleteHistory);
        edtSearch = view.findViewById(R.id.edtSearch);

        btnBack.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);
            navController.popBackStack();
        });

        btnDeleteHistory.setOnClickListener(v -> showDeleteHistoryDialog());

        // Focus vo edtSearch tu dong va show keyboard... (Them sau neu can)
        
        // Lang nghe su kien nhan enter/search tren ban phim (Them sau de qua ket qua)
    }

    private void showDeleteHistoryDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Xóa lịch sử?")
                .setMessage("Bạn có chắc chắn muốn xóa toàn bộ lịch sử tìm kiếm không?")
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Xác nhận", (dialog, which) -> {
                    Toast.makeText(getContext(), "Đã xóa lịch sử", Toast.LENGTH_SHORT).show();
                    // Thuc hien code xoa that su o day (neu co DB hoac SharedPrefs)
                })
                .show();
    }
}
