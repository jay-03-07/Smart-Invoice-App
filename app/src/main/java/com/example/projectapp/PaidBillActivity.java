package com.example.projectapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PaidBillActivity extends AppCompatActivity {

    private PaidBillAdapter paidBillsAdapter;
    private List<Customer> customerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paid_bill);

        RecyclerView recyclerViewPaidBills = findViewById(R.id.recyclerViewPaidBills);

        // Initialize Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            DatabaseReference customersRef = database.getReference("customers").child(currentUser.getUid());

            // Initialize RecyclerView
            customerList = new ArrayList<>();
            paidBillsAdapter = new PaidBillAdapter(customerList); // Create your RecyclerView adapter
            recyclerViewPaidBills.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewPaidBills.setAdapter(paidBillsAdapter);

            // Read data from the database for the current user
            customersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    customerList.clear();

                    for (DataSnapshot cusSnapshot : dataSnapshot.getChildren()) {
                        // Assuming each child node represents a customer
                        Customer customer = cusSnapshot.getValue(Customer.class);
                        if (customer != null) {
                            customerList.add(customer);
                        }
                    }

                    paidBillsAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors here
                }
            });
        }

        paidBillsAdapter.setOnItemClickListener(customer -> {
            Intent paidIntent = new Intent(PaidBillActivity.this, OrderHistory.class);
            paidIntent.putExtra("phone",customer.getPhoneNumber());
            paidIntent.putExtra("payment","paid");
            startActivity(paidIntent);
        });
    }
}
