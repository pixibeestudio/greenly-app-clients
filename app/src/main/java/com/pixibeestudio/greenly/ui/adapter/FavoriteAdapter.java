package com.pixibeestudio.greenly.ui.adapter;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pixibeestudio.greenly.R;
import com.pixibeestudio.greenly.data.model.Product;
import com.pixibeestudio.greenly.data.model.WishlistItem;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adapter hiển thị danh sách sản phẩm yêu thích.
 * Sử dụng layout item_favorite_product.xml
 */
public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {

    private List<WishlistItem> items = new ArrayList<>();
    private final OnFavoriteItemListener listener;

    /**
     * Interface callback cho các sự kiện trên từng item yêu thích.
     */
    public interface OnFavoriteItemListener {
        void onRemoveItem(WishlistItem item);
        void onAddToCartItem(WishlistItem item);
    }

    public FavoriteAdapter(OnFavoriteItemListener listener) {
        this.listener = listener;
    }

    /**
     * Cập nhật danh sách yêu thích và refresh UI.
     */
    public void setItems(List<WishlistItem> newItems) {
        this.items = newItems != null ? newItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    public List<WishlistItem> getItems() {
        return items;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favorite_product, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        WishlistItem wishlistItem = items.get(position);
        Product product = wishlistItem.getProduct();
        if (product == null) return;

        // Bind tên sản phẩm
        holder.tvName.setText(product.getName());

        // Bind đơn vị
        holder.tvUnit.setText(product.getUnit());

        // Xử lý giá tiền
        double originalPrice = product.getPrice();
        double discountPrice = product.getDiscountPrice();

        try {
            Locale vnLocale = new Locale.Builder().setLanguage("vi").setRegion("VN").build();
            NumberFormat format = NumberFormat.getCurrencyInstance(vnLocale);

            if (discountPrice > 0 && discountPrice < originalPrice) {
                // Có khuyến mãi
                holder.tvPrice.setText(format.format(discountPrice));
                holder.tvOriginalPrice.setVisibility(View.VISIBLE);
                holder.tvOriginalPrice.setText(format.format(originalPrice));
                holder.tvOriginalPrice.setPaintFlags(
                        holder.tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                // Không khuyến mãi
                holder.tvPrice.setText(format.format(originalPrice));
                holder.tvOriginalPrice.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            holder.tvPrice.setText(originalPrice + "đ");
            holder.tvOriginalPrice.setVisibility(View.GONE);
        }

        // Load ảnh sản phẩm bằng Glide
        Glide.with(holder.itemView.getContext())
                .load(product.getImage())
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_placeholder)
                .centerCrop()
                .into(holder.ivImage);

        // Click toàn bộ item -> navigate sang màn chi tiết sản phẩm
        holder.itemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt("productId", product.getId());
            Navigation.findNavController(v).navigate(R.id.action_global_to_productDetailFragment, bundle);
        });

        // Nút xóa item khỏi yêu thích
        holder.btnRemove.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRemoveItem(wishlistItem);
            }
        });

        // Nút thêm vào giỏ hàng (từng item)
        holder.btnAddCart.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAddToCartItem(wishlistItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        com.google.android.material.imageview.ShapeableImageView ivImage;
        TextView tvName;
        TextView tvUnit;
        TextView tvPrice;
        TextView tvOriginalPrice;
        ImageButton btnRemove;
        ImageView btnAddCart;

        FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivFavProductImage);
            tvName = itemView.findViewById(R.id.tvFavProductName);
            tvUnit = itemView.findViewById(R.id.tvFavProductUnit);
            tvPrice = itemView.findViewById(R.id.tvFavProductPrice);
            tvOriginalPrice = itemView.findViewById(R.id.tvFavProductOriginalPrice);
            btnRemove = itemView.findViewById(R.id.btnRemoveItem);
            btnAddCart = itemView.findViewById(R.id.btnAddCartItem);
        }
    }
}
