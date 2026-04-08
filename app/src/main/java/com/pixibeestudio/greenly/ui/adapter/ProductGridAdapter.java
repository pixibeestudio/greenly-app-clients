package com.pixibeestudio.greenly.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pixibeestudio.greenly.R;
import com.pixibeestudio.greenly.data.model.Product;

import java.text.NumberFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import android.os.Bundle;

/**
 * Adapter hiển thị sản phẩm dạng lưới (Grid 2 cột).
 * Dùng cho phần "Tất cả sản phẩm" ở cuối trang Home.
 */
public class ProductGridAdapter extends RecyclerView.Adapter<ProductGridAdapter.GridViewHolder> {

    private final List<Product> products;
    private final OnProductAddCartListener listener;
    private OnFavoriteToggleListener favoriteListener;
    private Set<Integer> favoriteIds = new HashSet<>();

    // Interface cho sự kiện thêm vào giỏ hàng
    public interface OnProductAddCartListener {
        void onAddCartClick(Product product);
    }

    // Interface cho sự kiện toggle yêu thích
    public interface OnFavoriteToggleListener {
        void onFavoriteToggle(Product product, boolean isNowFavorite);
    }

    public ProductGridAdapter(List<Product> products, OnProductAddCartListener listener) {
        this.products = products;
        this.listener = listener;
    }

    public void setFavoriteListener(OnFavoriteToggleListener listener) {
        this.favoriteListener = listener;
    }

    /**
     * Cập nhật danh sách ID sản phẩm yêu thích và refresh UI.
     */
    public void setFavoriteIds(Set<Integer> ids) {
        this.favoriteIds = ids != null ? ids : new HashSet<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_grid, parent, false);
        return new GridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GridViewHolder holder, int position) {
        Product product = products.get(position);

        // Bind data
        holder.tvProductName.setText(product.getName());
        holder.tvProductOrigin.setText(product.getOrigin());
        
        // Hiện tại chưa có rating thật, hardcode 5.0
        holder.tvProductRating.setText("5.0");
        
        holder.tvProductUnit.setText("/ " + product.getUnit());

        // Xử lý trạng thái Bán hết (Stock <= 0)
        if (product.getStockQuantity() <= 0) {
            // TRẠNG THÁI BÁN HẾT
            holder.layoutSoldOut.setVisibility(View.VISIBLE);
            
            // Đổi ảnh thành đen trắng (Grayscale)
            android.graphics.ColorMatrix matrix = new android.graphics.ColorMatrix();
            matrix.setSaturation(0); // 0 = đen trắng
            holder.ivProductImage.setColorFilter(new android.graphics.ColorMatrixColorFilter(matrix));
            
            // Vô hiệu hóa nút Add Cart và làm mờ
            holder.btnAddCart.setEnabled(false);
            holder.btnAddCart.setAlpha(0.5f);
        } else {
            // CÒN HÀNG
            holder.layoutSoldOut.setVisibility(View.GONE);
            
            // Xóa bộ lọc màu, trả lại ảnh gốc
            holder.ivProductImage.clearColorFilter();
            
            // Kích hoạt lại nút Add Cart
            holder.btnAddCart.setEnabled(true);
            holder.btnAddCart.setAlpha(1.0f);
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
                
                holder.tvProductOriginalPrice.setVisibility(View.VISIBLE);
                holder.tvDiscountBadge.setVisibility(View.VISIBLE);

                // Gạch ngang giá gốc
                holder.tvProductOriginalPrice.setPaintFlags(holder.tvProductOriginalPrice.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
                holder.tvProductOriginalPrice.setText(formattedOriginalPrice);

                // Set giá khuyến mãi
                holder.tvProductDiscountPrice.setText(formattedDiscountPrice);

                // Tính toán % giảm
                int percent = (int) Math.round(((originalPrice - discountPrice) / originalPrice) * 100);
                holder.tvDiscountBadge.setText("-" + percent + "%");
            } else {
                // TRẠNG THÁI BÌNH THƯỜNG
                holder.tvProductOriginalPrice.setVisibility(View.GONE);
                holder.tvDiscountBadge.setVisibility(View.GONE);
                holder.tvProductDiscountPrice.setText(formattedOriginalPrice);
            }
        } catch (Exception e) {
            // Fallback nếu lỗi format
            if (discountPrice > 0 && discountPrice < originalPrice) {
                holder.tvProductOriginalPrice.setVisibility(View.VISIBLE);
                holder.tvDiscountBadge.setVisibility(View.VISIBLE);
                holder.tvProductOriginalPrice.setPaintFlags(holder.tvProductOriginalPrice.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
                holder.tvProductOriginalPrice.setText(originalPrice + "đ");
                holder.tvProductDiscountPrice.setText(discountPrice + "đ");
                
                int percent = (int) Math.round(((originalPrice - discountPrice) / originalPrice) * 100);
                holder.tvDiscountBadge.setText("-" + percent + "%");
            } else {
                holder.tvProductOriginalPrice.setVisibility(View.GONE);
                holder.tvDiscountBadge.setVisibility(View.GONE);
                holder.tvProductDiscountPrice.setText(originalPrice + "đ");
            }
        }

        // Load hình ảnh bằng Glide
        Glide.with(holder.itemView.getContext())
                .load(product.getImage())
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_placeholder)
                .centerCrop()
                .into(holder.ivProductImage);

        // Sự kiện click vào toàn bộ item sản phẩm
        holder.itemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt("productId", product.getId());
            Navigation.findNavController(v).navigate(R.id.action_global_to_productDetailFragment, bundle);
        });

        // Sự kiện click nút thêm vào giỏ (riêng biệt, không bị đè bởi itemView)
        holder.btnAddCart.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAddCartClick(product);
            }
        });

        // Hiển thị trạng thái yêu thích và bắt sự kiện toggle
        boolean isFav = favoriteIds.contains(product.getId());
        holder.ivFavorite.setImageResource(isFav ? R.drawable.ic_favorite_red : R.drawable.ic_favorite_border);

        holder.ivFavorite.setOnClickListener(v -> {
            boolean currentlyFav = favoriteIds.contains(product.getId());
            boolean isNowFav = !currentlyFav;
            // Optimistic UI: đổi icon ngay lập tức
            if (isNowFav) {
                favoriteIds.add(product.getId());
            } else {
                favoriteIds.remove(product.getId());
            }
            holder.ivFavorite.setImageResource(isNowFav ? R.drawable.ic_favorite_red : R.drawable.ic_favorite_border);
            // Callback về Fragment để gọi API
            if (favoriteListener != null) {
                favoriteListener.onFavoriteToggle(product, isNowFav);
            }
        });
    }

    @Override
    public int getItemCount() {
        return products != null ? products.size() : 0;
    }

    static class GridViewHolder extends RecyclerView.ViewHolder {
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

        GridViewHolder(@NonNull View itemView) {
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
}
