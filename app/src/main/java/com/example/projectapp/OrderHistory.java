package com.example.projectapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderHistory extends AppCompatActivity {

    private RecyclerView recyclerViewPaidBills;
    OrderHistoryAdapter orderHistoryAdapter;
    private String phone,payment;
    private List<Map<String,Object>> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        Intent intent = getIntent();
        phone = intent.getStringExtra("phone");
        payment = intent.getStringExtra("payment");

        recyclerViewPaidBills = findViewById(R.id.recyclerViewPaidBills);
        recyclerViewPaidBills.setLayoutManager(new LinearLayoutManager(this));

        fetchData(phone, new OnCompleteFetch() {
            @Override
            public void OnFetchSuccess(List<Map<String, Object>> dataList) {
                orderHistoryAdapter = new OrderHistoryAdapter(dataList);
                recyclerViewPaidBills.setAdapter(orderHistoryAdapter);
            }
            @Override
            public void OnFetchFailed() {
                Toast.makeText(OrderHistory.this, "Data Not found", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void fetchData(String phone, OnCompleteFetch onCompleteFetch) {
        List<Map<String, Object>> dataList = new ArrayList<>();

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("orders").child(userId).child(phone);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                        if (orderSnapshot.exists()) {
                            for (DataSnapshot itemSnapshot : orderSnapshot.getChildren()) {
                                String detail = itemSnapshot.child("payment").getValue(String.class);
                                if (detail != null && detail.equals(payment)) {
                                    Map<String, Object> itemData = new HashMap<>();
                                    itemData.put("productName", itemSnapshot.child("productName").getValue(String.class));
                                    itemData.put("payment", detail);
                                    itemData.put("rate", itemSnapshot.child("rate").getValue(Integer.class));
                                    itemData.put("totalAmount", itemSnapshot.child("totalAmount").getValue(Integer.class));
                                    dataList.add(itemData);
                                }
                            }
                        }
                    }
                    if (!dataList.isEmpty()) {
                        onCompleteFetch.OnFetchSuccess(dataList);
                    } else {
                        onCompleteFetch.OnFetchFailed();
                    }
                } else {
                    onCompleteFetch.OnFetchFailed();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OrderHistory.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public interface OnCompleteFetch {
        public void OnFetchSuccess(List<Map<String,Object>> dataList);
        public void OnFetchFailed();
    }
}