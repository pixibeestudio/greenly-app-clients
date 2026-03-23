package com.pixibeestudio.greenly.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pixibeestudio.greenly.R;

import java.util.List;

/**
 * Adapter hiển thị sản phẩm dạng cuộn ngang.
 * Item cuối cùng (position == 10) sẽ hiển thị "Xem thêm".
 * Hỗ trợ chế độ showIcons để hiển thị icon trái tim + nút thêm giỏ hàng (dành cho Top 100).
 */
public class ProductHorizontalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Hai loại item: sản phẩm bình thường và nút "Xem thêm"
    private static final int TYPE_PRODUCT = 0;
    private static final int TYPE_VIEW_MORE = 1;

    private final List<String[]> products; // Mỗi item: [tên, giá]
    private final boolean showIcons; // Hiển thị icon trái tim + thêm giỏ hàng (cho Top 100)

    public ProductHorizontalAdapter(List<String[]> products, boolean showIcons) {
        this.products = products;
        this.showIcons = showIcons;
    }

    @Override
    public int getItemViewType(int position) {
        // Item cuối cùng (thứ 11, index 10) là "Xem thêm"
        if (position == 10) {
            return TYPE_VIEW_MORE;
        }
        return TYPE_PRODUCT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_VIEW_MORE) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_view_more, parent, false);
            return new ViewMoreViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_product_horizontal, parent, false);
            return new ProductViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ProductViewHolder) {
            ProductViewHolder productHolder = (ProductViewHolder) holder;
            String[] product = products.get(position);
            productHolder.tvProductName.setText(product[0]);
            productHolder.tvProductPrice.setText(product[1]);

            // Hiển thị icon trái tim và nút thêm giỏ hàng nếu bật showIcons (dành cho Top 100)
            if (showIcons) {
                productHolder.icHeart.setVisibility(View.VISIBLE);
                productHolder.icAddCart.setVisibility(View.VISIBLE);
            } else {
                productHolder.icHeart.setVisibility(View.GONE);
                productHolder.icAddCart.setVisibility(View.GONE);
            }
        }
        // ViewMore không cần bind dữ liệu
    }

    @Override
    public int getItemCount() {
        // 10 sản phẩm + 1 item "Xem thêm" = 11
        return products != null ? Math.min(products.size(), 10) + 1 : 0;
    }

    // ViewHolder cho sản phẩm
    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        ImageView icHeart;
        ImageView icAddCart;
        TextView tvProductName;
        TextView tvProductPrice;

        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.img_product);
            icHeart = itemView.findViewById(R.id.ic_heart);
            icAddCart = itemView.findViewById(R.id.ic_add_cart);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvProductPrice = itemView.findViewById(R.id.tv_product_price);
        }
    }

    // ViewHolder cho nút "Xem thêm"
    static class ViewMoreViewHolder extends RecyclerView.ViewHolder {
        ViewMoreViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
