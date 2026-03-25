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
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.button.MaterialButton;
import com.pixibeestudio.greenly.R;
import com.pixibeestudio.greenly.data.model.Product;
import com.pixibeestudio.greenly.ui.adapter.ProductImageSliderAdapter;
import com.pixibeestudio.greenly.ui.viewmodel.ProductDetailViewModel;

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

    private int productId = -1;
    private int currentQuantity = 1;
    private ProductDetailViewModel viewModel;

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
        loadProductDetail();
    }

    private void initViews(View view) {
        btnBack = view.findViewById(R.id.btnBack);
        searchBarDetail = view.findViewById(R.id.searchBarDetail);
        btnShare = view.findViewById(R.id.btnShare);
        btnCart = view.findViewById(R.id.btnCart);
        
        vpProductImages = view.findViewById(R.id.vpProductImages);
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
        
        tvQuantity.setText(String.valueOf(currentQuantity));
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.popBackStack();
        });
        
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
            if (getContext() != null) {
                Toast.makeText(getContext(), "Đã thêm " + currentQuantity + " sản phẩm vào giỏ", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void loadProductDetail() {
        if (productId == -1) return;
        
        viewModel.getProductDetail(productId).observe(getViewLifecycleOwner(), product -> {
            if (product != null) {
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
    }
}
