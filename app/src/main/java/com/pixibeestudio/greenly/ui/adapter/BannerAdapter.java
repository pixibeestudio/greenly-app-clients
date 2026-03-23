package com.pixibeestudio.greenly.ui.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pixibeestudio.greenly.R;

import java.util.List;

/**
 * Adapter hiển thị danh sách banner quảng cáo trong ViewPager2.
 * Sử dụng mock data với các màu nền khác nhau để test UI.
 */
public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

    private final List<Integer> bannerColors;

    public BannerAdapter(List<Integer> bannerColors) {
        this.bannerColors = bannerColors;
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
        // Gán màu nền cho banner giả lập
        holder.imgBanner.setBackgroundColor(bannerColors.get(position));
    }

    @Override
    public int getItemCount() {
        return bannerColors != null ? bannerColors.size() : 0;
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBanner;

        BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBanner = itemView.findViewById(R.id.img_banner);
        }
    }
}
