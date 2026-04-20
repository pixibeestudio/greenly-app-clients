package com.pixibeestudio.greenly.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Model đánh giá sản phẩm (đã đánh giá xong).
 */
public class Review {

    @SerializedName("id")
    private int id;

    @SerializedName("order_id")
    private int orderId;

    @SerializedName("order_detail_id")
    private int orderDetailId;

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

    @SerializedName("rating")
    private int rating;

    @SerializedName("comment")
    private String comment;

    @SerializedName("images")
    private List<String> images;

    @SerializedName("status")
    private String status;

    @SerializedName("admin_reply")
    private String adminReply;

    @SerializedName("created_at")
    private String createdAt;

    // Getters
    public int getId() { return id; }
    public int getOrderId() { return orderId; }
    public int getOrderDetailId() { return orderDetailId; }
    public int getProductId() { return productId; }
    public String getProductName() { return productName; }
    public String getProductImage() { return productImage; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public List<String> getImages() { return images; }
    public String getStatus() { return status; }
    public String getAdminReply() { return adminReply; }
    public String getCreatedAt() { return createdAt; }
}
