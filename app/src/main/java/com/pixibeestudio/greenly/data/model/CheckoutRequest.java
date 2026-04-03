package com.pixibeestudio.greenly.data.model;

import com.google.gson.annotations.SerializedName;

public class CheckoutRequest {
    @SerializedName("shipping_name")
    private String shippingName;

    @SerializedName("shipping_phone")
    private String shippingPhone;

    @SerializedName("shipping_address")
    private String shippingAddress;

    @SerializedName("shipping_method")
    private String shippingMethod;

    @SerializedName("payment_method")
    private String paymentMethod;

    @SerializedName("shipping_fee")
    private int shippingFee;

    @SerializedName("note")
    private String notes;

    public CheckoutRequest() {
    }

    public CheckoutRequest(String shippingName, String shippingPhone, String shippingAddress, String shippingMethod, String paymentMethod, int shippingFee, String notes) {
        this.shippingName = shippingName;
        this.shippingPhone = shippingPhone;
        this.shippingAddress = shippingAddress;
        this.shippingMethod = shippingMethod;
        this.paymentMethod = paymentMethod;
        this.shippingFee = shippingFee;
        this.notes = notes;
    }

    public String getShippingName() {
        return shippingName;
    }

    public void setShippingName(String shippingName) {
        this.shippingName = shippingName;
    }

    public String getShippingPhone() {
        return shippingPhone;
    }

    public void setShippingPhone(String shippingPhone) {
        this.shippingPhone = shippingPhone;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getShippingMethod() {
        return shippingMethod;
    }

    public void setShippingMethod(String shippingMethod) {
        this.shippingMethod = shippingMethod;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public int getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(int shippingFee) {
        this.shippingFee = shippingFee;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
