package com.pixibeestudio.greenly.ui.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.pixibeestudio.greenly.R;

import java.util.List;

/**
 * Adapter cho RecyclerView hiển thị ảnh đã chọn + 1 placeholder "Tải lên" ở cuối.
 * Dùng trong WriteReviewFragment.
 */
public class SelectedImageAdapter extends RecyclerView.Adapter<SelectedImageAdapter.ViewHolder> {

    private static final int MAX_IMAGES = 5;

    public interface OnImageActionListener {
        /** Click vào placeholder "Tải lên" */
        void onAddClick();
        /** Click nút xóa (X) ảnh tại position */
        void onRemoveClick(int position);
    }

    private final List<Uri> imageUris;
    private final OnImageActionListener listener;

    public SelectedImageAdapter(List<Uri> imageUris, OnImageActionListener listener) {
        this.imageUris = imageUris;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected_image, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Item cuối cùng là placeholder nếu chưa đạt MAX_IMAGES
        boolean isAddPlaceholder = position == imageUris.size() && imageUris.size() < MAX_IMAGES;

        if (isAddPlaceholder) {
            holder.ivSelected.setVisibility(View.GONE);
            holder.btnRemove.setVisibility(View.GONE);
            holder.layoutAdd.setVisibility(View.VISIBLE);
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) listener.onAddClick();
            });
        } else {
            holder.ivSelected.setVisibility(View.VISIBLE);
            holder.btnRemove.setVisibility(View.VISIBLE);
            holder.layoutAdd.setVisibility(View.GONE);
            Glide.with(holder.itemView.getContext())
                    .load(imageUris.get(position))
                    .centerCrop()
                    .into(holder.ivSelected);
            holder.itemView.setOnClickListener(null);
            holder.btnRemove.setOnClickListener(v -> {
                if (listener != null) listener.onRemoveClick(holder.getAdapterPosition());
            });
        }
    }

    @Override
    public int getItemCount() {
        // Hiển thị N ảnh + 1 placeholder (nếu chưa đủ MAX)
        if (imageUris.size() < MAX_IMAGES) return imageUris.size() + 1;
        return imageUris.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView ivSelected;
        ImageView btnRemove;
        LinearLayout layoutAdd;

        ViewHolder(@NonNull View v) {
            super(v);
            ivSelected = v.findViewById(R.id.iv_selected);
            btnRemove = v.findViewById(R.id.btn_remove_image);
            layoutAdd = v.findViewById(R.id.layout_add_placeholder);
        }
    }
}
