package com.pixibeestudio.greenly.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.pixibeestudio.greenly.R;
import com.pixibeestudio.greenly.data.local.SearchHistoryManager;

import java.util.List;

public class SearchFragment extends Fragment {

    private ImageButton btnBack;
    private ImageButton btnDeleteHistory;
    private EditText edtSearch;
    private ChipGroup chipGroupHistory;
    private TextView tvHistoryTitle;

    private SearchHistoryManager searchHistoryManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khoi tao SearchHistoryManager
        searchHistoryManager = new SearchHistoryManager(requireContext());

        // Anh xa views
        btnBack = view.findViewById(R.id.btnBack);
        btnDeleteHistory = view.findViewById(R.id.btnDeleteHistory);
        edtSearch = view.findViewById(R.id.edtSearch);
        chipGroupHistory = view.findViewById(R.id.chipGroupHistory);
        tvHistoryTitle = view.findViewById(R.id.tvHistoryTitle);

        // Nut Back -> quay ve man truoc
        btnBack.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);
            navController.popBackStack();
        });

        // Nut thung rac -> dialog xoa lich su
        btnDeleteHistory.setOnClickListener(v -> showDeleteHistoryDialog());

        // Bat su kien nhan Search tren ban phim
        edtSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = edtSearch.getText().toString().trim();
                if (!query.isEmpty()) {
                    performSearch(query);
                }
                return true;
            }
            return false;
        });

        // Tai lich su tim kiem
        loadSearchHistory();

        // Tu dong focus vao EditText va hien ban phim
        edtSearch.requestFocus();
        edtSearch.postDelayed(() -> {
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(edtSearch, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 200);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Cap nhat lai lich su moi khi quay ve man nay
        loadSearchHistory();
    }

    /**
     * Thuc hien tim kiem: luu lich su va dieu huong sang SearchResultFragment
     */
    private void performSearch(String query) {
        // Luu vao lich su
        searchHistoryManager.addSearchQuery(query);

        // An ban phim
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(edtSearch.getWindowToken(), 0);
        }

        // Dieu huong sang SearchResultFragment voi tu khoa
        Bundle args = new Bundle();
        args.putString("searchQuery", query);
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_searchFragment_to_searchResultFragment, args);
    }

    /**
     * Tai va hien thi lich su tim kiem duoi dang Chip trong ChipGroup
     */
    private void loadSearchHistory() {
        chipGroupHistory.removeAllViews();
        List<String> history = searchHistoryManager.getSearchHistory();

        // An/hien khu vuc lich su
        if (history.isEmpty()) {
            tvHistoryTitle.setVisibility(View.GONE);
            btnDeleteHistory.setVisibility(View.GONE);
        } else {
            tvHistoryTitle.setVisibility(View.VISIBLE);
            btnDeleteHistory.setVisibility(View.VISIBLE);
        }

        for (String query : history) {
            Chip chip = new Chip(requireContext());
            chip.setText(query);
            chip.setCheckable(false);
            chip.setChipBackgroundColorResource(R.color.gray_light);
            chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
            chip.setOnClickListener(v -> {
                // Khi bam vao tag: gan chu len EditText va tu dong kich hoat tim kiem
                edtSearch.setText(query);
                performSearch(query);
            });
            chipGroupHistory.addView(chip);
        }
    }

    /**
     * Hien dialog xac nhan xoa toan bo lich su tim kiem
     */
    private void showDeleteHistoryDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Xóa lịch sử?")
                .setMessage("Bạn có chắc chắn muốn xóa toàn bộ lịch sử tìm kiếm không?")
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Xác nhận", (dialog, which) -> {
                    // Xoa toan bo lich su
                    searchHistoryManager.clearHistory();
                    // Cap nhat lai giao dien
                    loadSearchHistory();
                    Toast.makeText(getContext(), "Đã xóa lịch sử", Toast.LENGTH_SHORT).show();
                })
                .show();
    }
}
