package com.pixibeestudio.greenly.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pixibeestudio.greenly.R;
import com.pixibeestudio.greenly.data.model.Product;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import android.os.Bundle;

/**
 * Adapter hiển thị sản phẩm dạng cuộn ngang.
 * Item cuối cùng (position == 10) sẽ hiển thị "Xem thêm".
 */
public class ProductHorizontalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Hai loại item: sản phẩm bình thường và nút "Xem thêm"
    private static final int TYPE_PRODUCT = 0;
    private static final int TYPE_VIEW_MORE = 1;

    private final List<Product> products;

    public ProductHorizontalAdapter(List<Product> products) {
        this.products = products;
    }

    @Override
    public int getItemViewType(int position) {
        // Nếu danh sách lớn hơn 10 thì item cuối (index 10) là nút "Xem thêm"
        if (products != null && products.size() > 10 && position == 10) {
            return TYPE_VIEW_MORE;
        }
        // Nếu danh sách <= 10 nhưng position bằng size (item cuối) thì cũng là "Xem thêm"
        if (products != null && products.size() <= 10 && position == products.size()) {
             return TYPE_VIEW_MORE;
        }
        return TYPE_PRODUCT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_VIEW_MORE) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_view_more, parent, false);
            return new ViewMoreViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_product_horizontal, parent, false);
            return new ProductViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ProductViewHolder && position < products.size()) {
            ProductViewHolder productHolder = (ProductViewHolder) holder;
            Product product = products.get(position);

            // Bind data
            productHolder.tvProductName.setText(product.getName());
            productHolder.tvProductOrigin.setText(product.getOrigin());
            
            // Hiện tại chưa có rating thật, hardcode 5.0
            productHolder.tvProductRating.setText("5.0");
            
            productHolder.tvProductUnit.setText("/ " + product.getUnit());

            // Xử lý trạng thái Bán hết (Stock <= 0)
            if (product.getStockQuantity() <= 0) {
                // TRẠNG THÁI BÁN HẾT
                productHolder.layoutSoldOut.setVisibility(View.VISIBLE);
                
                // Đổi ảnh thành đen trắng (Grayscale)
                android.graphics.ColorMatrix matrix = new android.graphics.ColorMatrix();
                matrix.setSaturation(0); // 0 = đen trắng
                productHolder.ivProductImage.setColorFilter(new android.graphics.ColorMatrixColorFilter(matrix));
                
                // Vô hiệu hóa nút Add Cart và làm mờ
                productHolder.btnAddCart.setEnabled(false);
                productHolder.btnAddCart.setAlpha(0.5f);
            } else {
                // CÒN HÀNG
                productHolder.layoutSoldOut.setVisibility(View.GONE);
                
                // Xóa bộ lọc màu, trả lại ảnh gốc
                productHolder.ivProductImage.clearColorFilter();
                
                // Kích hoạt lại nút Add Cart
                productHolder.btnAddCart.setEnabled(true);
                productHolder.btnAddCart.setAlpha(1.0f);
            }

            // Xử lý giá tiền và khuyến mãi
            double originalPrice = product.getPrice();
            double discountPrice = product.getDiscountPrice();

            try {
                Locale vnLocale = new Locale.Builder().setLanguage("vi").setRegion("VN").build();
                NumberFormat format = NumberFormat.getCurrencyInstance(vnLocale);
                
                String formattedOriginalPrice = format.format(originalPrice);

                if (discountPrice > 0 && discountPrice < originalPrice) {
                    // TRẠNG THÁI CÓ KHUYẾN MÃI
                    String formattedDiscountPrice = format.format(discountPrice);
                    
                    productHolder.tvProductOriginalPrice.setVisibility(View.VISIBLE);
                    productHolder.tvDiscountBadge.setVisibility(View.VISIBLE);

                    // Gạch ngang giá gốc
                    productHolder.tvProductOriginalPrice.setPaintFlags(productHolder.tvProductOriginalPrice.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
                    productHolder.tvProductOriginalPrice.setText(formattedOriginalPrice);

                    // Set giá khuyến mãi
                    productHolder.tvProductDiscountPrice.setText(formattedDiscountPrice);

                    // Tính toán % giảm
                    int percent = (int) Math.round(((originalPrice - discountPrice) / originalPrice) * 100);
                    productHolder.tvDiscountBadge.setText("-" + percent + "%");
                } else {
                    // TRẠNG THÁI BÌNH THƯỜNG
                    productHolder.tvProductOriginalPrice.setVisibility(View.GONE);
                    productHolder.tvDiscountBadge.setVisibility(View.GONE);
                    productHolder.tvProductDiscountPrice.setText(formattedOriginalPrice);
                }
            } catch (Exception e) {
                // Fallback nếu lỗi format
                if (discountPrice > 0 && discountPrice < originalPrice) {
                    productHolder.tvProductOriginalPrice.setVisibility(View.VISIBLE);
                    productHolder.tvDiscountBadge.setVisibility(View.VISIBLE);
                    productHolder.tvProductOriginalPrice.setPaintFlags(productHolder.tvProductOriginalPrice.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
                    productHolder.tvProductOriginalPrice.setText(originalPrice + "đ");
                    productHolder.tvProductDiscountPrice.setText(discountPrice + "đ");
                    
                    int percent = (int) Math.round(((originalPrice - discountPrice) / originalPrice) * 100);
                    productHolder.tvDiscountBadge.setText("-" + percent + "%");
                } else {
                    productHolder.tvProductOriginalPrice.setVisibility(View.GONE);
                    productHolder.tvDiscountBadge.setVisibility(View.GONE);
                    productHolder.tvProductDiscountPrice.setText(originalPrice + "đ");
                }
            }

            // Load hình ảnh bằng Glide
            Glide.with(productHolder.itemView.getContext())
                    .load(product.getImage())
                    .placeholder(R.drawable.img_placeholder)
                    .error(R.drawable.img_placeholder)
                    .centerCrop()
                    .into(productHolder.ivProductImage);

            // Sự kiện click vào toàn bộ item sản phẩm
            productHolder.itemView.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putInt("productId", product.getId());
                Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_productDetailFragment, bundle);
            });
        }
    }

    @Override
    public int getItemCount() {
        if (products == null || products.isEmpty()) {
            return 0;
        }
        // Thêm 1 item cho nút "Xem thêm", tối đa hiển thị 10 sản phẩm + 1 nút
        return Math.min(products.size(), 10) + 1;
    }

    // ViewHolder cho sản phẩm
    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        android.widget.FrameLayout layoutSoldOut;
        ImageView ivFavorite;
        TextView tvDiscountBadge;
        TextView tvProductName;
        TextView tvProductOrigin;
        TextView tvProductRating;
        TextView tvProductOriginalPrice;
        TextView tvProductDiscountPrice;
        TextView tvProductUnit;
        ImageView btnAddCart;

        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            layoutSoldOut = itemView.findViewById(R.id.layoutSoldOut);
            ivFavorite = itemView.findViewById(R.id.ivFavorite);
            tvDiscountBadge = itemView.findViewById(R.id.tvDiscountBadge);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductOrigin = itemView.findViewById(R.id.tvProductOrigin);
            tvProductRating = itemView.findViewById(R.id.tvProductRating);
            tvProductOriginalPrice = itemView.findViewById(R.id.tvProductOriginalPrice);
            tvProductDiscountPrice = itemView.findViewById(R.id.tvProductDiscountPrice);
            tvProductUnit = itemView.findViewById(R.id.tvProductUnit);
            btnAddCart = itemView.findViewById(R.id.btnAddCart);
        }
    }

    // ViewHolder cho nút "Xem thêm"
    static class ViewMoreViewHolder extends RecyclerView.ViewHolder {
        ViewMoreViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
