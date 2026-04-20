package com.pixibeestudio.greenly.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Sản phẩm trong đơn đã giao, chưa được đánh giá.
 * Dùng cho Tab "Chưa đánh giá" trong màn Đánh giá của tôi.
 */
public class PendingReviewItem {

    @SerializedName("order_detail_id")
    private int orderDetailId;

    @SerializedName("order_id")
    private int orderId;

    @SerializedName("order_code")
    private String orderCode;

    @SerializedName("product_id")
    private int productId;

    @SerializedName("product_name")
    private String productName;

    @SerializedName("product_image")
    private String productImage;

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("price")
    private double price;

    @SerializedName("delivered_at")
    private String deliveredAt;

    // Getters
    public int getOrderDetailId() { return orderDetailId; }
    public int getOrderId() { return orderId; }
    public String getOrderCode() { return orderCode; }
    public int getProductId() { return productId; }
    public String getProductName() { return productName; }
    public String getProductImage() { return productImage; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public String getDeliveredAt() { return deliveredAt; }
}
