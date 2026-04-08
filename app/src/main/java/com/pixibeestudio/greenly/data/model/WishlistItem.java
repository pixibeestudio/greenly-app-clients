package com.pixibeestudio.greenly.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Model đại diện cho 1 mục trong danh sách yêu thích.
 * Map từ response API: GET /api/wishlist
 * Backend trả về: { id, user_id, product_id, created_at, updated_at, product: {...} }
 */
public class WishlistItem {
    private int id;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("product_id")
    private int productId;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    // Quan hệ nested: Wishlist belongsTo Product (với images)
    private Product product;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
