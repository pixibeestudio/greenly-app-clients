package com.pixibeestudio.greenly.ui.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pixibeestudio.greenly.R;
import com.pixibeestudio.greenly.data.local.SessionManager;
import com.pixibeestudio.greenly.data.model.Product;
import com.pixibeestudio.greenly.data.model.WishlistItem;
import com.pixibeestudio.greenly.ui.adapter.ProductGridAdapter;
import com.pixibeestudio.greenly.ui.viewmodel.CartViewModel;
import com.pixibeestudio.greenly.ui.viewmodel.FavoriteViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Fragment hiển thị danh sách sản phẩm theo danh mục.
 * Nhận categoryId và categoryName qua Bundle arguments.
 */
public class CategoryProductFragment extends Fragment
        implements ProductGridAdapter.OnProductAddCartListener,
                   ProductGridAdapter.OnFavoriteToggleListener {

    private ImageButton btnBack, btnSearch;
    private TextView tvCategoryTitle;
    private RecyclerView rvCategoryProducts;

    // Filter tags
    private TextView tvFilterAll, tvFilterPriceAsc, tvFilterPriceDesc, tvFilterNewest, tvFilterTopSales;
    private TextView activeFilter;

    private ProductGridAdapter adapter;
    private CartViewModel cartViewModel;
    private FavoriteViewModel favoriteViewModel;
    private SessionManager sessionManager;

    private int categoryId = -1;
    private String categoryName = "";
    private final Set<Integer> favoriteProductIds = new HashSet<>();
    private List<Product> originalProducts = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_category_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo ViewModel và Manager
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        favoriteViewModel = new ViewModelProvider(this).get(FavoriteViewModel.class);
        sessionManager = new SessionManager(requireContext());

        // Lấy arguments
        if (getArguments() != null) {
            categoryId = getArguments().getInt("categoryId", -1);
            categoryName = getArguments().getString("categoryName", "");
        }

        // Ánh xạ views
        initViews(view);

        // Thiết lập click listeners
        setupClickListeners();

        // Thiết lập RecyclerView
        setupRecyclerView();

        // Thiết lập Filter Bar
        setupFilterBar();

        // Load dữ liệu
        loadFavorites();
        loadProducts();
    }

    /**
     * Ánh xạ tất cả views từ layout.
     */
    private void initViews(View view) {
        btnBack = view.findViewById(R.id.btnBack);
        btnSearch = view.findViewById(R.id.btnSearch);
        tvCategoryTitle = view.findViewById(R.id.tvCategoryTitle);
        rvCategoryProducts = view.findViewById(R.id.rvCategoryProducts);

        // Filter tags
        tvFilterAll = view.findViewById(R.id.tvFilterAll);
        tvFilterPriceAsc = view.findViewById(R.id.tvFilterPriceAsc);
        tvFilterPriceDesc = view.findViewById(R.id.tvFilterPriceDesc);
        tvFilterNewest = view.findViewById(R.id.tvFilterNewest);
        tvFilterTopSales = view.findViewById(R.id.tvFilterTopSales);

        // Hiển thị tên danh mục
        tvCategoryTitle.setText(categoryName);
    }

    /**
     * Thiết lập các click listener cho header buttons.
     */
    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        btnSearch.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_categoryProductFragment_to_searchFragment));
    }

    /**
     * Thiết lập RecyclerView Grid 2 cột.
     */
    private void setupRecyclerView() {
        rvCategoryProducts.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        adapter = new ProductGridAdapter(new ArrayList<>(), this);
        adapter.setFavoriteListener(this);
        adapter.setFavoriteIds(favoriteProductIds);
        rvCategoryProducts.setAdapter(adapter);
    }

    /**
     * Thiết lập Filter Bar: click vào tag để lọc/sort danh sách sản phẩm.
     */
    private void setupFilterBar() {
        activeFilter = tvFilterAll; // Mặc định "Tất cả" active

        View.OnClickListener filterClickListener = v -> {
            // Reset filter cũ
            if (activeFilter != null) {
                activeFilter.setBackgroundResource(R.drawable.bg_filter_inactive);
                activeFilter.setTextColor(getResources().getColor(R.color.black, null));
            }
            // Active filter mới
            TextView clickedFilter = (TextView) v;
            clickedFilter.setBackgroundResource(R.drawable.bg_filter_active);
            clickedFilter.setTextColor(getResources().getColor(R.color.white, null));
            activeFilter = clickedFilter;

            // Áp dụng logic lọc
            applyFilter(clickedFilter.getId());
        };

        tvFilterAll.setOnClickListener(filterClickListener);
        tvFilterPriceAsc.setOnClickListener(filterClickListener);
        tvFilterPriceDesc.setOnClickListener(filterClickListener);
        tvFilterNewest.setOnClickListener(filterClickListener);
        tvFilterTopSales.setOnClickListener(filterClickListener);
    }

    /**
     * Áp dụng logic lọc/sort dựa trên filter được chọn.
     */
    private void applyFilter(int filterId) {
        if (originalProducts == null || originalProducts.isEmpty()) return;

        List<Product> sorted = new ArrayList<>(originalProducts);

        if (filterId == R.id.tvFilterPriceAsc) {
            // Giá tăng dần: ưu tiên giá khuyến mãi nếu có
            Collections.sort(sorted, (a, b) -> {
                double priceA = a.getDiscountPrice() > 0 ? a.getDiscountPrice() : a.getPrice();
                double priceB = b.getDiscountPrice() > 0 ? b.getDiscountPrice() : b.getPrice();
                return Double.compare(priceA, priceB);
            });
        } else if (filterId == R.id.tvFilterPriceDesc) {
            // Giá giảm dần
            Collections.sort(sorted, (a, b) -> {
                double priceA = a.getDiscountPrice() > 0 ? a.getDiscountPrice() : a.getPrice();
                double priceB = b.getDiscountPrice() > 0 ? b.getDiscountPrice() : b.getPrice();
                return Double.compare(priceB, priceA);
            });
        } else if (filterId == R.id.tvFilterNewest) {
            // Mới nhất: sort theo ID giảm dần (ID lớn = mới hơn)
            Collections.sort(sorted, (a, b) -> Integer.compare(b.getId(), a.getId()));
        } else if (filterId == R.id.tvFilterTopSales) {
            // Bán chạy: sort theo sold_count giảm dần
            Collections.sort(sorted, (a, b) -> Integer.compare(b.getSoldCount(), a.getSoldCount()));
        }
        // filterId == R.id.tvFilterAll → giữ nguyên thứ tự gốc

        adapter = new ProductGridAdapter(sorted, this);
        adapter.setFavoriteListener(this);
        adapter.setFavoriteIds(favoriteProductIds);
        rvCategoryProducts.setAdapter(adapter);
    }

    /**
     * Load danh sách yêu thích từ API để đồng bộ icon trái tim.
     */
    private void loadFavorites() {
        if (sessionManager.isGuestMode()) return;
        favoriteViewModel.getFavorites().observe(getViewLifecycleOwner(), wishlistItems -> {
            favoriteProductIds.clear();
            if (wishlistItems != null) {
                for (WishlistItem item : wishlistItems) {
                    favoriteProductIds.add(item.getProductId());
                }
            }
            if (adapter != null) {
                adapter.setFavoriteIds(favoriteProductIds);
            }
        });
    }

    /**
     * Load sản phẩm theo danh mục từ API.
     */
    private void loadProducts() {
        if (categoryId == -1) return;

        com.pixibeestudio.greenly.data.repository.ProductRepository repository =
                new com.pixibeestudio.greenly.data.repository.ProductRepository(requireContext());

        repository.getProductsByCategory(categoryId).observe(getViewLifecycleOwner(), products -> {
            if (products != null && !products.isEmpty()) {
                originalProducts = products;
                adapter = new ProductGridAdapter(products, this);
                adapter.setFavoriteListener(this);
                adapter.setFavoriteIds(favoriteProductIds);
                rvCategoryProducts.setAdapter(adapter);
            } else {
                originalProducts = new ArrayList<>();
                adapter = new ProductGridAdapter(new ArrayList<>(), this);
                rvCategoryProducts.setAdapter(adapter);
                Toast.makeText(getContext(), "Không có sản phẩm trong danh mục này", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onAddCartClick(Product product) {
        if (sessionManager.isGuestMode()) {
            showGuestLoginPopup();
            return;
        }
        cartViewModel.addToCart(product.getId(), 1);
        Toast.makeText(getContext(), "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFavoriteToggle(Product product, boolean isNowFavorite) {
        if (sessionManager.isGuestMode()) {
            showGuestLoginPopup();
            return;
        }
        if (isNowFavorite) {
            favoriteProductIds.add(product.getId());
        } else {
            favoriteProductIds.remove(product.getId());
        }
        favoriteViewModel.toggleFavorite(product.getId());
    }

    /**
     * Hiển thị popup yêu cầu đăng nhập cho Guest.
     */
    private void showGuestLoginPopup() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Yêu cầu đăng nhập")
                .setMessage("Bạn cần đăng nhập để sử dụng tính năng này.")
                .setPositiveButton("Đăng nhập", (dialog, which) -> {
                    Navigation.findNavController(requireView()).navigate(R.id.loginFragment);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
