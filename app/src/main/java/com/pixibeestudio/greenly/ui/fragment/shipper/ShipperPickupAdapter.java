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

public class ShipperPickupAdapter extends RecyclerView.Adapter<ShipperPickupAdapter.PickupViewHolder> {

    private final Context context;
    private List<Order> orders;
    private final OnPickupClickListener listener;

    public interface OnPickupClickListener {
        void onPickupClick(Order order);
    }

    public ShipperPickupAdapter(Context context, OnPickupClickListener listener) {
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
    public PickupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_pickup, parent, false);
        return new PickupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PickupViewHolder holder, int position) {
        Order order = orders.get(position);
        
        holder.tvOrderCode.setText(order.getOrderCode() != null ? order.getOrderCode() : "Đơn hàng #" + order.getId());
        
        DecimalFormat df = new DecimalFormat("#,###đ");
        double shippingFee = order.getShippingFee() > 0 ? order.getShippingFee() : 20000; // Giả định 20k nếu không có
        holder.tvShippingFee.setText(df.format(shippingFee));

        // Gán cứng địa chỉ cửa hàng
        holder.tvStoreName.setText("Cửa hàng Greenly");
        holder.tvStoreAddress.setText("141 Chiến Thắng, Thanh Trì, Hà Nội");
        holder.tvPickupNote.setText("Hãy đọc mã đơn " + (order.getOrderCode() != null ? order.getOrderCode() : "#" + order.getId()) + " cho Admin để nhận hàng nhé.");

        holder.btnPickupSuccess.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPickupClick(order);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orders != null ? orders.size() : 0;
    }

    static class PickupViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderCode, tvShippingFee, tvStoreName, tvStoreAddress, tvPickupNote;
        MaterialButton btnPickupSuccess;

        public PickupViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderCode = itemView.findViewById(R.id.tvOrderCode);
            tvShippingFee = itemView.findViewById(R.id.tvShippingFee);
            tvStoreName = itemView.findViewById(R.id.tvStoreName);
            tvStoreAddress = itemView.findViewById(R.id.tvStoreAddress);
            tvPickupNote = itemView.findViewById(R.id.tvPickupNote);
            btnPickupSuccess = itemView.findViewById(R.id.btnPickupSuccess);
        }
    }
}
