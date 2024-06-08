package com.example.projectapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Customer_details extends AppCompatActivity {

    Button buttonNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_details);

        buttonNext = findViewById(R.id.buttonNext);

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get customer name, phone number, and address from EditText fields
                String customerName = ((EditText) findViewById(R.id.editTextCustomerName)).getText().toString().trim();
                String phoneNumber = ((EditText) findViewById(R.id.editTextPhoneNumber)).getText().toString().trim();
                String address = ((EditText) findViewById(R.id.editTextAddressCus)).getText().toString().trim();

                // Check if fields are empty
                if (customerName.isEmpty() && phoneNumber.isEmpty() && address.isEmpty()) {
                    // Display a toast message for all empty fields
                    Toast.makeText(Customer_details.this, "Please enter customer name, phone number, and address", Toast.LENGTH_SHORT).show();
                } else if (customerName.isEmpty()) {
                    // Display a toast message for empty customer name
                    Toast.makeText(Customer_details.this, "Please enter customer name", Toast.LENGTH_SHORT).show();
                } else if (phoneNumber.isEmpty()) {
                    // Display a toast message for empty phone number
                    Toast.makeText(Customer_details.this, "Please enter phone number", Toast.LENGTH_SHORT).show();
                } else if (address.isEmpty()) {
                    // Display a toast message for empty address
                    Toast.makeText(Customer_details.this, "Please enter address", Toast.LENGTH_SHORT).show();
                } else if (phoneNumber.length() != 10) {
                    // Display a toast message for invalid phone number length
                    Toast.makeText(Customer_details.this, "Please enter a 10-digit phone number", Toast.LENGTH_SHORT).show();
                } else {
                    // Create intent for Item_details activity
                    Intent itemBillIntent = new Intent(Customer_details.this, Item_details.class);

                    // Add customer details to the intent
                    itemBillIntent.putExtra("customerName", customerName);
                    itemBillIntent.putExtra("phoneNumber", phoneNumber);
                    itemBillIntent.putExtra("address", address);

                    // Start the Item_details activity
                    startActivity(itemBillIntent);
                }
            }
        });
    }
}
