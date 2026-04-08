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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import com.pixibeestudio.greenly.R;
import com.pixibeestudio.greenly.data.model.Product;
import com.pixibeestudio.greenly.data.model.WishlistItem;
import com.pixibeestudio.greenly.ui.adapter.FavoriteAdapter;
import com.pixibeestudio.greenly.ui.viewmodel.CartViewModel;
import com.pixibeestudio.greenly.ui.viewmodel.FavoriteViewModel;

import java.util.List;

/**
 * Fragment Danh sách Yêu thích
 * Hiển thị các sản phẩm đã được đánh dấu yêu thích.
 */
public class FavoriteFragment extends Fragment implements FavoriteAdapter.OnFavoriteItemListener {

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
    private FavoriteViewModel favoriteViewModel;
    private CartViewModel cartViewModel;
    private FavoriteAdapter favoriteAdapter;

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
        favoriteViewModel = new ViewModelProvider(this).get(FavoriteViewModel.class);
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);

        // Ánh xạ view
        initViews(view);

        // Thiết lập RecyclerView
        setupRecyclerView();

        // Gắn sự kiện click
        setupClickListeners();

        // Tải danh sách yêu thích từ API
        loadFavorites();
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
        favoriteAdapter = new FavoriteAdapter(this);
        rvFavorites.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvFavorites.setAdapter(favoriteAdapter);
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

    // ======================== TẢI DỮ LIỆU ========================

    /**
     * Gọi API lấy danh sách yêu thích và cập nhật UI.
     */
    private void loadFavorites() {
        favoriteViewModel.getFavorites().observe(getViewLifecycleOwner(), wishlistItems -> {
            if (wishlistItems != null && !wishlistItems.isEmpty()) {
                showContentState();
                favoriteAdapter.setItems(wishlistItems);
            } else {
                showEmptyState();
            }
        });
    }

    // ======================== TRẠNG THÁI UI ========================

    /**
     * Hiển thị danh sách yêu thích (có dữ liệu).
     */
    private void showContentState() {
        rvFavorites.setVisibility(View.VISIBLE);
        layoutEmptyFavorite.setVisibility(View.GONE);
        btnAddAllToCart.setVisibility(View.VISIBLE);
        btnClearAll.setVisibility(View.VISIBLE);
    }

    /**
     * Hiển thị trạng thái trống (không có sản phẩm yêu thích).
     */
    private void showEmptyState() {
        rvFavorites.setVisibility(View.GONE);
        layoutEmptyFavorite.setVisibility(View.VISIBLE);
        btnAddAllToCart.setVisibility(View.GONE);
        btnClearAll.setVisibility(View.GONE);
    }

    // ======================== CALLBACK TỪ ADAPTER ========================

    /**
     * Xóa 1 item: Hiện dialog xác nhận -> gọi toggle API -> load lại list.
     */
    @Override
    public void onRemoveItem(WishlistItem item) {
        Product product = item.getProduct();
        String productName = product != null ? product.getName() : "sản phẩm này";

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Xóa yêu thích?")
                .setMessage("Bỏ \"" + productName + "\" khỏi danh sách yêu thích?")
                .setPositiveButton("Xác nhận", (dialog, which) -> {
                    // Gọi API toggle để xóa item
                    favoriteViewModel.toggleFavorite(item.getProductId()).observe(
                            getViewLifecycleOwner(), result -> {
                                Toast.makeText(requireContext(), "Đã xóa khỏi yêu thích", Toast.LENGTH_SHORT).show();
                                // Load lại danh sách
                                loadFavorites();
                            });
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }

    /**
     * Thêm 1 item vào giỏ hàng (số lượng = 1).
     */
    @Override
    public void onAddToCartItem(WishlistItem item) {
        if (item.getProduct() == null) return;
        cartViewModel.addToCart(item.getProduct().getId(), 1);
        Toast.makeText(requireContext(), "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
    }

    // ======================== DIALOG ========================

    /**
     * Hiển thị dialog xác nhận xóa tất cả sản phẩm yêu thích.
     */
    private void showClearAllDialog() {
        if (favoriteAdapter.getItemCount() == 0) {
            Toast.makeText(requireContext(), "Danh sách yêu thích đang trống", Toast.LENGTH_SHORT).show();
            return;
        }
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Xóa danh sách?")
                .setMessage("Bạn có chắc chắn muốn xóa tất cả sản phẩm khỏi danh sách yêu thích?")
                .setPositiveButton("Xác nhận", (dialog, which) -> {
                    favoriteViewModel.clearAllFavorites().observe(getViewLifecycleOwner(), success -> {
                        if (success != null && success) {
                            Toast.makeText(requireContext(), "Đã xóa toàn bộ", Toast.LENGTH_SHORT).show();
                            loadFavorites();
                        } else {
                            Toast.makeText(requireContext(), "Xóa thất bại", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }

    /**
     * Hiển thị dialog xác nhận thêm tất cả vào giỏ hàng.
     * Chạy vòng lặp gọi addToCart cho từng sản phẩm.
     */
    private void showAddAllToCartDialog() {
        List<WishlistItem> items = favoriteAdapter.getItems();
        if (items == null || items.isEmpty()) {
            Toast.makeText(requireContext(), "Danh sách yêu thích đang trống", Toast.LENGTH_SHORT).show();
            return;
        }
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Thêm vào giỏ hàng?")
                .setMessage("Thêm toàn bộ " + items.size() + " sản phẩm trong danh sách này vào giỏ hàng?")
                .setPositiveButton("Xác nhận", (dialog, which) -> {
                    // Vòng lặp gọi API addToCart cho từng sản phẩm
                    int count = 0;
                    for (WishlistItem item : items) {
                        if (item.getProduct() != null) {
                            cartViewModel.addToCart(item.getProduct().getId(), 1);
                            count++;
                        }
                    }
                    Toast.makeText(requireContext(),
                            "Đã thêm " + count + " sản phẩm vào giỏ hàng",
                            Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }
}
