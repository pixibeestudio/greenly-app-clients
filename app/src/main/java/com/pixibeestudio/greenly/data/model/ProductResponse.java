package com.pixibeestudio.greenly.data.model;

import java.util.List;

public class ProductResponse {
    private boolean success;
    private List<Product> data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Product> getData() {
        return data;
    }

    public void setData(List<Product> data) {
        this.data = data;
    }
}
