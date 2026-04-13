package com.pixibeestudio.greenly.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.material.imageview.ShapeableImageView;
import com.pixibeestudio.greenly.R;
import com.pixibeestudio.greenly.data.model.Banner;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter hiển thị danh sách banner quảng cáo trong ViewPager2.
 * Dùng Glide để tải ảnh từ URL vào ShapeableImageView.
 */
public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

    private List<Banner> banners;

    public BannerAdapter(List<Banner> banners) {
        this.banners = banners != null ? banners : new ArrayList<>();
    }

    /**
     * Cập nhật danh sách banner mới và refresh giao diện.
     */
    public void setBanners(List<Banner> newBanners) {
        this.banners = newBanners != null ? newBanners : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_banner, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        Banner banner = banners.get(position);
        // Dùng Glide load ảnh banner với placeholder và error image
        Glide.with(holder.itemView.getContext())
                .load(banner.getImageUrl())
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_error)
                .transition(DrawableTransitionOptions.withCrossFade())
                .centerCrop()
                .into(holder.ivBanner);
    }

    @Override
    public int getItemCount() {
        return banners != null ? banners.size() : 0;
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView ivBanner;

        BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBanner = itemView.findViewById(R.id.ivBanner);
        }
    }
}
