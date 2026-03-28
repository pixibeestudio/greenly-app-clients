package com.pixibeestudio.greenly.data.model;

import com.google.gson.annotations.SerializedName;

public class CartRequest {
    @SerializedName("product_id")
    private int productId;

    @SerializedName("quantity")
    private int quantity;

    public CartRequest(int productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
