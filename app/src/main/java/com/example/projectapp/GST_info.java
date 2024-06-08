package com.example.projectapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class GST_info extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private TextView textViewTotalTaxLabel;
    private TextView textViewTotalTax;
    private TextView textViewTotalAmountLabel;
    private TextView textViewTotalAmount;

    private double totalGSTSum = 0.0;
    private double totalAmountSum = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gst_info);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference().child("orders");

        // Initialize TextViews
        textViewTotalTaxLabel = findViewById(R.id.textViewTotalTaxLabel);
        textViewTotalTax = findViewById(R.id.textViewTotalTax);
        textViewTotalAmountLabel = findViewById(R.id.textViewTotalAmountLabel);
        textViewTotalAmount = findViewById(R.id.textViewTotalAmount);

        // Get the currently logged-in user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            // Get the UID of the current user
            String uid = currentUser.getUid();

            // Attach a listener to read the data for the current user
            databaseReference.child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Reset sums for each update
                    totalGSTSum = 0.0;
                    totalAmountSum = 0.0;

                    // Iterate through the orders node for the current user
                    for (DataSnapshot phoneSnapshot : dataSnapshot.getChildren()) {
                        for (DataSnapshot orderSnapshot : phoneSnapshot.getChildren()) {
                            for (DataSnapshot itemSnapshot : orderSnapshot.getChildren()) {
                                // Access rate, quantity, and GST percentage
                                double rate = itemSnapshot.child("rate").getValue(Double.class);
                                int quantity = itemSnapshot.child("quantity").getValue(Integer.class);
                                String gstPercentageString = itemSnapshot.child("gstpercentage").getValue(String.class);

                                // Parse GST percentage (remove "%" sign)
                                double gstPercentage = Double.parseDouble(gstPercentageString.replace("%", ""));

                                // Calculate GST
                                double gstAmount = (rate * quantity * gstPercentage / 100);

                                // Accumulate the values
                                totalGSTSum += gstAmount;
                                totalAmountSum += rate * quantity+gstAmount;

                                Log.d("GST_info", "GST Amount: " + gstAmount);
                            }
                        }
                    }

                    // Update TextViews with the accumulated values
                    textViewTotalTax.setText(String.format(" ₹ %.2f", totalGSTSum));
                    textViewTotalAmount.setText(String.format(" ₹ %.2f", totalAmountSum));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors
                    Toast.makeText(GST_info.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Handle the case where the user is not logged in
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }
}
