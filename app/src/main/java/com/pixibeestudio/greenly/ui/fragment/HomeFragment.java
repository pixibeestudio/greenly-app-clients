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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.pixibeestudio.greenly.R;
import com.pixibeestudio.greenly.ui.adapter.BannerAdapter;
import com.pixibeestudio.greenly.ui.adapter.CategoryAdapter;
import com.pixibeestudio.greenly.ui.adapter.ProductGridAdapter;
import com.pixibeestudio.greenly.ui.adapter.ProductHorizontalAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Fragment Trang chủ - Hiển thị danh mục, banner, sản phẩm nổi bật,
 * sản phẩm giảm giá, Top 100 và tất cả sản phẩm dạng Grid.
 */
public class HomeFragment extends Fragment {

    // Thời gian tự động chuyển banner (3 giây)
    private static final int BANNER_AUTO_SLIDE_DELAY = 3000;

    private ViewPager2 vpBanner;
    private LinearLayout llIndicator;
    private RecyclerView rvCategories;
    private RecyclerView rvPopularProducts;
    private RecyclerView rvDiscountProducts;
    private RecyclerView rvTop100Products;
    private RecyclerView rvAllProducts;

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

        // Ánh xạ các view
        initViews(view);

        // Thiết lập từng phần giao diện
        setupCategories();
        setupBanner();
        setupPopularProducts();
        setupDiscountProducts();
        setupTop100Products();
        setupAllProducts();
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
    }

    // ======================== DANH MỤC ========================

    /**
     * Thiết lập RecyclerView danh mục sản phẩm (cuộn ngang).
     */
    private void setupCategories() {
        List<String> categories = Arrays.asList(
                "Rau củ", "Trái cây", "Thịt tươi", "Hải sản",
                "Sữa & Trứng", "Đồ uống", "Gia vị", "Đồ khô"
        );
        rvCategories.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rvCategories.setAdapter(new CategoryAdapter(categories));
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
     * Thiết lập RecyclerView sản phẩm nổi bật (cuộn ngang, không hiển thị icon).
     */
    private void setupPopularProducts() {
        List<String[]> products = generateMockProducts("Rau sạch", 10);
        rvPopularProducts.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rvPopularProducts.setAdapter(new ProductHorizontalAdapter(products, false));
    }

    // ======================== ĐANG GIẢM GIÁ ========================

    /**
     * Thiết lập RecyclerView sản phẩm đang giảm giá (cuộn ngang, không hiển thị icon).
     */
    private void setupDiscountProducts() {
        List<String[]> products = generateMockProducts("Trái cây", 10);
        rvDiscountProducts.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rvDiscountProducts.setAdapter(new ProductHorizontalAdapter(products, false));
    }

    // ======================== TOP 100 BÁN CHẠY ========================

    /**
     * Thiết lập RecyclerView Top 100 sản phẩm bán chạy (cuộn ngang, CÓ hiển thị icon).
     */
    private void setupTop100Products() {
        List<String[]> products = generateMockProducts("Thực phẩm", 10);
        rvTop100Products.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        // showIcons = true: hiển thị icon trái tim + nút thêm giỏ hàng
        rvTop100Products.setAdapter(new ProductHorizontalAdapter(products, true));
    }

    // ======================== TẤT CẢ SẢN PHẨM (GRID) ========================

    /**
     * Thiết lập RecyclerView tất cả sản phẩm (Grid 2 cột).
     */
    private void setupAllProducts() {
        List<String[]> products = generateMockProducts("Sản phẩm", 20);
        rvAllProducts.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rvAllProducts.setAdapter(new ProductGridAdapter(products));
    }

    // ======================== TIỆN ÍCH ========================

    /**
     * Tạo danh sách sản phẩm giả lập để test giao diện.
     * @param prefix Tiền tố tên sản phẩm
     * @param count Số lượng sản phẩm
     * @return Danh sách mảng [tên, giá]
     */
    private List<String[]> generateMockProducts(String prefix, int count) {
        List<String[]> products = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            String name = prefix + " " + i;
            // Giá ngẫu nhiên từ 15.000đ đến 150.000đ
            int price = (int) (Math.random() * 135 + 15) * 1000;
            String priceStr = String.format("%,dđ", price);
            products.add(new String[]{name, priceStr});
        }
        return products;
    }

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
