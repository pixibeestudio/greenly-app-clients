package com.pixibeestudio.greenly.ui.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.tabs.TabLayout;

import com.pixibeestudio.greenly.R;
import com.pixibeestudio.greenly.data.model.Category;
import com.pixibeestudio.greenly.data.model.CategoryResponse;
import com.pixibeestudio.greenly.data.network.ApiService;
import com.pixibeestudio.greenly.data.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * BottomSheetDialog bộ lọc sản phẩm.
 * Hỗ trợ 4 chế độ hiển thị:
 * - MODE_ALL: Hiển thị đầy đủ 3 khối + TabLayout + sync scrolling.
 * - MODE_SORT_ONLY: Chỉ hiển thị khối Lọc theo.
 * - MODE_CATEGORY_ONLY: Chỉ hiển thị khối Loại hàng.
 * - MODE_DISCOUNT_ONLY: Chỉ hiển thị khối Ưu đãi.
 */
public class FilterBottomSheetFragment extends BottomSheetDialogFragment {

    private static final String TAG = "FilterBottomSheet";

    // === Chế độ hiển thị ===
    public static final int MODE_ALL = 0;
    public static final int MODE_SORT_ONLY = 1;
    public static final int MODE_CATEGORY_ONLY = 2;
    public static final int MODE_DISCOUNT_ONLY = 3;
    private static final String ARG_MODE = "filter_mode";

    // === Views ===
    private ImageButton btnCloseFilter;
    private TabLayout tabLayoutFilter;
    private NestedScrollView nsvFilter;
    private TextView tvFilterTitle;

    // Các khối nội dung (section) + divider giữa các khối
    private LinearLayout sectionSort, sectionCategory, sectionDiscount;
    private View dividerSort, dividerCategory;

    // Radio & Checkbox
    private RadioGroup rgSortFilter, rgCategoryFilter;
    private RadioButton rbDefault, rbNewest, rbTopSales, rbPriceHigh, rbPriceLow;
    private RadioButton rbAllCategory;
    private CheckBox cbDiscounted, cbMostDiscount;

    // Footer
    private TextView btnReset, btnApply;

    // Cờ tránh vòng lặp khi sync scroll ↔ tab
    private boolean isScrollingByTab = false;

    // Chế độ hiện tại
    private int currentMode = MODE_ALL;

    // Callback gửi kết quả lọc về Fragment cha
    private OnFilterAppliedListener filterListener;

    /**
     * Interface giao tiếp kết quả lọc về Fragment gọi BottomSheet.
     */
    public interface OnFilterAppliedListener {
        void onFilterApplied(String sortType, int categoryId, boolean discounted, boolean mostDiscount);
        void onFilterReset();
    }

    public void setOnFilterAppliedListener(OnFilterAppliedListener listener) {
        this.filterListener = listener;
    }

    /**
     * Factory method tạo instance với mode chỉ định.
     */
    public static FilterBottomSheetFragment newInstance(int mode) {
        FilterBottomSheetFragment fragment = new FilterBottomSheetFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_MODE, mode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentMode = getArguments().getInt(ARG_MODE, MODE_ALL);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog bsDialog = (BottomSheetDialog) dialogInterface;
            View bottomSheet = bsDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);

                // Chiều cao dựa theo mode
                int screenHeight = getResources().getDisplayMetrics().heightPixels;
                ViewGroup.LayoutParams params = bottomSheet.getLayoutParams();
                if (currentMode == MODE_ALL) {
                    params.height = (int) (screenHeight * 0.80);
                } else {
                    // Cho các mode đơn lẻ, dùng wrap_content (tự co theo nội dung)
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                }
                bottomSheet.setLayoutParams(params);

