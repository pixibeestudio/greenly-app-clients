package com.pixibeestudio.greenly.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.pixibeestudio.greenly.R;
import com.pixibeestudio.greenly.data.model.Review;
import com.pixibeestudio.greenly.data.network.RetrofitClient;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Adapter hiển thị danh sách review đã đánh giá của user.
 */
public class CompletedReviewAdapter extends RecyclerView.Adapter<CompletedReviewAdapter.ViewHolder> {

    private final List<Review> reviews;

    public CompletedReviewAdapter(List<Review> reviews) {
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_completed_review, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Review r = reviews.get(position);

        holder.tvProductName.setText(r.getProductName());
        holder.tvCreatedAt.setText(formatDate(r.getCreatedAt()));

        // Hiển thị rating: tô sáng sao theo số sao
        ImageView[] stars = {holder.star1, holder.star2, holder.star3, holder.star4, holder.star5};
        for (int i = 0; i < 5; i++) {
            stars[i].setColorFilter(i < r.getRating()
                    ? android.graphics.Color.parseColor("#FFC107")
                    : android.graphics.Color.parseColor("#E0E0E0"));
        }

        // Load ảnh sản phẩm
        String imageUrl = r.getProductImage();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            String fullUrl = imageUrl.startsWith("http") ? imageUrl : RetrofitClient.BASE_URL + imageUrl.replaceFirst("^/", "");
            Glide.with(holder.itemView.getContext())
                    .load(fullUrl)
                    .placeholder(R.drawable.ic_default_product)
                    .error(R.drawable.ic_default_product)
                    .centerCrop()
                    .into(holder.ivProduct);
        } else {
            holder.ivProduct.setImageResource(R.drawable.ic_default_product);
        }

        // Comment
        if (r.getComment() != null && !r.getComment().trim().isEmpty()) {
            holder.tvComment.setText(r.getComment());
            holder.tvComment.setVisibility(View.VISIBLE);
        } else {
            holder.tvComment.setVisibility(View.GONE);
        }

        // Danh sách ảnh review
        if (r.getImages() != null && !r.getImages().isEmpty()) {
            holder.rvImages.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
            holder.rvImages.setAdapter(new ReviewImageDisplayAdapter(r.getImages()));
            holder.rvImages.setVisibility(View.VISIBLE);
        } else {
            holder.rvImages.setVisibility(View.GONE);
        }

        // Admin reply
        if (r.getAdminReply() != null && !r.getAdminReply().trim().isEmpty()) {
            holder.tvAdminReply.setText(r.getAdminReply());
            holder.layoutAdminReply.setVisibility(View.VISIBLE);
        } else {
            holder.layoutAdminReply.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    /**
     * Format ISO date string → dd/MM/yyyy
     */
    private String formatDate(String iso) {
        if (iso == null) return "";
        try {
            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat output = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            // Cắt milliseconds + Z
            String cleaned = iso.split("\\.")[0];
            Date d = input.parse(cleaned);
            return d != null ? output.format(d) : iso;
        } catch (Exception e) {
            return iso.length() > 10 ? iso.substring(0, 10) : iso;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView ivProduct;
        TextView tvProductName, tvCreatedAt, tvComment, tvAdminReply;
        ImageView star1, star2, star3, star4, star5;
        RecyclerView rvImages;
        LinearLayout layoutAdminReply;

        ViewHolder(@NonNull View v) {
            super(v);
            ivProduct = v.findViewById(R.id.iv_product_completed);
            tvProductName = v.findViewById(R.id.tv_product_name_completed);
            tvCreatedAt = v.findViewById(R.id.tv_created_at);
            tvComment = v.findViewById(R.id.tv_comment_completed);
            tvAdminReply = v.findViewById(R.id.tv_admin_reply);
            star1 = v.findViewById(R.id.star_1);
            star2 = v.findViewById(R.id.star_2);
            star3 = v.findViewById(R.id.star_3);
            star4 = v.findViewById(R.id.star_4);
            star5 = v.findViewById(R.id.star_5);
            rvImages = v.findViewById(R.id.rv_review_images);
            layoutAdminReply = v.findViewById(R.id.layout_admin_reply);
        }
    }
}
