// Item.java
package com.example.projectapp;

import java.io.Serializable;

public class Item implements Serializable {
    private String itemName;
    private int quantity;
    private double rate;
    private String gstPercentage;
    private String prodId; // Adding prodId field

    public Item(String itemName, int quantity, double rate, String gstPercentage, String prodId) {
        this.itemName = itemName;
        this.quantity = quantity;
        this.rate = rate;
        this.gstPercentage = gstPercentage;
        this.prodId = prodId;
    }

    public String getItemName() {
        return itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getRate() {
        return rate;
    }

    public String getGstPercentage() {
        return gstPercentage;
    }

    public String getProdId() {
        return prodId;
    }

    public void setProdId(String prodId) {
        this.prodId = prodId;
    }
}
