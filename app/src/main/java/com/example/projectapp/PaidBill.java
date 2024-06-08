package com.example.projectapp;

public class PaidBill {
    private String customerName;
    private String phoneNumber;

    public String getCustomerName() {
        return customerName;
    }

    public PaidBill(String customerName, String phoneNumber, String address, String paymentStatus, double totalAmount) {
        this.customerName = customerName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.paymentStatus = paymentStatus;
        this.totalAmount = totalAmount;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    private String address;
    private String paymentStatus;
    private double totalAmount;

    // Constructors, getters, and setters
}
