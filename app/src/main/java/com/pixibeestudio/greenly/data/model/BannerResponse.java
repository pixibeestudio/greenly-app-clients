package com.pixibeestudio.greenly.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Response từ API GET /api/banners.
 */
public class BannerResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private List<Banner> data;

    public boolean isSuccess() {
        return success;
    }

    public List<Banner> getData() {
        return data;
    }
}
