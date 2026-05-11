package com.pixibeestudio.greenly.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Response wrapper cho API GET /api/notifications.
 */
public class NotificationResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private List<NotificationItem> data;

    public boolean isSuccess() {
        return success;
    }

    public List<NotificationItem> getData() {
        return data;
    }
}
