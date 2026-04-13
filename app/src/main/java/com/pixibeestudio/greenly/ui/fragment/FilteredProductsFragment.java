package com.pixibeestudio.greenly.ui.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
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
import com.pixibeestudio.greenly.ui.viewmodel.FilterViewModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Fragment hiển thị kết quả lọc sản phẩm.
 * Nhận arguments: sort_by, category_id, category_name, is_discount.
 */
public class FilteredProductsFragment extends Fragment
        implements ProductGridAdapter.OnProductAddCartListener,
                   ProductGridAdapter.OnFavoriteToggleListener {

    // === Header ===
    private ImageButton btnBackHome;
    private TextView tvSearchBox;
    private ImageButton btnCart;
    private TextView tvCartBadge;

    // === Filter Bar ===
    private ImageButton btnOpenFilter;
    private TextView tvFilterBadge;
    private TextView btnCurrentSort;
    private TextView btnCurrentCategory;
    private TextView btnCurrentDiscount;

    // === RecyclerView & Empty State ===
    private RecyclerView rvFilteredProducts;
    private ProductGridAdapter adapter;
    private TextView tvEmptyFilter;

    // === ViewModel & Session ===
    private CartViewModel cartViewModel;
    private FavoriteViewModel favoriteViewModel;
    private FilterViewModel filterViewModel;
    private SessionManager sessionManager;

    // === Dữ liệu lọc từ Arguments ===
    private String sortBy = "newest";
    private int categoryId = 0;
    private String categoryName = "";
    private boolean isDiscount = false;

    private final Set<Integer> favoriteProductIds = new HashSet<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_filtered_products, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo ViewModel & Session
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        favoriteViewModel = new ViewModelProvider(this).get(FavoriteViewModel.class);
        filterViewModel = new ViewModelProvider(this).get(FilterViewModel.class);
        sessionManager = new SessionManager(requireContext());

        // Lấy arguments
        parseArguments();

        // Ánh xạ views
        initViews(view);

        // Biến hình thanh Filter theo dữ liệu nhận được
        transformFilterBar();

        // Thiết lập sự kiện click
        setupClickListeners();

        // Thiết lập RecyclerView
        setupRecyclerView();

        // Load dữ liệu yêu thích
        loadFavorites();

        // Observe kết quả lọc từ API
        observeFilteredProducts();

        // Gọi API lọc sản phẩm
        filterViewModel.fetchFilteredProducts(sortBy, categoryId, isDiscount);
    }

    /**
     * Lấy dữ liệu lọc từ Bundle arguments.
     */
    private void parseArguments() {
        if (getArguments() != null) {
            sortBy = getArguments().getString("sort_by", "newest");
            categoryId = getArguments().getInt("category_id", 0);
            categoryName = getArguments().getString("category_name", "");
            String discountStr = getArguments().getString("is_discount", "false");
            isDiscount = "true".equalsIgnoreCase(discountStr);
        }
    }

    /**
     * Ánh xạ views từ layout.
     */
    private void initViews(View view) {
        // Header
        btnBackHome = view.findViewById(R.id.btnBackHome);
        tvSearchBox = view.findViewById(R.id.tvSearchBox);
        btnCart = view.findViewById(R.id.btnCart);
        tvCartBadge = view.findViewById(R.id.tvCartBadge);

        // Filter Bar
        btnOpenFilter = view.findViewById(R.id.btnOpenFilter);
        tvFilterBadge = view.findViewById(R.id.tvFilterBadge);
        btnCurrentSort = view.findViewById(R.id.btnCurrentSort);
        btnCurrentCategory = view.findViewById(R.id.btnCurrentCategory);
        btnCurrentDiscount = view.findViewById(R.id.btnCurrentDiscount);

        // RecyclerView & Empty State
        rvFilteredProducts = view.findViewById(R.id.rvFilteredProducts);
        tvEmptyFilter = view.findViewById(R.id.tvEmptyFilter);
    }

    /**
     * "Biến hình" thanh Filter dựa trên arguments nhận được.
     * - Thay text nút theo điều kiện lọc đang áp dụng.
     * - Đổi background sang trạng thái Active.
     * - Hiển thị Badge đỏ với số lượng bộ lọc.
     */
    private void transformFilterBar() {
        int filterCount = 0;

        // --- Sort ---
        if (sortBy != null && !sortBy.equals("default") && !sortBy.equals("newest")) {
            String sortLabel = getSortLabel(sortBy);
            btnCurrentSort.setText(sortLabel);
            setFilterTagActive(btnCurrentSort);
            filterCount++;
        } else if ("newest".equals(sortBy)) {
            // "Mới nhất" cũng là filter đã chọn (khác "Mặc định")
            btnCurrentSort.setText("Mới nhất");
            setFilterTagActive(btnCurrentSort);
            filterCount++;
        }

        // --- Category ---
        if (categoryName != null && !categoryName.isEmpty()) {
            btnCurrentCategory.setText(categoryName);
            setFilterTagActive(btnCurrentCategory);
            filterCount++;
        }

        // --- Discount ---
        if (isDiscount) {
            btnCurrentDiscount.setText("Đang giảm giá");
            setFilterTagActive(btnCurrentDiscount);
            filterCount++;
        }

        // --- Badge đỏ ---
        if (filterCount > 0) {
            tvFilterBadge.setText(String.valueOf(filterCount));
            tvFilterBadge.setVisibility(View.VISIBLE);
        } else {
            tvFilterBadge.setVisibility(View.GONE);
        }
    }

    /**
     * Chuyển text label cho từng loại sort.
     */
    private String getSortLabel(String sort) {
        switch (sort) {
            case "top_sales":
                return "Bán chạy";
            case "price_desc":
                return "Giá cao nhất";
            case "price_asc":
                return "Giá rẻ nhất";
            case "newest":
                return "Mới nhất";
            default:
                return "Lọc theo";
        }
    }

    /**
     * Đổi trạng thái nút filter sang Active (nền xanh nhạt, viền xanh, chữ xanh).
     */
    private void setFilterTagActive(TextView tag) {
        tag.setBackgroundResource(R.drawable.bg_filter_tag_active);
        tag.setTextColor(getResources().getColor(R.color.green_primary, null));
    }

    /**
     * Thiết lập tất cả sự kiện click.
     */
    private void setupClickListeners() {
        NavController navController = Navigation.findNavController(requireView());

        // Nút Back → về thẳng Home
        btnBackHome.setOnClickListener(v ->
                navController.popBackStack(R.id.homeFragment, false));

        // Ô tìm kiếm giả → chuyển sang SearchFragment
        tvSearchBox.setOnClickListener(v ->
                navController.navigate(R.id.action_filteredProductsFragment_to_searchFragment));

        // Nút Giỏ hàng → chuyển sang CartFragment
        btnCart.setOnClickListener(v ->
                navController.navigate(R.id.action_filteredProductsFragment_to_cartFragment));

        // Nút mở bộ lọc BottomSheet (tạm Toast)
        btnOpenFilter.setOnClickListener(v ->
                Toast.makeText(getContext(), "Mở lại bộ lọc", Toast.LENGTH_SHORT).show());

        // Các nút filter tag (tạm Toast, sau sẽ mở BottomSheet tại tab tương ứng)
        btnCurrentSort.setOnClickListener(v ->
                Toast.makeText(getContext(), "Mở lại bộ lọc", Toast.LENGTH_SHORT).show());

        btnCurrentCategory.setOnClickListener(v ->
                Toast.makeText(getContext(), "Mở lại bộ lọc", Toast.LENGTH_SHORT).show());

        btnCurrentDiscount.setOnClickListener(v ->
                Toast.makeText(getContext(), "Mở lại bộ lọc", Toast.LENGTH_SHORT).show());
    }

    /**
     * Thiết lập RecyclerView Grid 2 cột.
     */
    private void setupRecyclerView() {
        rvFilteredProducts.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        adapter = new ProductGridAdapter(new ArrayList<>(), this);
        adapter.setFavoriteListener(this);
        adapter.setFavoriteIds(favoriteProductIds);
        rvFilteredProducts.setAdapter(adapter);
    }

    /**
     * Observe LiveData từ FilterViewModel.
     * Khi có kết quả → đổ vào adapter. Nếu rỗng → hiện empty state.
     */
    private void observeFilteredProducts() {
        filterViewModel.getFilteredProductsLiveData().observe(getViewLifecycleOwner(), products -> {
            if (products != null && !products.isEmpty()) {
                // Có sản phẩm → hiện RecyclerView, ẩn empty text
                rvFilteredProducts.setVisibility(View.VISIBLE);
                tvEmptyFilter.setVisibility(View.GONE);

                adapter = new ProductGridAdapter(products, this);
                adapter.setFavoriteListener(this);
                adapter.setFavoriteIds(favoriteProductIds);
                rvFilteredProducts.setAdapter(adapter);
            } else {
                // Không có sản phẩm → ẩn RecyclerView, hiện empty text
                rvFilteredProducts.setVisibility(View.GONE);
                tvEmptyFilter.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * Load danh sách yêu thích từ API.
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

    // ======================== CALLBACK TỪ ADAPTER ========================

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
