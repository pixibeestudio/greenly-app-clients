package com.pixibeestudio.greenly.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pixibeestudio.greenly.R;
import com.pixibeestudio.greenly.data.model.Product;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Adapter hiển thị sản phẩm dạng lưới (Grid 2 cột).
 * Dùng cho phần "Tất cả sản phẩm" ở cuối trang Home.
 */
public class ProductGridAdapter extends RecyclerView.Adapter<ProductGridAdapter.GridViewHolder> {

    private final List<Product> products;

    public ProductGridAdapter(List<Product> products) {
        this.products = products;
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

        // Format giá tiền
        try {
            Locale vnLocale = new Locale.Builder().setLanguage("vi").setRegion("VN").build();
            NumberFormat format = NumberFormat.getCurrencyInstance(vnLocale);
            String formattedPrice = format.format(product.getPrice());
            holder.tvProductPrice.setText(formattedPrice);
        } catch (Exception e) {
            holder.tvProductPrice.setText(product.getPrice() + "đ");
        }

        // Load hình ảnh bằng Glide
        Glide.with(holder.itemView.getContext())
                .load(product.getImage())
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_placeholder)
                .centerCrop()
                .into(holder.ivProductImage);
    }

    @Override
    public int getItemCount() {
        return products != null ? products.size() : 0;
    }

    static class GridViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        ImageView ivFavorite;
        TextView tvProductName;
        TextView tvProductOrigin;
        TextView tvProductRating;
        TextView tvProductPrice;
        TextView tvProductUnit;
        ImageView btnAddCart;

        GridViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            ivFavorite = itemView.findViewById(R.id.ivFavorite);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductOrigin = itemView.findViewById(R.id.tvProductOrigin);
            tvProductRating = itemView.findViewById(R.id.tvProductRating);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvProductUnit = itemView.findViewById(R.id.tvProductUnit);
            btnAddCart = itemView.findViewById(R.id.btnAddCart);
        }
    }
}
