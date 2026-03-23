package com.pixibeestudio.greenly.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.pixibeestudio.greenly.R;

import java.util.List;

/**
 * Adapter hiển thị danh sách danh mục sản phẩm (cuộn ngang).
 * Sử dụng mock data gồm tên danh mục.
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final List<String> categoryNames;

    public CategoryAdapter(List<String> categoryNames) {
        this.categoryNames = categoryNames;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.tvCategoryName.setText(categoryNames.get(position));
    }

    @Override
    public int getItemCount() {
        return categoryNames != null ? categoryNames.size() : 0;
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView imgCategory;
        TextView tvCategoryName;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCategory = itemView.findViewById(R.id.img_category);
            tvCategoryName = itemView.findViewById(R.id.tv_category_name);
        }
    }
}
