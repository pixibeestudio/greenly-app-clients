package com.pixibeestudio.greenly.data.model;

import com.google.gson.annotations.SerializedName;

public class ShipperStats {
    @SerializedName("today_orders")
    private int todayOrders;

    @SerializedName("today_income")
    private double todayIncome;

    @SerializedName("fullname")
    private String fullname;

    @SerializedName("work_status")
    private String workStatus;

    public int getTodayOrders() {
        return todayOrders;
    }

    public void setTodayOrders(int todayOrders) {
        this.todayOrders = todayOrders;
    }

    public double getTodayIncome() {
        return todayIncome;
    }

    public void setTodayIncome(double todayIncome) {
        this.todayIncome = todayIncome;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(String workStatus) {
        this.workStatus = workStatus;
    }
}
