package com.example.projectapp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class OrderIdGenerator {
    private static OrderIdGenerator instance;
    private String orderId;

    private OrderIdGenerator() {
        // Private constructor to prevent instantiation
        generateOrderId();
    }

    public static OrderIdGenerator getInstance() {
        if (instance == null) {
            instance = new OrderIdGenerator();
        }
        return instance;
    }

    private void generateOrderId() {
        // Get current timestamp
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String timestamp = dateFormat.format(new Date());

        // Generate a random component
        Random random = new Random();
        int randomInt = random.nextInt(10000); // You can adjust the range as needed

        // Combine timestamp and random component
        orderId = "Order" + timestamp + randomInt;
    }

    public String getOrderId() {
        return orderId;
    }

    public void resetOrderId() {
        // Reset order ID to null or any other appropriate value
        orderId = null;
    }
}
