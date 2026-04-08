package com.pixibeestudio.greenly.data.model;

import java.util.List;

/**
 * Response wrapper cho API GET /api/wishlist
 * Backend trả về: { success: true, data: [ {id, user_id, product_id, product: {...}}, ... ] }
 */
public class WishlistResponse {
    private boolean success;
    private List<WishlistItem> data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<WishlistItem> getData() {
        return data;
    }

    public void setData(List<WishlistItem> data) {
        this.data = data;
    }
}
