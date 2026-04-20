package com.pixibeestudio.greenly.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.pixibeestudio.greenly.R;
import com.pixibeestudio.greenly.data.model.PendingReviewItem;
import com.pixibeestudio.greenly.data.network.RetrofitClient;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Adapter hiển thị danh sách sản phẩm chưa đánh giá.
 */
public class PendingReviewAdapter extends RecyclerView.Adapter<PendingReviewAdapter.ViewHolder> {

    public interface OnReviewClickListener {
        void onReviewClick(PendingReviewItem item);
    }

    private final List<PendingReviewItem> items;
    private final OnReviewClickListener listener;

    public PendingReviewAdapter(List<PendingReviewItem> items, OnReviewClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pending_review, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PendingReviewItem item = items.get(position);

        holder.tvProductName.setText(item.getProductName());
        holder.tvOrderCode.setText("Mã: #" + item.getOrderCode());
        holder.tvQuantity.setText("x" + item.getQuantity());

        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"));
        holder.tvPrice.setText(format.format(item.getPrice()));

        // Load ảnh sản phẩm
        String imageUrl = item.getProductImage();
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

        holder.btnReview.setOnClickListener(v -> {
            if (listener != null) listener.onReviewClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView ivProduct;
        TextView tvProductName, tvOrderCode, tvQuantity, tvPrice;
        MaterialButton btnReview;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.iv_product_pending);
            tvProductName = itemView.findViewById(R.id.tv_product_name_pending);
            tvOrderCode = itemView.findViewById(R.id.tv_order_code_pending);
            tvQuantity = itemView.findViewById(R.id.tv_quantity_pending);
            tvPrice = itemView.findViewById(R.id.tv_price_pending);
            btnReview = itemView.findViewById(R.id.btn_review_now);
        }
    }
}
