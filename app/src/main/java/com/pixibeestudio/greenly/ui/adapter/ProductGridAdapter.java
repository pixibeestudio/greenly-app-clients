package com.pixibeestudio.greenly.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pixibeestudio.greenly.R;

import java.util.List;

/**
 * Adapter hiển thị sản phẩm dạng lưới (Grid 2 cột).
 * Dùng cho phần "Tất cả sản phẩm" ở cuối trang Home.
 */
public class ProductGridAdapter extends RecyclerView.Adapter<ProductGridAdapter.GridViewHolder> {

    private final List<String[]> products; // Mỗi item: [tên, giá]

    public ProductGridAdapter(List<String[]> products) {
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
        String[] product = products.get(position);
        holder.tvProductName.setText(product[0]);
        holder.tvProductPrice.setText(product[1]);
    }

    @Override
    public int getItemCount() {
        return products != null ? products.size() : 0;
    }

    static class GridViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvProductName;
        TextView tvProductPrice;

        GridViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
        }
    }
}
