package com.pixibeestudio.greenly.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pixibeestudio.greenly.R;

import java.util.List;

public class ProductImageSliderAdapter extends RecyclerView.Adapter<ProductImageSliderAdapter.ImageViewHolder> {

    private final List<String> images;

    public ProductImageSliderAdapter(List<String> images) {
        this.images = images;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String imageUrl = images.get(position);
        
        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .centerInside()
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_placeholder)
                .into(holder.ivProductSlider);
    }

    @Override
    public int getItemCount() {
        return images != null ? images.size() : 0;
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductSlider;

        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductSlider = itemView.findViewById(R.id.ivProductSlider);
        }
    }
}
