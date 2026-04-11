package com.pixibeestudio.greenly.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.imageview.ShapeableImageView;
import com.pixibeestudio.greenly.R;
import com.pixibeestudio.greenly.data.model.Category;

import java.util.List;

/**
 * Adapter hiển thị danh sách danh mục sản phẩm (cuộn ngang).
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final List<Category> categories;
    private OnCategoryClickListener listener;

    // Interface cho sự kiện click vào danh mục
    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    public CategoryAdapter(List<Category> categories) {
        this.categories = categories;
    }

    public CategoryAdapter(List<Category> categories, OnCategoryClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    public void setOnCategoryClickListener(OnCategoryClickListener listener) {
        this.listener = listener;
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
        Category category = categories.get(position);
        holder.tvCategoryName.setText(category.getName());
        
        // Dùng Glide để load ảnh tròn
        Glide.with(holder.itemView.getContext())
             .load(category.getImage())
             .placeholder(R.drawable.bg_circle_green)
             .error(R.drawable.bg_circle_green)
             .apply(RequestOptions.bitmapTransform(new CircleCrop()))
             .into(holder.imgCategory);

        // Sự kiện click vào danh mục
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCategoryClick(category);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories != null ? categories.size() : 0;
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
