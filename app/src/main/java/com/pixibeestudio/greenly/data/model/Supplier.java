package com.pixibeestudio.greenly.data.model;

import com.google.gson.annotations.SerializedName;

public class Supplier {
    @SerializedName("name")
    private String name;

    @SerializedName("address")
    private String address;

    @SerializedName("certificate")
    private String certificate;

    public Supplier(String name, String address, String certificate) {
        this.name = name;
        this.address = address;
        this.certificate = certificate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }
}
