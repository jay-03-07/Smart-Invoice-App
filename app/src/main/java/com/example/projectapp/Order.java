package com.example.projectapp;

import java.util.Map;

public class Order {
    private String orderId;
    private double totalAmount;
    private String payment;

    public String getPayment() {
        return payment;
    }
    public Order() {
        // Default constructor required for Firebase
    }
    public void setPayment(String payment) {
        this.payment = payment;
    }

    public double getTotalAmount() {
        return totalAmount;

    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCustomerId() {
        return customerId;
    }

    public Order(String orderId, Map<String, Item> items, String userId, String customerId) {
        this.orderId = orderId;
        this.items = items;
        this.userId = userId;
        this.customerId = customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Map<String, Item> getItems() {
        return items;
    }

    public void setItems(Map<String, Item> items) {
        this.items = items;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    private Map<String, Item> items;
    private String userId; // assuming you want to store the user ID
    private String customerId; // assuming you want to store the customer ID
}
