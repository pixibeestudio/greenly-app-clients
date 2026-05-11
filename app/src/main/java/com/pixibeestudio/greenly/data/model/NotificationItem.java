package com.pixibeestudio.greenly.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Model thông báo nhận từ API /api/notifications.
 */
public class NotificationItem {

    @SerializedName("id")
    private int id;

    @SerializedName("order_id")
    private int orderId;

    @SerializedName("order_code")
    private String orderCode;

    @SerializedName("type")
    private String type;

    @SerializedName("title")
    private String title;

    @SerializedName("message")
    private String message;

    @SerializedName("is_read")
    private boolean isRead;

    @SerializedName("time_ago")
    private String timeAgo;

    @SerializedName("created_at")
    private String createdAt;

    // --- Getters ---

    public int getId() {
        return id;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public boolean isRead() {
        return isRead;
    }

    public String getTimeAgo() {
        return timeAgo;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    // --- Setters ---

    public void setId(int id) {
        this.id = id;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}
