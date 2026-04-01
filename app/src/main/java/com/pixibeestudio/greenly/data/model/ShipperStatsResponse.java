package com.pixibeestudio.greenly.data.model;

import com.google.gson.annotations.SerializedName;

public class ShipperStatsResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private ShipperStats data;

    @SerializedName("message")
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public ShipperStats getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}
