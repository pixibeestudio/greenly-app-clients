package com.pixibeestudio.greenly.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import com.pixibeestudio.greenly.R;

/**
 * Fragment Danh sách Yêu thích
 * Hiển thị các sản phẩm đã được đánh dấu yêu thích.
 */
public class FavoriteFragment extends Fragment {

    // Header
    private ImageButton btnBack;
    private ImageButton btnSearch;
    private ImageButton btnCart;
    private ImageButton btnClearAll;

    // Body
    private RecyclerView rvFavorites;
    private LinearLayout layoutEmptyFavorite;

    // Footer
    private MaterialButton btnAddAllToCart;

    private NavController navController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorite, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        // Ánh xạ view
        initViews(view);

        // Thiết lập RecyclerView
        setupRecyclerView();

        // Gắn sự kiện click
        setupClickListeners();
    }

    /**
     * Ánh xạ tất cả view từ layout
     */
    private void initViews(View view) {
        // Header
        btnBack = view.findViewById(R.id.btnBack);
        btnSearch = view.findViewById(R.id.btnSearch);
        btnCart = view.findViewById(R.id.btnCart);
        btnClearAll = view.findViewById(R.id.btnClearAll);

        // Body
        rvFavorites = view.findViewById(R.id.rvFavorites);
        layoutEmptyFavorite = view.findViewById(R.id.layoutEmptyFavorite);

        // Footer
        btnAddAllToCart = view.findViewById(R.id.btnAddAllToCart);
    }

    /**
     * Thiết lập RecyclerView danh sách yêu thích
     */
    private void setupRecyclerView() {
        rvFavorites.setLayoutManager(new LinearLayoutManager(requireContext()));
        // TODO: Gắn FavoriteAdapter khi có dữ liệu thật
    }

    /**
     * Gắn sự kiện click cho tất cả các nút
     */
    private void setupClickListeners() {
        // 1. Nút Back: Quay lại màn trước
        btnBack.setOnClickListener(v -> navController.popBackStack());

        // 2. Nút Tìm kiếm: Chuyển sang SearchFragment
        btnSearch.setOnClickListener(v ->
                navController.navigate(R.id.action_favoriteFragment_to_searchFragment));

        // 3. Nút Giỏ hàng: Chuyển sang CartFragment
        btnCart.setOnClickListener(v ->
                navController.navigate(R.id.action_favoriteFragment_to_cartFragment));

        // 4. Nút Xóa tất cả: Hiện dialog xác nhận
        btnClearAll.setOnClickListener(v -> showClearAllDialog());

        // 5. Nút Thêm tất cả vào giỏ: Hiện dialog xác nhận
        btnAddAllToCart.setOnClickListener(v -> showAddAllToCartDialog());
    }

    /**
     * Hiển thị dialog xác nhận xóa tất cả sản phẩm yêu thích
     */
    private void showClearAllDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Xóa danh sách?")
                .setMessage("Bạn có chắc chắn muốn xóa tất cả sản phẩm khỏi danh sách yêu thích?")
                .setPositiveButton("Xác nhận", (dialog, which) -> {
                    // TODO: Gọi API hoặc xóa local khi tích hợp logic thật
                    Toast.makeText(requireContext(), "Đang xóa...", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }

    /**
     * Hiển thị dialog xác nhận thêm tất cả vào giỏ hàng
     */
    private void showAddAllToCartDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Thêm vào giỏ hàng?")
                .setMessage("Thêm toàn bộ sản phẩm trong danh sách này vào giỏ hàng?")
                .setPositiveButton("Xác nhận", (dialog, which) -> {
                    // TODO: Gọi API addToCart cho từng sản phẩm khi tích hợp logic thật
                    Toast.makeText(requireContext(), "Đang thêm...", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }
}
