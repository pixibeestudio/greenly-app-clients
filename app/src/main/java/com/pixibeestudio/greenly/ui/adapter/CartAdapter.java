package com.pixibeestudio.greenly.ui.adapter;

import android.app.AlertDialog;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.pixibeestudio.greenly.R;
import com.pixibeestudio.greenly.data.model.Cart;
import com.pixibeestudio.greenly.data.model.Product;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<Cart> cartList = new ArrayList<>();
    private OnCartItemListener listener;

    public interface OnCartItemListener {
        void onIncrease(Cart cart);
        void onDecrease(Cart cart);
        void onDelete(Cart cart);
    }

    public CartAdapter(OnCartItemListener listener) {
        this.listener = listener;
    }

    public void setCartList(List<Cart> cartList) {
        this.cartList = cartList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Cart cart = cartList.get(position);
        Product product = cart.getProduct();

        if (product != null) {
            holder.tvName.setText(product.getName());
            holder.tvUnit.setText(product.getUnit());

            // Định dạng tiền tệ VNĐ
            NumberFormat format = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"));

            if (product.getDiscountPrice() > 0) {
                holder.tvPrice.setText(format.format(product.getDiscountPrice()));
                holder.tvOriginalPrice.setText(format.format(product.getPrice()));
                holder.tvOriginalPrice.setPaintFlags(holder.tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.tvOriginalPrice.setVisibility(View.VISIBLE);
            } else {
                holder.tvPrice.setText(format.format(product.getPrice()));
                holder.tvOriginalPrice.setVisibility(View.GONE);
            }

            // Load ảnh
            String imageUrl = product.getImage();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                String finalUrl = imageUrl;
                if (!imageUrl.startsWith("http")) {
                    finalUrl = "http://192.168.2.200:8000" + imageUrl;
                    //finalUrl = "http://10.0.2.2:8000" + imageUrl;
                }
                Glide.with(holder.itemView.getContext())
                        .load(finalUrl)
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(holder.ivImage);
            } else {
                holder.ivImage.setImageResource(R.drawable.ic_launcher_background);
            }
        }

        holder.tvQuantity.setText(String.valueOf(cart.getQuantity()));

        // Sự kiện Tăng/Giảm
        holder.btnIncrease.setOnClickListener(v -> {
            if (listener != null) {
                listener.onIncrease(cart);
            }
        });

        holder.btnDecrease.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDecrease(cart);
            }
        });

        // Sự kiện Xoá
        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(holder.itemView.getContext())
                    .setTitle("Xoá sản phẩm")
                    .setMessage("Bạn chắc chắn muốn xoá sản phẩm này khỏi giỏ hàng?")
                    .setPositiveButton("Có", (dialog, which) -> {
                        if (listener != null) {
                            listener.onDelete(cart);
                        }
                    })
                    .setNegativeButton("Không", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return cartList == null ? 0 : cartList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView ivImage;
        TextView tvName, tvUnit, tvPrice, tvOriginalPrice, tvQuantity;
        ImageButton btnIncrease, btnDecrease, btnDelete;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivCartItemImage);
            tvName = itemView.findViewById(R.id.tvCartItemName);
            tvUnit = itemView.findViewById(R.id.tvCartItemUnit);
            tvPrice = itemView.findViewById(R.id.tvCartItemPrice);
            tvOriginalPrice = itemView.findViewById(R.id.tvCartItemOriginalPrice);
            tvQuantity = itemView.findViewById(R.id.tvCartItemQuantity);
            btnIncrease = itemView.findViewById(R.id.btnCartItemIncrease);
            btnDecrease = itemView.findViewById(R.id.btnCartItemDecrease);
            btnDelete = itemView.findViewById(R.id.btnCartItemDelete);
        }
    }
}
