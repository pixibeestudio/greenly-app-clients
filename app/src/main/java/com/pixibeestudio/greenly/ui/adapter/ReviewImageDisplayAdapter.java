package com.pixibeestudio.greenly.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.pixibeestudio.greenly.R;
import com.pixibeestudio.greenly.data.network.RetrofitClient;

import java.util.List;

/**
 * Adapter đơn giản để hiển thị danh sách URL ảnh (đọc-only) cho review đã gửi.
 */
public class ReviewImageDisplayAdapter extends RecyclerView.Adapter<ReviewImageDisplayAdapter.ViewHolder> {

    private final List<String> imageUrls;

    public ReviewImageDisplayAdapter(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review_image, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String url = imageUrls.get(position);
        String fullUrl = url.startsWith("http") ? url : RetrofitClient.BASE_URL + url.replaceFirst("^/", "");
        Glide.with(holder.itemView.getContext())
                .load(fullUrl)
                .placeholder(R.drawable.ic_default_product)
                .centerCrop()
                .into(holder.iv);
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView iv;
        ViewHolder(@NonNull View v) {
            super(v);
            iv = v.findViewById(R.id.iv_review_image);
        }
    }
}
