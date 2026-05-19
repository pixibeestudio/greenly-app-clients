package com.pixibeestudio.greenly.ui.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.pixibeestudio.greenly.R;
import com.pixibeestudio.greenly.data.local.SessionManager;
import com.pixibeestudio.greenly.ui.activity.MainActivity;
import com.pixibeestudio.greenly.utils.NetworkUtils;
import com.pixibeestudio.greenly.data.model.Cart;
import com.pixibeestudio.greenly.ui.adapter.CartAdapter;
import com.pixibeestudio.greenly.ui.viewmodel.CartViewModel;

import java.text.NumberFormat;
import java.util.Locale;

public class CartFragment extends Fragment {

    private SessionManager sessionManager;
    private CartViewModel cartViewModel;
    private CartAdapter cartAdapter;
    
    private double currentSubtotal = 0;

    // Views
    private ImageButton btnBackCart, btnClearCart;
    private TextView tvCartTitle, tvSubtotalCart, tvGrandTotalCart, tvShippingCart;
    private RecyclerView rvCartItems;
    private MaterialButton btnCheckoutCart, btnEmptyCartAction, btnContinueShopping;
    private NestedScrollView layoutCartContent;
    private ConstraintLayout layoutCartBottom;
    private LinearLayout layoutCartEmpty;
    private TextView tvEmptyCartMessage;
    private SwipeRefreshLayout swipeRefreshCart;
    private View layoutNoConnection;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);

        initViews(view);
        setupRecyclerView();
        checkArgumentsAndSetupBack();

        // Ánh xạ layout mất kết nối
        layoutNoConnection = view.findViewById(R.id.layoutNoConnection);
        if (layoutNoConnection != null) {
            View btnRetry = layoutNoConnection.findViewById(R.id.btnRetry);
            if (btnRetry != null) {
                btnRetry.setOnClickListener(v -> checkAndLoadCart());
            }
        }

        if (sessionManager.isGuestMode()) {
            showEmptyState("Bạn chưa đăng nhập", true);
        } else {
            setupClickListeners(view);
            setupSwipeRefresh();
            observeViewModel();
            checkAndLoadCart();
        }
    }

    /**
     * Kiểm tra mạng và tải giỏ hàng.
     */
    private void checkAndLoadCart() {
        if (!NetworkUtils.isNetworkAvailable(getContext())) {
            if (layoutNoConnection != null) layoutNoConnection.setVisibility(View.VISIBLE);
            return;
        }
        if (layoutNoConnection != null) layoutNoConnection.setVisibility(View.GONE);
        loadCartData();
    }

    private void initViews(View view) {
        btnBackCart = view.findViewById(R.id.btnBackCart);
        btnClearCart = view.findViewById(R.id.btnClearCart);
        tvCartTitle = view.findViewById(R.id.tvCartTitle);
        tvSubtotalCart = view.findViewById(R.id.tvSubtotalCart);
        tvGrandTotalCart = view.findViewById(R.id.tvGrandTotalCart);
        tvShippingCart = view.findViewById(R.id.tvShippingCart);
        rvCartItems = view.findViewById(R.id.rvCartItems);
        btnCheckoutCart = view.findViewById(R.id.btnCheckoutCart);
        btnEmptyCartAction = view.findViewById(R.id.btnEmptyCartAction);
        
        layoutCartContent = view.findViewById(R.id.layoutCartContent);
        layoutCartBottom = view.findViewById(R.id.layoutCartBottom);
        layoutCartEmpty = view.findViewById(R.id.layoutCartEmpty);
        tvEmptyCartMessage = view.findViewById(R.id.tvEmptyCartMessage);
        swipeRefreshCart = view.findViewById(R.id.swipeRefreshCart);
        btnContinueShopping = view.findViewById(R.id.btnContinueShopping);
    }

    private void setupRecyclerView() {
        cartAdapter = new CartAdapter(new CartAdapter.OnCartItemListener() {
            @Override
            public void onIncrease(Cart cart) {
                // TH01: Kiểm tra số lượng tồn kho trước khi tăng
                int stock = 0;
                if (cart.getProduct() != null) {
                    stock = cart.getProduct().getStockQuantity();
                }
                if (stock > 0 && cart.getQuantity() >= stock) {
                    // Đã đạt giới hạn tồn kho
                    new AlertDialog.Builder(requireContext())
                            .setTitle("Không thể thêm")
                            .setMessage("Số lượng trong kho chỉ còn " + stock + " sản phẩm. Bạn đã đạt giới hạn số lượng có thể mua.")
                            .setPositiveButton("Đã hiểu", null)
                            .show();
                    return;
                }
                cartViewModel.updateCart(cart.getId(), cart.getQuantity() + 1).observe(getViewLifecycleOwner(), success -> {
                    if (success) loadCartData();
                });
            }

            @Override
            public void onDecrease(Cart cart) {
                if (cart.getQuantity() > 1) {
                    cartViewModel.updateCart(cart.getId(), cart.getQuantity() - 1).observe(getViewLifecycleOwner(), success -> {
                        if (success) loadCartData();
                    });
                } else {
                    // TH02: Số lượng = 1, hỏi xác nhận xóa sản phẩm
                    String productName = cart.getProduct() != null ? cart.getProduct().getName() : "sản phẩm này";
                    new AlertDialog.Builder(requireContext())
                            .setTitle("Xóa sản phẩm")
                            .setMessage("Bạn có muốn xóa \"" + productName + "\" khỏi giỏ hàng không?")
                            .setPositiveButton("Có", (dialog, which) -> {
                                cartViewModel.deleteCartItem(cart.getId()).observe(getViewLifecycleOwner(), success -> {
                                    if (success) {
                                        Toast.makeText(requireContext(), "Đã xóa sản phẩm", Toast.LENGTH_SHORT).show();
                                        loadCartData();
                                    }
                                });
                            })
                            .setNegativeButton("Không", null)
                            .show();
                }
            }

            @Override
            public void onDelete(Cart cart) {
                cartViewModel.deleteCartItem(cart.getId()).observe(getViewLifecycleOwner(), success -> {
                    if (success) {
                        Toast.makeText(requireContext(), "Đã xoá", Toast.LENGTH_SHORT).show();
                        loadCartData();
                    }
                });
            }
        });
        rvCartItems.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvCartItems.setAdapter(cartAdapter);
    }

    private void checkArgumentsAndSetupBack() {
        boolean shouldShowBack = false;

        // Kiểm tra argument isFromDetail (từ ProductDetailFragment) hoặc isFromFilter (từ FilteredProductsFragment)
        if (getArguments() != null) {
            if (getArguments().getBoolean("isFromDetail", false)
                    || getArguments().getBoolean("isFromFilter", false)) {
                shouldShowBack = true;
            }
        }

        // Kiểm tra BackStack: nếu màn trước là FavoriteFragment hoặc FilteredProductsFragment thì hiện nút Back
        NavController navController = Navigation.findNavController(requireView());
        if (navController.getPreviousBackStackEntry() != null) {
            int prevId = navController.getPreviousBackStackEntry().getDestination().getId();
            if (prevId == R.id.favoriteFragment || prevId == R.id.filteredProductsFragment) {
                shouldShowBack = true;
            }
        }

        if (shouldShowBack) {
            btnBackCart.setVisibility(View.VISIBLE);
            btnBackCart.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());
        } else {
            btnBackCart.setVisibility(View.GONE);
        }
    }

    private void setupClickListeners(View view) {
        btnClearCart.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Xoá giỏ hàng")
                    .setMessage("Bạn chắc chắn muốn xoá tất cả sản phẩm?")
                    .setPositiveButton("Xoá hết", (dialog, which) -> {
                        cartViewModel.clearCart().observe(getViewLifecycleOwner(), success -> {
                            if (success) loadCartData();
                        });
                    })
                    .setNegativeButton("Huỷ", null)
                    .show();
        });

        btnCheckoutCart.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putDouble("subtotal", currentSubtotal);
            Navigation.findNavController(v).navigate(R.id.action_cartFragment_to_checkoutFragment, bundle);
        });

        // Nút "Tiếp tục mua sắm" → quay về trang chủ
        btnContinueShopping.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_cartFragment_to_homeFragment);
        });
    }

    private void setupSwipeRefresh() {
        if (swipeRefreshCart == null) return;
        swipeRefreshCart.setColorSchemeResources(R.color.header_bg_green);
        swipeRefreshCart.setOnRefreshListener(() -> {
            loadCartData();
            // Tắt loading sau 1s
            swipeRefreshCart.postDelayed(() -> {
                if (swipeRefreshCart != null) swipeRefreshCart.setRefreshing(false);
            }, 1000);
        });
    }

    private void loadCartData() {
        cartViewModel.getCarts().observe(getViewLifecycleOwner(), carts -> {
            if (carts != null && !carts.isEmpty()) {
                // Có hàng
                showContentState();
                cartAdapter.setCartList(carts);
                tvCartTitle.setText("Giỏ hàng (" + carts.size() + ")");
                cartViewModel.calculateTotals(carts);
            } else {
                // Trống
                showEmptyState("Giỏ hàng của bạn đang trống", false);
                tvCartTitle.setText("Giỏ hàng (0)");
            }
            // Cập nhật cart badge trên bottom nav
            if (requireActivity() instanceof MainActivity) {
                ((MainActivity) requireActivity()).loadCartBadge();
            }
        });
    }

    private void observeViewModel() {
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"));
        
        cartViewModel.getSubtotalLiveData().observe(getViewLifecycleOwner(), subtotal -> {
            currentSubtotal = subtotal;
            tvSubtotalCart.setText(format.format(subtotal));
        });

        cartViewModel.getGrandTotalLiveData().observe(getViewLifecycleOwner(), grandTotal -> {
            tvGrandTotalCart.setText(format.format(grandTotal));
        });
    }

    private void showContentState() {
        layoutCartContent.setVisibility(View.VISIBLE);
        layoutCartBottom.setVisibility(View.VISIBLE);
        btnClearCart.setVisibility(View.VISIBLE);
        btnContinueShopping.setVisibility(View.VISIBLE);
        layoutCartEmpty.setVisibility(View.GONE);
    }

    private void showEmptyState(String message, boolean isGuest) {
        layoutCartContent.setVisibility(View.GONE);
        layoutCartBottom.setVisibility(View.GONE);
        btnClearCart.setVisibility(View.GONE);
        btnContinueShopping.setVisibility(View.GONE);
        layoutCartEmpty.setVisibility(View.VISIBLE);
        
        tvEmptyCartMessage.setText(message);
        
        if (isGuest) {
            btnEmptyCartAction.setText("Đăng nhập ngay");
            btnEmptyCartAction.setOnClickListener(v -> {
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_cartFragment_to_loginFragment); // Yêu cầu có action này trong nav_main
            });
        } else {
            btnEmptyCartAction.setText("Đi mua sắm");
            btnEmptyCartAction.setOnClickListener(v -> {
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_cartFragment_to_homeFragment); // Quay về home
            });
        }
    }
}
