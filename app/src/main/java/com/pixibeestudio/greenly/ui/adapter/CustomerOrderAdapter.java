package com.pixibeestudio.greenly.ui.adapter;

import android.content.Context;
import android.graphics.Color;
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
import com.pixibeestudio.greenly.data.model.Order;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CustomerOrderAdapter extends RecyclerView.Adapter<CustomerOrderAdapter.OrderViewHolder> {

    private final Context context;
    private List<Order> orderList = new ArrayList<>();
    private final OnOrderClickListener listener;

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
        void onActionClick(Order order);
    }

    public CustomerOrderAdapter(Context context, OnOrderClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setOrders(List<Order> orders) {
        this.orderList = orders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_customer_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        // 1. Trạng thái đơn hàng
        String statusText;
        int statusColor;
        switch (order.getStatus()) {
            case "pending":
                statusText = "Chờ xác nhận";
                statusColor = Color.parseColor("#FF9800"); // Orange
                break;
            case "processing":
                statusText = "Đang xử lý";
                statusColor = Color.parseColor("#2196F3"); // Blue
                break;
            case "ready_for_pickup":
                statusText = "Chờ lấy hàng";
                statusColor = Color.parseColor("#9C27B0"); // Purple
                break;
            case "shipping":
                statusText = "Đang giao";
                statusColor = Color.parseColor("#03A9F4"); // Deep Orange
                break;
            case "delivered":
                statusText = "Đã giao";
                statusColor = Color.parseColor("#4CAF50"); // Green
                break;
            case "cancelled":
                statusText = "Đã hủy";
                statusColor = Color.parseColor("#F44336"); // Red
                break;
            default:
                statusText = "Không xác định";
                statusColor = Color.parseColor("#757575"); // Grey
                break;
        }
        holder.tvOrderStatus.setText(statusText);
        holder.tvOrderStatus.setTextColor(statusColor);

        // 2. Body (Thông tin sản phẩm đại diện)
        holder.tvProductName.setText(order.getTitle() != null ? order.getTitle() : "Đơn hàng #" + order.getId());
        holder.tvProductCategory.setText(order.getOrderCode() != null ? "Mã: " + order.getOrderCode() : "");
        holder.tvQuantity.setText("");

        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"));
        holder.tvProductPrice.setText(format.format(order.getTotalPrice()));

        Glide.with(context)
                .load(order.getImageUrl())
                .placeholder(R.drawable.ic_default_product)
                .error(R.drawable.ic_default_product)
                .centerCrop()
                .into(holder.ivProductImage);

        // 3. Footer (Tổng tiền)
        holder.tvTotalAmount.setText(format.format(order.getTotalPrice()));

        // 4. Nút Action tùy theo trạng thái
        if ("pending".equals(order.getStatus())) {
            holder.btnAction.setVisibility(View.VISIBLE);
            holder.btnAction.setText("Hủy đơn");
            holder.btnAction.setBackgroundColor(Color.parseColor("#F44336")); // Đỏ
        } else {
            holder.btnAction.setVisibility(View.VISIBLE);
            holder.btnAction.setText("Xem chi tiết");
            holder.btnAction.setBackgroundColor(Color.parseColor("#4CAF50")); // Xanh
        }

        // Click Events
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onOrderClick(order);
        });

        holder.btnAction.setOnClickListener(v -> {
            if (listener != null) listener.onActionClick(order);
        });
    }

    @Override
    public int getItemCount() {
        return orderList != null ? orderList.size() : 0;
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderStatus, tvProductName, tvProductCategory, tvQuantity, tvProductPrice, tvTotalAmount;
        ShapeableImageView ivProductImage;
        MaterialButton btnAction;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductCategory = itemView.findViewById(R.id.tvProductCategory);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvTotalAmount = itemView.findViewById(R.id.tvTotalAmount);
            btnAction = itemView.findViewById(R.id.btnAction);
        }
    }
}
