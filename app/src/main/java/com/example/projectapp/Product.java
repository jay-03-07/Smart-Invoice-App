// Product.java
package com.example.projectapp;

public class Product {
    private String id; // Assuming id is the product ID
    private String productName;
    private double rate;

    // Default constructor (required for Firebase)
    public Product() {
    }

    public Product(String productName, double rate) {
        this.productName = productName;
        this.rate = rate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    // Use existing getId() method for getting the product ID
    public String getProductId() {
        return getId();
    }

    public String getProdId() {
         return getId();
    }
}
