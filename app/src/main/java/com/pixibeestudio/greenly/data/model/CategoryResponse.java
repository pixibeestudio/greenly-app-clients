package com.pixibeestudio.greenly.data.model;

import java.util.List;

public class CategoryResponse {
    private boolean success;
    private List<Category> data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Category> getData() {
        return data;
    }

    public void setData(List<Category> data) {
        this.data = data;
    }
}
