package com.pixibeestudio.greenly.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Model đại diện cho một banner quảng cáo từ API.
 */
public class Banner {

    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("image_url")
    private String imageUrl;

    @SerializedName("sort_order")
    private int sortOrder;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getSortOrder() {
        return sortOrder;
    }
}
