package com.pixibeestudio.greenly.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.appcompat.app.AlertDialog;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.button.MaterialButton;
import com.pixibeestudio.greenly.R;
import com.pixibeestudio.greenly.data.model.Product;
import com.pixibeestudio.greenly.ui.adapter.ProductImageSliderAdapter;
import com.pixibeestudio.greenly.data.local.SessionManager;
import com.pixibeestudio.greenly.data.model.WishlistItem;
import com.pixibeestudio.greenly.ui.viewmodel.CartViewModel;
import com.pixibeestudio.greenly.ui.viewmodel.FavoriteViewModel;
import com.pixibeestudio.greenly.ui.viewmodel.ProductDetailViewModel;

import android.content.res.ColorStateList;
import android.graphics.Color;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductDetailFragment extends Fragment {

    private ImageButton btnBack;
    private LinearLayout searchBarDetail;
    private ImageButton btnShare;
    private ImageButton btnCart;
    private ViewPager2 vpProductImages;
    private TextView tvImageCounter;
    private TextView tvProductNameDetail;
    private TextView tvProductRatingDetail;
    private TextView tvProductOriginalPriceDetail;
    private TextView tvProductDiscountPriceDetail;
    private TextView tvProductUnitDetail;
    private ImageView btnDetailFavorite;
    private ImageButton btnDecreaseQty;
    private TextView tvQuantity;
    private ImageButton btnIncreaseQty;
    private MaterialButton btnAddToCart;
    
    // Phase 2 views
    private View cardSupplier;
    private TextView tvSupplierNameDetail;
    private TextView tvSupplierAddressDetail;
    private TextView tvSupplierCertDetail;
    
    private TextView tvDescriptionDetail;
    private TextView tvToggleDescriptionDetail;

    private int productId = -1;
    private int currentQuantity = 1;
    private ProductDetailViewModel viewModel;
    private CartViewModel cartViewModel;
    private FavoriteViewModel favoriteViewModel;
    private SessionManager sessionManager;
    private Product currentProduct;
    private boolean isFavorite = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Nhận dữ liệu từ arguments
        if (getArguments() != null) {
            productId = getArguments().getInt("productId", -1);
        }

        initViews(view);
        setupListeners();
        
        viewModel = new ViewModelProvider(this).get(ProductDetailViewModel.class);
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        favoriteViewModel = new ViewModelProvider(this).get(FavoriteViewModel.class);
        sessionManager = new SessionManager(requireContext());
        loadProductDetail();
        loadFavoriteStatus();
    }

    private void initViews(View view) {
        btnBack = view.findViewById(R.id.btnBack);
        searchBarDetail = view.findViewById(R.id.searchBarDetail);
        btnShare = view.findViewById(R.id.btnShare);
        btnCart = view.findViewById(R.id.btnCart);
        
        vpProductImages = view.findViewById(R.id.vpProductImages);
        tvImageCounter = view.findViewById(R.id.tvImageCounter);
        tvProductNameDetail = view.findViewById(R.id.tvProductNameDetail);
        tvProductRatingDetail = view.findViewById(R.id.tvProductRatingDetail);
        tvProductOriginalPriceDetail = view.findViewById(R.id.tvProductOriginalPriceDetail);
        tvProductDiscountPriceDetail = view.findViewById(R.id.tvProductDiscountPriceDetail);
        tvProductUnitDetail = view.findViewById(R.id.tvProductUnitDetail);
        btnDetailFavorite = view.findViewById(R.id.btnDetailFavorite);
        
        btnDecreaseQty = view.findViewById(R.id.btnDecreaseQty);
        tvQuantity = view.findViewById(R.id.tvQuantity);
        btnIncreaseQty = view.findViewById(R.id.btnIncreaseQty);
        btnAddToCart = view.findViewById(R.id.btnAddToCart);
        
        // Phase 2 views
        cardSupplier = view.findViewById(R.id.cardSupplier);
        tvSupplierNameDetail = view.findViewById(R.id.tvSupplierNameDetail);
        tvSupplierAddressDetail = view.findViewById(R.id.tvSupplierAddressDetail);
        tvSupplierCertDetail = view.findViewById(R.id.tvSupplierCertDetail);
        
        tvDescriptionDetail = view.findViewById(R.id.tvDescriptionDetail);
        tvToggleDescriptionDetail = view.findViewById(R.id.tvToggleDescriptionDetail);
        
        tvQuantity.setText(String.valueOf(currentQuantity));
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.popBackStack();
        });

        // Click vao o tim kiem gia -> navigate sang SearchFragment
        if (searchBarDetail != null) {
            searchBarDetail.setOnClickListener(v ->
                    Navigation.findNavController(v).navigate(R.id.searchFragment));
        }
        
        btnDecreaseQty.setOnClickListener(v -> {
            if (currentQuantity > 1) {
                currentQuantity--;
                tvQuantity.setText(String.valueOf(currentQuantity));
            }
        });
        
        btnIncreaseQty.setOnClickListener(v -> {
            currentQuantity++;
            tvQuantity.setText(String.valueOf(currentQuantity));
        });
        
        btnAddToCart.setOnClickListener(v -> {
            if (sessionManager.isGuestMode()) {
                showGuestLoginPopup();
            } else {
                if (currentProduct != null) {
                    cartViewModel.addToCart(currentProduct.getId(), currentQuantity);
                    Toast.makeText(getContext(), "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCart.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putBoolean("isFromDetail", true);
            Navigation.findNavController(v).navigate(R.id.action_productDetailFragment_to_cartFragment, bundle);
        });

        // Nút yêu thích trên màn chi tiết
        if (btnDetailFavorite != null) {
            btnDetailFavorite.setOnClickListener(v -> {
                if (sessionManager.isGuestMode()) {
                    showGuestLoginPopup();
                    return;
                }
                if (currentProduct == null) return;
                // Optimistic UI: đổi icon ngay lập tức
                isFavorite = !isFavorite;
                updateFavoriteIcon();
                // Gọi API toggle
                favoriteViewModel.toggleFavorite(currentProduct.getId());
            });
        }
    }
    
    private void loadProductDetail() {
        if (productId == -1) return;

        viewModel.getProductDetail(productId).observe(getViewLifecycleOwner(), product -> {
            if (product != null) {
                currentProduct = product;
                bindProductData(product);
            } else {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Không tải được dữ liệu sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    
    private void bindProductData(Product product) {
        // Set ảnh slider
        List<String> images = product.getAllImages();
        if (images == null || images.isEmpty()) {
            images = new ArrayList<>();
            images.add(product.getImage()); // Fallback dùng ảnh chính nếu allImages rỗng
        }
        ProductImageSliderAdapter imageAdapter = new ProductImageSliderAdapter(images);
        vpProductImages.setAdapter(imageAdapter);
        
        // Cài đặt bộ đếm ảnh
        if (images.size() > 1) {
            tvImageCounter.setVisibility(View.VISIBLE);
            tvImageCounter.setText("1/" + images.size());
            
            final int totalImages = images.size();
            vpProductImages.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    tvImageCounter.setText((position + 1) + "/" + totalImages);
                }
            });
        } else {
            tvImageCounter.setVisibility(View.GONE);
        }
        
        // Set tên và đơn vị
        tvProductNameDetail.setText(product.getName());
        tvProductUnitDetail.setText("/ " + product.getUnit());
        tvProductRatingDetail.setText("5.0"); // Hardcode rating
        
        // Logic hiển thị giá (tương tự như trong các adapter list)
        double originalPrice = product.getPrice();
        double discountPrice = product.getDiscountPrice();
        
        try {
            Locale vnLocale = new Locale.Builder().setLanguage("vi").setRegion("VN").build();
            NumberFormat format = NumberFormat.getCurrencyInstance(vnLocale);
            
            String formattedOriginalPrice = format.format(originalPrice);
            
            if (discountPrice > 0 && discountPrice < originalPrice) {
                // Có khuyến mãi
                String formattedDiscountPrice = format.format(discountPrice);
                
                tvProductOriginalPriceDetail.setVisibility(View.VISIBLE);
                tvProductOriginalPriceDetail.setPaintFlags(tvProductOriginalPriceDetail.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
                tvProductOriginalPriceDetail.setText(formattedOriginalPrice);
                
                tvProductDiscountPriceDetail.setText(formattedDiscountPrice);
            } else {
                // Không khuyến mãi
                tvProductOriginalPriceDetail.setVisibility(View.GONE);
                tvProductDiscountPriceDetail.setText(formattedOriginalPrice);
            }
        } catch (Exception e) {
            // Fallback nếu format lỗi
            if (discountPrice > 0 && discountPrice < originalPrice) {
                tvProductOriginalPriceDetail.setVisibility(View.VISIBLE);
                tvProductOriginalPriceDetail.setPaintFlags(tvProductOriginalPriceDetail.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
                tvProductOriginalPriceDetail.setText(originalPrice + "đ");
                tvProductDiscountPriceDetail.setText(discountPrice + "đ");
            } else {
                tvProductOriginalPriceDetail.setVisibility(View.GONE);
                tvProductDiscountPriceDetail.setText(originalPrice + "đ");
            }
        }

        // ================= PHASE 2 BINDING =================

        // 1. Nhà cung cấp
        if (product.getSupplier() != null) {
            cardSupplier.setVisibility(View.VISIBLE);
            tvSupplierNameDetail.setText(product.getSupplier().getName());
            tvSupplierAddressDetail.setText(product.getSupplier().getAddress());
            tvSupplierCertDetail.setText(product.getSupplier().getCertificate());
        } else {
            cardSupplier.setVisibility(View.GONE);
        }

        // 2. Mô tả sản phẩm
        if (product.getDescription() != null && !product.getDescription().isEmpty()) {
            tvDescriptionDetail.setText(product.getDescription());
        } else {
            tvDescriptionDetail.setText("Chưa có mô tả cho sản phẩm này.");
        }

        // 3. Logic Xem thêm / Thu gọn
        final boolean[] isExpanded = {false};
        tvToggleDescriptionDetail.setOnClickListener(v -> {
            if (isExpanded[0]) {
                tvDescriptionDetail.setMaxLines(3);
                tvToggleDescriptionDetail.setText("Xem thêm >");
            } else {
                tvDescriptionDetail.setMaxLines(Integer.MAX_VALUE);
                tvToggleDescriptionDetail.setText("Thu gọn ^");
            }
            isExpanded[0] = !isExpanded[0];
        });
    }

    /**
     * Lấy trạng thái yêu thích của sản phẩm hiện tại từ API.
     */
    private void loadFavoriteStatus() {
        if (sessionManager.isGuestMode() || productId == -1) return;

        favoriteViewModel.getFavorites().observe(getViewLifecycleOwner(), wishlistItems -> {
            isFavorite = false;
            if (wishlistItems != null) {
                for (WishlistItem item : wishlistItems) {
                    if (item.getProductId() == productId) {
                        isFavorite = true;
                        break;
                    }
                }
            }
            updateFavoriteIcon();
        });
    }

    /**
     * Cập nhật icon trái tim dựa trên trạng thái isFavorite.
     */
    private void updateFavoriteIcon() {
        if (btnDetailFavorite == null) return;
        if (isFavorite) {
            btnDetailFavorite.setImageResource(R.drawable.ic_favorite_red);
            btnDetailFavorite.setImageTintList(ColorStateList.valueOf(Color.parseColor("#E53935")));
        } else {
            btnDetailFavorite.setImageResource(R.drawable.ic_favorite_border);
            btnDetailFavorite.setImageTintList(ColorStateList.valueOf(Color.parseColor("#9E9E9E")));
        }
    }

    /**
     * Hiển thị popup yêu cầu đăng nhập cho Guest.
     */
    private void showGuestLoginPopup() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Bạn chưa đăng nhập")
                .setMessage("Vui lòng đăng nhập để mua hàng")
                .setPositiveButton("Đăng nhập", (dialog, which) -> {
                    Navigation.findNavController(requireView()).navigate(R.id.action_productDetailFragment_to_loginFragment);
                })
                .setNegativeButton("Ở lại", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setCancelable(false)
                .show();
    }
}
