package com.pixibeestudio.greenly.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.imageview.ShapeableImageView;

import com.pixibeestudio.greenly.R;
import com.pixibeestudio.greenly.data.local.SessionManager;
import com.pixibeestudio.greenly.data.model.Product;
import com.pixibeestudio.greenly.data.model.Category;
import com.pixibeestudio.greenly.ui.adapter.BannerAdapter;
import com.pixibeestudio.greenly.ui.adapter.CategoryAdapter;
import com.pixibeestudio.greenly.ui.adapter.ProductGridAdapter;
import com.pixibeestudio.greenly.ui.adapter.ProductHorizontalAdapter;
import com.pixibeestudio.greenly.ui.viewmodel.CartViewModel;
import com.pixibeestudio.greenly.ui.viewmodel.FavoriteViewModel;
import com.pixibeestudio.greenly.ui.viewmodel.HomeViewModel;
import com.pixibeestudio.greenly.data.model.WishlistItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Fragment Trang chủ - Hiển thị danh mục, banner, sản phẩm nổi bật,
 * sản phẩm giảm giá, Top 100 và tất cả sản phẩm dạng Grid.
 */
public class HomeFragment extends Fragment implements ProductGridAdapter.OnProductAddCartListener, ProductHorizontalAdapter.OnProductAddCartListener,
        ProductGridAdapter.OnFavoriteToggleListener, ProductHorizontalAdapter.OnFavoriteToggleListener {

    // Thời gian tự động chuyển banner (3 giây)
    private static final int BANNER_AUTO_SLIDE_DELAY = 3000;

    private ViewPager2 vpBanner;
    private LinearLayout llIndicator;
    private RecyclerView rvCategories;
    private RecyclerView rvPopularProducts;
    private RecyclerView rvDiscountProducts;
    private RecyclerView rvTop100Products;
    private RecyclerView rvAllProducts;

    // Profile & Greeting
    private TextView tvGreeting;
    private TextView tvUserName;
    private ImageView ivAvatar;
    private SessionManager sessionManager;

    // Header logic
    private AppBarLayout appBarLayout;
    private ConstraintLayout layoutHeaderExpanded;
    private ConstraintLayout layoutHeaderPinned;
    private Toolbar toolbarSticky;
    private LinearLayout layoutSearch;
    private LinearLayout searchBarSticky;
    private Button btnFilterBy, btnCategoryFilter, btnDiscountFilter, btnResetFilter;

    private HomeViewModel homeViewModel;
    private CartViewModel cartViewModel;
    private FavoriteViewModel favoriteViewModel;

    // Danh sách ID sản phẩm yêu thích (dùng chung cho tất cả adapter)
    private final Set<Integer> favoriteProductIds = new HashSet<>();

    // Giữ tham chiếu adapter để cập nhật favoriteIds khi load xong
    private ProductGridAdapter allProductsAdapter;
    private ProductHorizontalAdapter popularAdapter;
    private ProductHorizontalAdapter discountAdapter;
    private ProductHorizontalAdapter top100Adapter;

    // Handler và Runnable cho auto-slide banner
    private final Handler bannerHandler = new Handler(Looper.getMainLooper());
    private final Runnable bannerRunnable = new Runnable() {
        @Override
        public void run() {
            if (vpBanner != null && vpBanner.getAdapter() != null) {
                int totalItems = vpBanner.getAdapter().getItemCount();
                if (totalItems > 0) {
                    int nextItem = (vpBanner.getCurrentItem() + 1) % totalItems;
                    vpBanner.setCurrentItem(nextItem, true);
                }
                bannerHandler.postDelayed(this, BANNER_AUTO_SLIDE_DELAY);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo ViewModel và SessionManager
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        favoriteViewModel = new ViewModelProvider(this).get(FavoriteViewModel.class);
        sessionManager = new SessionManager(requireContext());

        // Ánh xạ các view
        initViews(view);
        
        // Cập nhật giao diện theo trạng thái đăng nhập
        updateUIBasedOnAuth();
        
        // Khởi tạo LayoutManager và Adapter rỗng cho các danh sách để tránh reset vị trí cuộn
        setupInitialLists();

        // Thiết lập header cross-fade và filter logic
        setupHeaderLogic();

        // Observe Data từ ViewModel
        observeData();

        // Thiết lập từng phần giao diện
        setupBanner();
        setupPopularProducts();
        setupTop100Products();
    }
    
    /**
     * Cập nhật lời chào và thông tin người dùng dựa trên SessionManager
     */
    private void updateUIBasedOnAuth() {
        // Gắn lời chào theo thời gian thực
        if (tvGreeting != null) {
            tvGreeting.setText(getTimeBasedGreeting());
        }

        if (sessionManager.isLoggedIn()) {
            // User đã đăng nhập
            if (tvUserName != null) {
                tvUserName.setText(sessionManager.getUserName());
            }
            
            // Load avatar bằng Glide
            if (ivAvatar != null) {
                String avatarUrl = sessionManager.getUserAvatar();
                Glide.with(this)
                     .load(avatarUrl)
                     .placeholder(R.drawable.ic_default_avatar_placeholder)
                     .error(R.drawable.ic_default_avatar_placeholder)
                     .circleCrop()
                     .into(ivAvatar);
            }
        } else if (sessionManager.isGuestMode()) {
            // Luồng Guest
            if (tvUserName != null) {
                tvUserName.setText("Khách");
            }
            if (ivAvatar != null) {
                ivAvatar.setImageResource(R.drawable.ic_default_avatar_placeholder);
            }
        }
    }

    /**
     * Trả về lời chào dựa trên giờ trong ngày
     */
    private String getTimeBasedGreeting() {
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if (timeOfDay >= 0 && timeOfDay < 12) {
            return "Chào buổi sáng,";
        } else if (timeOfDay >= 12 && timeOfDay < 15) {
            return "Chào buổi trưa,";
        } else if (timeOfDay >= 15 && timeOfDay < 18) {
            return "Chào buổi chiều,";
        } else {
            return "Chào buổi tối,";
        }
    }

    /**
     * Khởi tạo khung layout cho các danh sách ngay từ đầu để giữ vị trí cuộn
     */
    private void setupInitialLists() {
        rvCategories.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rvCategories.setAdapter(new CategoryAdapter(new ArrayList<>(), this::onCategoryClick));
        
        rvDiscountProducts.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rvDiscountProducts.setAdapter(new ProductHorizontalAdapter(new ArrayList<>(), this));
        
        rvAllProducts.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rvAllProducts.setAdapter(new ProductGridAdapter(new ArrayList<>(), this));
    }

    /**
     * Quan sát dữ liệu từ ViewModel và cập nhật UI.
     */
    private void observeData() {
        homeViewModel.getCategoriesLiveData().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null && !categories.isEmpty()) {
                rvCategories.setAdapter(new CategoryAdapter(categories, this::onCategoryClick));
            }
        });

        // Tất cả sản phẩm (Grid)
        homeViewModel.getProductsLiveData().observe(getViewLifecycleOwner(), products -> {
            if (products != null && !products.isEmpty()) {
                allProductsAdapter = new ProductGridAdapter(products, this);
                allProductsAdapter.setFavoriteListener(this);
                allProductsAdapter.setFavoriteIds(favoriteProductIds);
                rvAllProducts.setAdapter(allProductsAdapter);
            }
        });

        // Sản phẩm giảm giá (Horizontal)
        homeViewModel.getDiscountedProductsLiveData().observe(getViewLifecycleOwner(), discountedProducts -> {
            if (discountedProducts != null && !discountedProducts.isEmpty()) {
                discountAdapter = new ProductHorizontalAdapter(discountedProducts, this);
                discountAdapter.setFavoriteListener(this);
                discountAdapter.setFavoriteIds(favoriteProductIds);
                rvDiscountProducts.setAdapter(discountAdapter);
            }
        });

        // Sản phẩm mới nhất (sắp xếp theo ID giảm dần, lấy 10 SP đầu)
        homeViewModel.getProductsLiveData().observe(getViewLifecycleOwner(), products -> {
            if (products != null && !products.isEmpty()) {
                List<Product> sorted = new ArrayList<>(products);
                sorted.sort((p1, p2) -> Integer.compare(p2.getId(), p1.getId()));
                List<Product> newest = sorted.subList(0, Math.min(10, sorted.size()));
                popularAdapter = new ProductHorizontalAdapter(newest, this);
                popularAdapter.setFavoriteListener(this);
                popularAdapter.setFavoriteIds(favoriteProductIds);
                rvPopularProducts.setAdapter(popularAdapter);
            }
        });

        // Top 10 bán chạy (tạm lấy 10 SP đầu từ danh sách chung)
        homeViewModel.getProductsLiveData().observe(getViewLifecycleOwner(), products -> {
            if (products != null && !products.isEmpty()) {
                List<Product> top10 = products.subList(0, Math.min(10, products.size()));
                top100Adapter = new ProductHorizontalAdapter(top10, this);
                top100Adapter.setFavoriteListener(this);
                top100Adapter.setFavoriteIds(favoriteProductIds);
                rvTop100Products.setAdapter(top100Adapter);
            }
        });

        // Observe danh sách yêu thích từ API → cập nhật favoriteProductIds cho tất cả adapter
        if (!sessionManager.isGuestMode()) {
            favoriteViewModel.getFavorites().observe(getViewLifecycleOwner(), wishlistItems -> {
                favoriteProductIds.clear();
                if (wishlistItems != null) {
                    for (WishlistItem item : wishlistItems) {
                        favoriteProductIds.add(item.getProductId());
                    }
                }
                // Refresh tất cả adapter đang giữ tham chiếu
                if (allProductsAdapter != null) allProductsAdapter.setFavoriteIds(favoriteProductIds);
                if (popularAdapter != null) popularAdapter.setFavoriteIds(favoriteProductIds);
                if (discountAdapter != null) discountAdapter.setFavoriteIds(favoriteProductIds);
                if (top100Adapter != null) top100Adapter.setFavoriteIds(favoriteProductIds);
            });
        }
    }

    /**
     * Ánh xạ tất cả các view từ layout.
     */
    private void initViews(View view) {
        vpBanner = view.findViewById(R.id.vp_banner);
        llIndicator = view.findViewById(R.id.ll_indicator);
        rvCategories = view.findViewById(R.id.rv_categories);
        rvPopularProducts = view.findViewById(R.id.rv_popular_products);
        rvDiscountProducts = view.findViewById(R.id.rv_discount_products);
        rvTop100Products = view.findViewById(R.id.rv_top100_products);
        rvAllProducts = view.findViewById(R.id.rv_all_products);
        
        // Profile Views
        tvGreeting = view.findViewById(R.id.tv_greeting);
        tvUserName = view.findViewById(R.id.tv_user_name);
        ivAvatar = view.findViewById(R.id.img_avatar);

        // Header Views
        appBarLayout = view.findViewById(R.id.app_bar_layout);
        layoutHeaderExpanded = view.findViewById(R.id.layoutHeaderExpanded);
        layoutHeaderPinned = view.findViewById(R.id.layoutHeaderPinned);
        toolbarSticky = view.findViewById(R.id.toolbarSticky);

        // Search Bar Views (Fake - click de navigate sang SearchFragment)
        layoutSearch = view.findViewById(R.id.layoutSearch);
        searchBarSticky = view.findViewById(R.id.searchBarSticky);

        View.OnClickListener searchClickListener = v ->
                Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_searchFragment);

        if (layoutSearch != null) layoutSearch.setOnClickListener(searchClickListener);
        if (searchBarSticky != null) searchBarSticky.setOnClickListener(searchClickListener);

        // Nút Yêu thích trên cả 2 Header -> navigate sang FavoriteFragment
        View.OnClickListener favoriteClickListener = v ->
                Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_favoriteFragment);

        ImageButton btnFavorite = view.findViewById(R.id.ic_favorite);
        if (btnFavorite != null) btnFavorite.setOnClickListener(favoriteClickListener);

        ImageButton btnFavSticky = view.findViewById(R.id.ibFavSticky);
        if (btnFavSticky != null) btnFavSticky.setOnClickListener(favoriteClickListener);

        // Filter Buttons
        btnFilterBy = view.findViewById(R.id.btnFilterBy);
        btnCategoryFilter = view.findViewById(R.id.btnCategoryFilter);
        btnDiscountFilter = view.findViewById(R.id.btnDiscountFilter);
        btnResetFilter = view.findViewById(R.id.btnResetFilter);
    }

    /**
     * Thiết lập logic làm mờ Header và mở Filter Dialog.
     */
    private void setupHeaderLogic() {
        if (appBarLayout != null && layoutHeaderExpanded != null && layoutHeaderPinned != null) {
            appBarLayout.addOnOffsetChangedListener((appBarLayout1, verticalOffset) -> {
                int totalScroll = appBarLayout1.getTotalScrollRange();
                float percentage = (float) Math.abs(verticalOffset) / totalScroll;
                
                // Khi cuộn lên, percentage tăng từ 0 lên 1
                layoutHeaderPinned.setAlpha(percentage);
                layoutHeaderExpanded.setAlpha(1f - percentage);
                
                // Tắt/bật touch event để tránh click nhầm khi mờ
                if (percentage > 0.1f) {
                    // Hiện Toolbar sticky để nhận touch và hiển thị nội dung
                    if (toolbarSticky != null) toolbarSticky.setVisibility(View.VISIBLE);
                    layoutHeaderPinned.setVisibility(View.VISIBLE);
                } else {
                    layoutHeaderPinned.setVisibility(View.GONE);
                    // INVISIBLE: Toolbar vẫn chiếm không gian (giữ collapsing behavior)
                    // nhưng KHÔNG nhận touch events → touch xuyên qua đến expanded header
                    if (toolbarSticky != null) toolbarSticky.setVisibility(View.INVISIBLE);
                }
            });
        }

        // Bắt sự kiện click mở Filter Dialog
        View.OnClickListener showFilterListener = v -> {
            new FilterDialogFragment().show(getChildFragmentManager(), "FilterDialog");
        };

        if (btnFilterBy != null) btnFilterBy.setOnClickListener(showFilterListener);
        if (btnCategoryFilter != null) btnCategoryFilter.setOnClickListener(showFilterListener);
        if (btnDiscountFilter != null) btnDiscountFilter.setOnClickListener(showFilterListener);
    }

    // ======================== BANNER ========================

    /**
     * Thiết lập ViewPager2 banner với auto-slide và indicator.
     */
    private void setupBanner() {
        // Dữ liệu banner giả lập (3 màu nền khác nhau)
        List<Integer> bannerColors = Arrays.asList(
                Color.parseColor("#A5D6A7"),  // Xanh lá nhạt
                Color.parseColor("#FFCC80"),  // Cam nhạt
                Color.parseColor("#90CAF9")   // Xanh dương nhạt
        );

        BannerAdapter bannerAdapter = new BannerAdapter(bannerColors);
        vpBanner.setAdapter(bannerAdapter);

        // Tạo indicator cho banner
        createBannerIndicator(bannerColors.size());

        // Lắng nghe sự kiện chuyển trang để cập nhật indicator
        vpBanner.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateBannerIndicator(position);
            }
        });

        // Bắt đầu auto-slide
        bannerHandler.postDelayed(bannerRunnable, BANNER_AUTO_SLIDE_DELAY);
    }

    /**
     * Tạo các chấm indicator cho banner.
     */
    private void createBannerIndicator(int count) {
        llIndicator.removeAllViews();
        for (int i = 0; i < count; i++) {
            ImageView dot = new ImageView(requireContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    dpToPx(8), dpToPx(8));
            params.setMargins(dpToPx(4), 0, dpToPx(4), 0);
            dot.setLayoutParams(params);

            // Chấm đầu tiên active, còn lại inactive
            if (i == 0) {
                dot.setImageResource(R.drawable.bg_indicator_active);
            } else {
                dot.setImageResource(R.drawable.bg_indicator_inactive);
            }
            llIndicator.addView(dot);
        }
    }

    /**
     * Cập nhật trạng thái indicator khi chuyển banner.
     */
    private void updateBannerIndicator(int selectedPosition) {
        for (int i = 0; i < llIndicator.getChildCount(); i++) {
            ImageView dot = (ImageView) llIndicator.getChildAt(i);
            if (i == selectedPosition) {
                dot.setImageResource(R.drawable.bg_indicator_active);
            } else {
                dot.setImageResource(R.drawable.bg_indicator_inactive);
            }
        }
    }

    // ======================== SẢN PHẨM NỔI BẬT ========================

    /**
     * Thiết lập RecyclerView sản phẩm mới nhất (cuộn ngang).
     * Dữ liệu thật sẽ được nạp qua observeData()
     */
    private void setupPopularProducts() {
        rvPopularProducts.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rvPopularProducts.setAdapter(new ProductHorizontalAdapter(new ArrayList<>(), this));
    }

    // ======================== ĐANG GIẢM GIÁ ========================

    /**
     * Thiết lập RecyclerView sản phẩm đang giảm giá (cuộn ngang, không hiển thị icon).
     * Dữ liệu thật sẽ được nạp qua observeData()
     */
    private void setupDiscountProducts() {
        rvDiscountProducts.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rvDiscountProducts.setAdapter(new ProductHorizontalAdapter(new ArrayList<>(), this));
    }

    // ======================== TOP 100 BÁN CHẠY ========================

    /**
     * Thiết lập RecyclerView Top 10 sản phẩm bán chạy (cuộn ngang).
     * Dữ liệu thật sẽ được nạp qua observeData()
     */
    private void setupTop100Products() {
        rvTop100Products.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rvTop100Products.setAdapter(new ProductHorizontalAdapter(new ArrayList<>(), this));
    }

    // ======================== THÊM VÀO GIỎ HÀNG ========================

    /**
     * Xử lý sự kiện thêm sản phẩm vào giỏ hàng từ adapter.
     * Nếu là Guest, hiện popup yêu cầu đăng nhập.
     */
    @Override
    public void onAddCartClick(Product product) {
        if (sessionManager.isGuestMode()) {
            showGuestLoginPopup();
        } else {
            cartViewModel.addToCart(product.getId(), 1);
            Toast.makeText(requireContext(), "Đang thêm vào giỏ...", Toast.LENGTH_SHORT).show();
        }
    }

    // ======================== YÊU THÍCH (TOGGLE) ========================

    /**
     * Xử lý sự kiện toggle yêu thích từ adapter.
     * Optimistic UI đã được adapter xử lý, ở đây chỉ gọi API.
     */
    @Override
    public void onFavoriteToggle(Product product, boolean isNowFavorite) {
        if (sessionManager.isGuestMode()) {
            showGuestLoginPopup();
            return;
        }
        // Cập nhật Set local (đồng bộ giữa các adapter dùng chung Set)
        if (isNowFavorite) {
            favoriteProductIds.add(product.getId());
        } else {
            favoriteProductIds.remove(product.getId());
        }
        // Gọi API toggle (âm thầm, không cần observe kết quả)
        favoriteViewModel.toggleFavorite(product.getId());
    }

    /**
     * Hiển thị popup yêu cầu đăng nhập cho Guest.
     */
    private void showGuestLoginPopup() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Bạn chưa đăng nhập")
                .setMessage("Vui lòng đăng nhập để mua hàng")
                .setPositiveButton("Đăng nhập", (dialog, which) -> {
                    // Chuyển về màn Login
                    Navigation.findNavController(requireView()).navigate(R.id.action_homeFragment_to_loginFragment);
                })
                .setNegativeButton("Ở lại", (dialog, which) -> {
                    // Đóng dialog
                    dialog.dismiss();
                })
                .setCancelable(false)
                .show();
    }

    // ======================== DANH MỤC SẢN PHẨM ========================

    /**
     * Xử lý sự kiện click vào danh mục → chuyển sang màn hình sản phẩm theo danh mục.
     */
    private void onCategoryClick(Category category) {
        Bundle bundle = new Bundle();
        bundle.putInt("categoryId", category.getId());
        bundle.putString("categoryName", category.getName());
        Navigation.findNavController(requireView())
                .navigate(R.id.action_homeFragment_to_categoryProductFragment, bundle);
    }

    // ======================== TẤT CẢ SẢN PHẨM (GRID) ========================

    /**
     * Thiết lập RecyclerView tất cả sản phẩm (Grid 2 cột).
     * Dữ liệu thật sẽ được nạp qua observeData()
     */
    private void setupAllProducts() {
        rvAllProducts.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rvAllProducts.setAdapter(new ProductGridAdapter(new ArrayList<>(), this));
    }

    // ======================== TIỆN ÍCH ========================


    /**
     * Chuyển đổi dp sang px.
     */
    private int dpToPx(int dp) {
        float density = requireContext().getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Hủy auto-slide khi Fragment bị hủy để tránh memory leak
        bannerHandler.removeCallbacks(bannerRunnable);
    }
}
