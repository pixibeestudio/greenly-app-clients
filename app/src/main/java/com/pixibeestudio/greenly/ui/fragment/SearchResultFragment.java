package com.pixibeestudio.greenly.ui.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import com.pixibeestudio.greenly.data.local.SearchHistoryManager;
import com.pixibeestudio.greenly.data.local.SessionManager;
import com.pixibeestudio.greenly.data.model.Product;
import com.pixibeestudio.greenly.data.model.WishlistItem;
import com.pixibeestudio.greenly.ui.adapter.ProductGridAdapter;
import com.pixibeestudio.greenly.ui.viewmodel.CartViewModel;
import com.pixibeestudio.greenly.ui.viewmodel.FavoriteViewModel;
import com.pixibeestudio.greenly.ui.viewmodel.SearchViewModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchResultFragment extends Fragment
        implements ProductGridAdapter.OnProductAddCartListener,
                   ProductGridAdapter.OnFavoriteToggleListener {

    private ImageButton btnBack;
    private EditText edtSearch;
    private RecyclerView rvSearchResults;
    private LinearLayout layoutEmptySearch;
    private TextView tvFilterAll, tvFilterPrice, tvFilterNewest, tvFilterTopSales;
    private String searchQuery = "";
    private String currentSort = "all";

    private SearchViewModel viewModel;
    private FavoriteViewModel favoriteViewModel;
    private CartViewModel cartViewModel;
    private ProductGridAdapter adapter;
    private SearchHistoryManager searchHistoryManager;
    private SessionManager sessionManager;
    private final Set<Integer> favoriteProductIds = new HashSet<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khoi tao ViewModel va Manager
        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        favoriteViewModel = new ViewModelProvider(this).get(FavoriteViewModel.class);
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        searchHistoryManager = new SearchHistoryManager(requireContext());
        sessionManager = new SessionManager(requireContext());

        // Anh xa views
        btnBack = view.findViewById(R.id.btnBack);
        edtSearch = view.findViewById(R.id.edtSearch);
        rvSearchResults = view.findViewById(R.id.rvSearchResults);
        layoutEmptySearch = view.findViewById(R.id.layoutEmptySearch);
        tvFilterAll = view.findViewById(R.id.tvFilterAll);
        tvFilterPrice = view.findViewById(R.id.tvFilterPrice);
        tvFilterNewest = view.findViewById(R.id.tvFilterNewest);
        tvFilterTopSales = view.findViewById(R.id.tvFilterTopSales);

        // Cau hinh RecyclerView Grid 2 cot
        rvSearchResults.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        // Lay argument tu Bundle va hien len EditText
        if (getArguments() != null) {
            searchQuery = getArguments().getString("searchQuery", "");
            edtSearch.setText(searchQuery);
        }

        // Nut Back -> lui ve SearchFragment
        btnBack.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);
            navController.popBackStack();
        });

        // Thiết lập sự kiện click cho các nút lọc
        setupFilterListeners();

        // Thiết lập click cho icon kính lúp (drawableEnd) trong EditText
        setupSearchIconClick();

        // Bat su kien tim kiem lai tu EditText
        edtSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = edtSearch.getText().toString().trim();
                if (!query.isEmpty()) {
                    searchQuery = query;
                    searchHistoryManager.addSearchQuery(query);
                    performSearch(query);

                    // An ban phim
                    InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(edtSearch.getWindowToken(), 0);
                    }
                }
                return true;
            }
            return false;
        });

        // Load danh sach yeu thich de hien trang thai icon tim
        loadFavorites();

        // Goi API tim kiem voi tu khoa ban dau
        if (!searchQuery.isEmpty()) {
            performSearch(searchQuery);
        }
    }

    /**
     * Load danh sach yeu thich tu API de dong bo trang thai icon tim.
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
            // Refresh adapter voi favoriteIds moi
            if (adapter != null) {
                adapter.setFavoriteIds(favoriteProductIds);
            }
        });
    }

    /**
     * Thiết lập click cho các nút lọc: Tất cả, Giá, Mới nhất, Bán chạy.
     */
    private void setupFilterListeners() {
        View.OnClickListener filterClick = v -> {
            // Reset trạng thái tất cả nút
            resetFilterButtons();

            // Active nút được chọn
            TextView selected = (TextView) v;
            selected.setBackgroundResource(R.drawable.bg_filter_active);
            selected.setTextColor(getResources().getColor(android.R.color.white, null));

            // Xác định loại sort
            int id = v.getId();
            if (id == R.id.tvFilterAll) {
                currentSort = "all";
            } else if (id == R.id.tvFilterPrice) {
                currentSort = "price_asc";
            } else if (id == R.id.tvFilterNewest) {
                currentSort = "newest";
            } else if (id == R.id.tvFilterTopSales) {
                currentSort = "top_sales";
            }

            // Tìm kiếm lại với sort mới
            if (!searchQuery.isEmpty()) {
                performSearch(searchQuery);
            }
        };

        tvFilterAll.setOnClickListener(filterClick);
        tvFilterPrice.setOnClickListener(filterClick);
        tvFilterNewest.setOnClickListener(filterClick);
        tvFilterTopSales.setOnClickListener(filterClick);
    }

    /**
     * Reset trạng thái các nút lọc về inactive.
     */
    private void resetFilterButtons() {
        tvFilterAll.setBackgroundResource(R.drawable.bg_filter_inactive);
        tvFilterAll.setTextColor(getResources().getColor(android.R.color.black, null));
        tvFilterPrice.setBackgroundResource(R.drawable.bg_filter_inactive);
        tvFilterPrice.setTextColor(getResources().getColor(android.R.color.black, null));
        tvFilterNewest.setBackgroundResource(R.drawable.bg_filter_inactive);
        tvFilterNewest.setTextColor(getResources().getColor(android.R.color.black, null));
        tvFilterTopSales.setBackgroundResource(R.drawable.bg_filter_inactive);
        tvFilterTopSales.setTextColor(getResources().getColor(android.R.color.black, null));
    }

    /**
     * Thiết lập click cho icon kính lúp (drawableEnd) trong EditText.
     */
    private void setupSearchIconClick() {
        edtSearch.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // Kiểm tra click vào vùng drawableEnd
                if (edtSearch.getCompoundDrawables()[2] != null) {
                    int drawableWidth = edtSearch.getCompoundDrawables()[2].getBounds().width();
                    if (event.getRawX() >= (edtSearch.getRight() - drawableWidth - edtSearch.getPaddingEnd())) {
                        // Thực hiện tìm kiếm
                        String query = edtSearch.getText().toString().trim();
                        if (!query.isEmpty()) {
                            searchQuery = query;
                            searchHistoryManager.addSearchQuery(query);
                            performSearch(query);
                            // Ẩn bàn phím
                            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            if (imm != null) imm.hideSoftInputFromWindow(edtSearch.getWindowToken(), 0);
                        }
                        return true;
                    }
                }
            }
            return false;
        });
    }

    /**
     * Goi API tim kiem va observe ket qua
     */
    private void performSearch(String keyword) {
        viewModel.searchProducts(keyword, currentSort);
        viewModel.getSearchResultsLiveData().observe(getViewLifecycleOwner(), products -> {
            if (products != null && !products.isEmpty()) {
                // Co ket qua -> cap nhat adapter
                rvSearchResults.setVisibility(View.VISIBLE);
                layoutEmptySearch.setVisibility(View.GONE);
                adapter = new ProductGridAdapter(products, this);
                adapter.setFavoriteListener(this);
                adapter.setFavoriteIds(favoriteProductIds);
                rvSearchResults.setAdapter(adapter);
            } else {
                // Khong co ket qua -> hien empty state
                rvSearchResults.setVisibility(View.GONE);
                layoutEmptySearch.setVisibility(View.VISIBLE);
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
     * Hien popup yeu cau dang nhap cho Guest.
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
