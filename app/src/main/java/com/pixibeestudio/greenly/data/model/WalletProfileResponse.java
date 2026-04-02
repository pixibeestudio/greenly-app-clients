package com.pixibeestudio.greenly.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class WalletProfileResponse {

    @SerializedName("totalIncome")
    private double totalIncome;

    @SerializedName("codBalance")
    private double codBalance;

    @SerializedName("completedOrders")
    private int completedOrders;

    @SerializedName("rating")
    private float rating;

    @SerializedName("chartData")
    private List<ChartData> chartData;

    @SerializedName("history")
    private List<TransactionHistory> history;

    public double getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(double totalIncome) {
        this.totalIncome = totalIncome;
    }

    public double getCodBalance() {
        return codBalance;
    }

    public void setCodBalance(double codBalance) {
        this.codBalance = codBalance;
    }

    public int getCompletedOrders() {
        return completedOrders;
    }

    public void setCompletedOrders(int completedOrders) {
        this.completedOrders = completedOrders;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public List<ChartData> getChartData() {
        return chartData;
    }

    public void setChartData(List<ChartData> chartData) {
        this.chartData = chartData;
    }

    public List<TransactionHistory> getHistory() {
        return history;
    }

    public void setHistory(List<TransactionHistory> history) {
        this.history = history;
    }

    public static class ChartData {
        @SerializedName("date")
        private String date;

        @SerializedName("income")
        private double income;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public double getIncome() {
            return income;
        }

        public void setIncome(double income) {
            this.income = income;
        }
    }

    public static class TransactionHistory {
        @SerializedName("id")
        private int id;

        @SerializedName("title")
        private String title;

        @SerializedName("time")
        private String time;

        @SerializedName("amount")
        private double amount;

        @SerializedName("type")
        private String type; // e.g., "INCOME", "WITHDRAWAL", "COD_SUBMIT"

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
