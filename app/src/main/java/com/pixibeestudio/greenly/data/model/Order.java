package com.pixibeestudio.greenly.data.model;

import com.google.gson.annotations.SerializedName;

public class Order {
    @SerializedName("id")
    private int id;

    @SerializedName("order_code")
    private String orderCode;

    @SerializedName("title")
    private String title;

    @SerializedName("time_ago")
    private String timeAgo;

    @SerializedName("image_url")
    private String imageUrl;

    @SerializedName("address")
    private String address;

    @SerializedName("total_price")
    private double totalPrice;

    @SerializedName("status")
    private String status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTimeAgo() {
        return timeAgo;
    }

    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