                // Luôn expanded ngay lập tức, không half-expanded
                behavior.setSkipCollapsed(true);
                behavior.setPeekHeight(screenHeight);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_filter_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupCloseButton();
        applyMode();
        loadCategories();
        setupFooterButtons();
    }

    /**
     * Ánh xạ tất cả view từ layout.
     */
    private void initViews(View view) {
        btnCloseFilter = view.findViewById(R.id.btnCloseFilter);
        tabLayoutFilter = view.findViewById(R.id.tabLayoutFilter);
        nsvFilter = view.findViewById(R.id.nsvFilter);

        // Các khối section
        sectionSort = view.findViewById(R.id.sectionSort);
        sectionCategory = view.findViewById(R.id.sectionCategory);
        sectionDiscount = view.findViewById(R.id.sectionDiscount);

        // Divider giữa các khối (lấy theo thứ tự trong layout)
        // dividerSort nằm giữa sectionSort và sectionCategory
        // dividerCategory nằm giữa sectionCategory và sectionDiscount
        LinearLayout contentContainer = (LinearLayout) nsvFilter.getChildAt(0);
        if (contentContainer != null) {
            // Tìm các View divider (là View có height=1dp)
            for (int i = 0; i < contentContainer.getChildCount(); i++) {
                View child = contentContainer.getChildAt(i);
                // Divider sau sectionSort
                if (i > 0 && contentContainer.getChildAt(i - 1) == sectionSort && child instanceof View && !(child instanceof LinearLayout)) {
                    dividerSort = child;
                }
                // Divider sau sectionCategory
                if (i > 0 && contentContainer.getChildAt(i - 1) == sectionCategory && child instanceof View && !(child instanceof LinearLayout)) {
                    dividerCategory = child;
                }
            }
        }

        // Radio & Checkbox
        rgSortFilter = view.findViewById(R.id.rgSortFilter);
        rgCategoryFilter = view.findViewById(R.id.rgCategoryFilter);

        rbDefault = view.findViewById(R.id.rbDefault);
        rbNewest = view.findViewById(R.id.rbNewest);
        rbTopSales = view.findViewById(R.id.rbTopSales);
        rbPriceHigh = view.findViewById(R.id.rbPriceHigh);
        rbPriceLow = view.findViewById(R.id.rbPriceLow);
        rbAllCategory = view.findViewById(R.id.rbAllCategory);

        cbDiscounted = view.findViewById(R.id.cbDiscounted);
        cbMostDiscount = view.findViewById(R.id.cbMostDiscount);

        // Footer
        btnReset = view.findViewById(R.id.btnReset);
        btnApply = view.findViewById(R.id.btnApply);
    }

    /**
     * Áp dụng chế độ hiển thị: ẩn/hiện các section, TabLayout theo mode.
     */
    private void applyMode() {
        switch (currentMode) {
            case MODE_SORT_ONLY:
                // Chỉ hiện khối Lọc theo, ẩn TabLayout
                tabLayoutFilter.setVisibility(View.GONE);
                sectionCategory.setVisibility(View.GONE);
                sectionDiscount.setVisibility(View.GONE);
                if (dividerSort != null) dividerSort.setVisibility(View.GONE);
                if (dividerCategory != null) dividerCategory.setVisibility(View.GONE);
                break;

            case MODE_CATEGORY_ONLY:
                // Chỉ hiện khối Loại hàng, ẩn TabLayout
                tabLayoutFilter.setVisibility(View.GONE);
                sectionSort.setVisibility(View.GONE);
                sectionDiscount.setVisibility(View.GONE);
                if (dividerSort != null) dividerSort.setVisibility(View.GONE);
                if (dividerCategory != null) dividerCategory.setVisibility(View.GONE);
                break;

            case MODE_DISCOUNT_ONLY:
                // Chỉ hiện khối Ưu đãi, ẩn TabLayout
                tabLayoutFilter.setVisibility(View.GONE);
                sectionSort.setVisibility(View.GONE);
                sectionCategory.setVisibility(View.GONE);
                if (dividerSort != null) dividerSort.setVisibility(View.GONE);
                if (dividerCategory != null) dividerCategory.setVisibility(View.GONE);
                break;

            case MODE_ALL:
            default:
                // Hiện đầy đủ + setup sync scrolling
                tabLayoutFilter.setVisibility(View.VISIBLE);
                setupSyncScrolling();
                break;
        }
    }

    /**
     * Nút đóng dialog.
     */
    private void setupCloseButton() {
        btnCloseFilter.setOnClickListener(v -> dismiss());
    }

    /**
     * Load danh mục từ API và đổ vào RadioGroup rgCategoryFilter.
     */
    private void loadCategories() {
        ApiService apiService = RetrofitClient.getClient(requireContext()).create(ApiService.class);
        apiService.getCategories().enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(@NonNull Call<CategoryResponse> call, @NonNull Response<CategoryResponse> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> categories = response.body().getData();
                    if (categories != null) {
                        for (Category cat : categories) {
                            RadioButton rb = new RadioButton(requireContext());
                            rb.setId(View.generateViewId());
                            rb.setText(cat.getName());
                            rb.setTag(cat.getId());
                            rb.setTextSize(15);
                            rb.setTextColor(getResources().getColor(R.color.text_black, null));
                            rb.setPadding(8, 24, 8, 24);
                            rb.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                            rb.setButtonDrawable(null);
                            rb.setCompoundDrawablesWithIntrinsicBounds(
                                    0, 0, android.R.drawable.btn_radio, 0);

                            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(
                                    RadioGroup.LayoutParams.MATCH_PARENT,
                                    RadioGroup.LayoutParams.WRAP_CONTENT
                            );
                            rgCategoryFilter.addView(rb, params);
                        }
                        Log.d(TAG, "Đã load " + categories.size() + " danh mục");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<CategoryResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Lỗi load danh mục: " + t.getMessage());
            }
        });
    }

    /**
     * Lập trình Sync Scrolling giữa TabLayout và NestedScrollView.
     * Chỉ dùng cho MODE_ALL.
     */
    private void setupSyncScrolling() {

        // ---- 1. Cuộn nội dung → Nhảy Tab ----
        nsvFilter.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (isScrollingByTab) return;

            int categoryTop = sectionCategory.getTop();
            int discountTop = sectionDiscount.getTop();

            if (scrollY >= discountTop) {
                selectTabSilently(2);
            } else if (scrollY >= categoryTop) {
                selectTabSilently(1);
            } else {
                selectTabSilently(0);
            }
        });

        // ---- 2. Bấm Tab → Tự cuộn ----
        tabLayoutFilter.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                isScrollingByTab = true;

                int targetY = 0;
                switch (tab.getPosition()) {
                    case 0:
                        targetY = sectionSort.getTop();
                        break;
                    case 1:
                        targetY = sectionCategory.getTop();
                        break;
                    case 2:
                        targetY = sectionDiscount.getTop();
                        break;
                }

                nsvFilter.smoothScrollTo(0, targetY);
                nsvFilter.postDelayed(() -> isScrollingByTab = false, 500);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                onTabSelected(tab);
            }
        });
    }

    /**
     * Chọn tab mà không trigger onTabSelected (tránh vòng lặp).
     */
    private void selectTabSilently(int tabIndex) {
        TabLayout.Tab tab = tabLayoutFilter.getTabAt(tabIndex);
        if (tab != null && tabLayoutFilter.getSelectedTabPosition() != tabIndex) {
            boolean wasScrollingByTab = isScrollingByTab;
            isScrollingByTab = true;
            tab.select();
            nsvFilter.post(() -> isScrollingByTab = wasScrollingByTab);
        }
    }

    /**
     * Thiết lập nút Áp dụng và Đặt lại.
     */
    private void setupFooterButtons() {

        rgSortFilter.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != R.id.rbDefault) {
                btnReset.setVisibility(View.VISIBLE);
            }
        });

        rgCategoryFilter.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != R.id.rbAllCategory) {
                btnReset.setVisibility(View.VISIBLE);
            }
        });

        cbDiscounted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) btnReset.setVisibility(View.VISIBLE);
        });

        cbMostDiscount.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) btnReset.setVisibility(View.VISIBLE);
        });

        // Nút Đặt lại
        btnReset.setOnClickListener(v -> {
            rgSortFilter.check(R.id.rbDefault);
            rgCategoryFilter.check(R.id.rbAllCategory);
            cbDiscounted.setChecked(false);
            cbMostDiscount.setChecked(false);
            btnReset.setVisibility(View.GONE);

            if (filterListener != null) {
                filterListener.onFilterReset();
            }
        });

        // Nút Áp dụng
        btnApply.setOnClickListener(v -> {
            // 1. Thu thập dữ liệu Sort
            String sortBy = getSortType();

            // 2. Thu thập dữ liệu Category
            int categoryId = getSelectedCategoryId();
            String categoryName = getSelectedCategoryName();

            // 3. Thu thập dữ liệu Discount
            boolean isDiscount = cbDiscounted.isChecked();

            // Gọi callback (nếu có)
            if (filterListener != null) {
                filterListener.onFilterApplied(sortBy, categoryId, isDiscount, cbMostDiscount.isChecked());
            }

            // 4. Đóng gói Bundle và chuyển sang FilteredProductsFragment
            Bundle bundle = new Bundle();
            bundle.putString("sort_by", sortBy);
            bundle.putInt("category_id", categoryId);
            bundle.putString("category_name", categoryName);
            bundle.putString("is_discount", String.valueOf(isDiscount));

            dismiss();

            // Navigate sang màn kết quả lọc
            try {
                NavHostFragment.findNavController(FilterBottomSheetFragment.this)
                        .navigate(R.id.filteredProductsFragment, bundle);
            } catch (Exception e) {
                if (getParentFragment() != null) {
                    NavHostFragment.findNavController(getParentFragment())
                            .navigate(R.id.filteredProductsFragment, bundle);
                }
            }
        });
    }

    /**
     * Lấy loại sắp xếp đã chọn.
     */
    private String getSortType() {
        int checkedId = rgSortFilter.getCheckedRadioButtonId();
        if (checkedId == R.id.rbNewest) return "newest";
        if (checkedId == R.id.rbTopSales) return "top_sales";
        if (checkedId == R.id.rbPriceHigh) return "price_desc";
        if (checkedId == R.id.rbPriceLow) return "price_asc";
        return "default";
    }

    /**
     * Lấy ID danh mục đã chọn. Trả về 0 nếu chọn "Tất cả".
     */
    private int getSelectedCategoryId() {
        int checkedId = rgCategoryFilter.getCheckedRadioButtonId();
        if (checkedId == R.id.rbAllCategory) return 0;

        View checkedView = rgCategoryFilter.findViewById(checkedId);
        if (checkedView != null && checkedView.getTag() != null) {
            try {
                return Integer.parseInt(checkedView.getTag().toString());
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    /**
     * Lấy tên danh mục đã chọn. Trả về "" nếu chọn "Tất cả".
     */
    private String getSelectedCategoryName() {
        int checkedId = rgCategoryFilter.getCheckedRadioButtonId();
        if (checkedId == R.id.rbAllCategory) return "";

        View checkedView = rgCategoryFilter.findViewById(checkedId);
        if (checkedView instanceof RadioButton) {
            return ((RadioButton) checkedView).getText().toString();
        }
        return "";
    }
}
