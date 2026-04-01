package com.pixibeestudio.greenly.ui.fragment.shipper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.pixibeestudio.greenly.R;
import com.pixibeestudio.greenly.data.model.Order;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ShipperShippingAdapter extends RecyclerView.Adapter<ShipperShippingAdapter.ShippingViewHolder> {

    private final Context context;
    private List<Order> orders;
    private final OnShippingActionListener listener;

    public interface OnShippingActionListener {
        void onCallClick(Order order);
        void onMapClick(Order order);
        void onFailClick(Order order);
        void onCompleteClick(Order order);
    }

    public ShipperShippingAdapter(Context context, OnShippingActionListener listener) {
        this.context = context;
        this.orders = new ArrayList<>();
        this.listener = listener;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ShippingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_shipping, parent, false);
        return new ShippingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShippingViewHolder holder, int position) {
        Order order = orders.get(position);
        
        holder.tvOrderCode.setText(order.getOrderCode() != null ? order.getOrderCode() : "Đơn hàng #" + order.getId());
        
        DecimalFormat df = new DecimalFormat("#,###đ");
        
        // Phí ship shipper nhận được (chỉ để hiển thị góc phải trên)
        double shippingFee = order.getShippingFee() > 0 ? order.getShippingFee() : 20000;
        holder.tvShippingFee.setText(df.format(shippingFee));

        // Thông tin khách hàng
        holder.tvCustomerName.setText(order.getShippingName() != null ? order.getShippingName() : "Khách hàng");
        holder.tvCustomerAddress.setText(order.getAddress() != null ? order.getAddress() : "Chưa có địa chỉ");

        // Tổng thu hộ COD
        double totalCod = order.getTotalPrice() > 0 ? order.getTotalPrice() : 0;
        holder.tvCodAmount.setText(df.format(totalCod));

        // Các sự kiện click
        holder.btnCallCustomer.setOnClickListener(v -> {
            if (listener != null) listener.onCallClick(order);
        });

        holder.btnNavigateMap.setOnClickListener(v -> {
            if (listener != null) listener.onMapClick(order);
        });

        holder.btnDeliveryFailed.setOnClickListener(v -> {
            if (listener != null) listener.onFailClick(order);
        });

        holder.btnDeliverySuccess.setOnClickListener(v -> {
            if (listener != null) listener.onCompleteClick(order);
        });
    }

    @Override
    public int getItemCount() {
        return orders != null ? orders.size() : 0;
    }

    static class ShippingViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderCode, tvShippingFee, tvCustomerName, tvCustomerAddress, tvCodAmount;
        MaterialButton btnCallCustomer, btnNavigateMap, btnDeliveryFailed, btnDeliverySuccess;

        public ShippingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderCode = itemView.findViewById(R.id.tvOrderCode);
            tvShippingFee = itemView.findViewById(R.id.tvShippingFee);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvCustomerAddress = itemView.findViewById(R.id.tvCustomerAddress);
            tvCodAmount = itemView.findViewById(R.id.tvCodAmount);
            
            btnCallCustomer = itemView.findViewById(R.id.btnCallCustomer);
            btnNavigateMap = itemView.findViewById(R.id.btnNavigateMap);
            btnDeliveryFailed = itemView.findViewById(R.id.btnDeliveryFailed);
            btnDeliverySuccess = itemView.findViewById(R.id.btnDeliverySuccess);
        }
    }
}
