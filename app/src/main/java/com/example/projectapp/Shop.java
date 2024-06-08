package com.example.projectapp;

// Shop.java
public class Shop {
    private String shopName;
    private String address;
    private String phoneNo;
    private String gstNo;

    // Empty constructor required for Firebase
    public Shop() {
    }

    public Shop(String shopName, String address, String phoneNo, String gstNo) {
        this.shopName = shopName;
        this.address = address;
        this.phoneNo = phoneNo;
        this.gstNo = gstNo;
    }

    // Getters and setters

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getGstNo() {
        return gstNo;
    }

    public void setGstNo(String gstNo) {
        this.gstNo = gstNo;
    }
}