package com.pixibeestudio.greenly.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pixibeestudio.greenly.R;
import com.pixibeestudio.greenly.data.model.Order;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ShipperOrderAdapter extends RecyclerView.Adapter<ShipperOrderAdapter.OrderViewHolder> {

    private final Context context;
    private List<Order> orderList = new ArrayList<>();
    private final OnOrderActionListener listener;

    public interface OnOrderActionListener {
        void onAcceptClick(Order order);
        void onRejectClick(Order order);
    }

    public ShipperOrderAdapter(Context context, OnOrderActionListener listener) {
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_dashboard_shipper, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        String title = order.getOrderCode();
        if (order.getTitle() != null && !order.getTitle().isEmpty()) {
            title += " (" + order.getTitle() + ")";
        }
        holder.tvProductName.setText(title);

        holder.tvTime.setText(order.getTimeAgo() != null ? order.getTimeAgo() : "Vừa xong");
        holder.tvAddress.setText(order.getAddress());

        // Format tiền
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        holder.tvPrice.setText(format.format(order.getTotalPrice()));

        // Load ảnh sản phẩm hoặc avatar placeholder
        if (order.getImageUrl() != null && !order.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(order.getImageUrl())
                    .placeholder(R.drawable.ic_avatar_placeholder)
                    .into(holder.imgProduct);
        } else {
            holder.imgProduct.setImageResource(R.drawable.ic_avatar_placeholder);
        }

        // Sự kiện click
        holder.btnAccept.setOnClickListener(v -> {
            if (listener != null) listener.onAcceptClick(order);
        });

        holder.btnReject.setOnClickListener(v -> {
            if (listener != null) listener.onRejectClick(order);
        });
    }

    @Override
    public int getItemCount() {
        return orderList != null ? orderList.size() : 0;
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvTime, tvAddress, tvPrice;
        ImageView imgProduct;
        Button btnAccept, btnReject;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}
