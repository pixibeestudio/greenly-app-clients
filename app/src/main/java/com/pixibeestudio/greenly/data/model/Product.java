package com.pixibeestudio.greenly.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Product {
    private int id;
    private String name;
    private String image;
    private double price;
    
    @SerializedName("discount_price")
    private double discountPrice;
    
    private String unit;
    private String origin;
    
    @SerializedName("stock_quantity")
    private int stockQuantity;

    @SerializedName("all_images")
    private List<String> allImages;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("supplier")
    private Supplier supplier;

    public Product(int id, String name, String image, double price, double discountPrice, String unit, String origin, int stockQuantity) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.price = price;
        this.discountPrice = discountPrice;
        this.unit = unit;
        this.origin = origin;
        this.stockQuantity = stockQuantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(double discountPrice) {
        this.discountPrice = discountPrice;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public List<String> getAllImages() {
        return allImages;
    }

    public void setAllImages(List<String> allImages) {
        this.allImages = allImages;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }
}
