package com.pixibeestudio.greenly.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.pixibeestudio.greenly.R;
import com.pixibeestudio.greenly.data.model.NotificationItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter hiển thị danh sách thông báo trong RecyclerView.
 */
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private final Context context;
    private List<NotificationItem> items = new ArrayList<>();
    private OnNotificationClickListener listener;

    public interface OnNotificationClickListener {
        void onNotificationClick(NotificationItem item);
    }

    public NotificationAdapter(Context context, OnNotificationClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setItems(List<NotificationItem> newItems) {
        this.items = newItems != null ? newItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationItem item = items.get(position);

        // 1. Nội dung thông báo
        holder.tvMessage.setText(item.getMessage());
        holder.tvTimeAgo.setText(item.getTimeAgo());

        // 2. Màu status dot theo loại trạng thái
        int statusColor = getStatusColor(item.getType());
        GradientDrawable statusDot = (GradientDrawable) holder.viewStatusDot.getBackground();
        statusDot.setColor(statusColor);

        // 3. Chấm unread
        holder.viewUnreadDot.setVisibility(item.isRead() ? View.GONE : View.VISIBLE);

        // 4. Background card: chưa đọc → nền xanh nhạt, đã đọc → trắng
        if (!item.isRead()) {
            holder.cardNotification.setCardBackgroundColor(Color.parseColor("#E8F5E9"));
        } else {
            holder.cardNotification.setCardBackgroundColor(Color.WHITE);
        }

        // 5. Click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onNotificationClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * Trả về mã màu tương ứng với trạng thái đơn hàng.
     * Đồng bộ với CustomerOrderAdapter.
     */
    private int getStatusColor(String type) {
        if (type == null) return Color.parseColor("#757575");
        switch (type) {
            case "pending":
                return Color.parseColor("#FF9800");
            case "processing":
                return Color.parseColor("#2196F3");
            case "ready_for_pickup":
                return Color.parseColor("#9C27B0");
            case "shipping":
                return Color.parseColor("#03A9F4");
            case "delivered":
                return Color.parseColor("#4CAF50");
            case "cancelled":
                return Color.parseColor("#F44336");
            default:
                return Color.parseColor("#757575");
        }
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardNotification;
        View viewStatusDot;
        TextView tvMessage;
        TextView tvTimeAgo;
        View viewUnreadDot;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            cardNotification = itemView.findViewById(R.id.cardNotification);
            viewStatusDot = itemView.findViewById(R.id.viewStatusDot);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTimeAgo = itemView.findViewById(R.id.tvTimeAgo);
            viewUnreadDot = itemView.findViewById(R.id.viewUnreadDot);
        }
    }
}
