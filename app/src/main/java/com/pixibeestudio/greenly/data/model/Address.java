package com.pixibeestudio.greenly.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Model đại diện cho một địa chỉ giao hàng của người dùng
 */
public class Address {
    @SerializedName("id")
    private int id;

    @SerializedName("receiver_name")
    private String receiverName;

    @SerializedName("phone")
    private String phone;

    @SerializedName("province")
    private String province;

    @SerializedName("district")
    private String district;

    @SerializedName("ward")
    private String ward;

    @SerializedName("street")
    private String street;

    @SerializedName("house_number")
    private String houseNumber;

    @SerializedName("full_address")
    private String fullAddress;

    @SerializedName("label")
    private String label;

    @SerializedName("is_default")
    private boolean isDefault;

    public int getId() { return id; }
    public String getReceiverName() { return receiverName; }
    public String getPhone() { return phone; }
    public String getProvince() { return province; }
    public String getDistrict() { return district; }
    public String getWard() { return ward; }
    public String getStreet() { return street; }
    public String getHouseNumber() { return houseNumber; }
    public String getFullAddress() { return fullAddress; }
    public String getLabel() { return label; }
    public boolean isDefault() { return isDefault; }

    public void setId(int id) { this.id = id; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setProvince(String province) { this.province = province; }
    public void setDistrict(String district) { this.district = district; }
    public void setWard(String ward) { this.ward = ward; }
    public void setStreet(String street) { this.street = street; }
    public void setHouseNumber(String houseNumber) { this.houseNumber = houseNumber; }
    public void setFullAddress(String fullAddress) { this.fullAddress = fullAddress; }
    public void setLabel(String label) { this.label = label; }
    public void setDefault(boolean aDefault) { isDefault = aDefault; }

    /**
     * Trả về nhãn hiển thị cho loại địa chỉ
     */
    public String getLabelDisplay() {
        if (label == null) return "Nhà riêng";
        switch (label) {
            case "office": return "Văn phòng";
            case "other": return "Khác";
            default: return "Nhà riêng";
        }
    }
}
