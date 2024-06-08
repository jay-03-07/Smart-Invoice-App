package com.example.projectapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Item_details extends AppCompatActivity {

    private Spinner spinnerProductName;
    private EditText editTextQuantity;
    private TextView editTextRate;
    private Spinner spinnerGST;
    private Button buttonAddMoreItems;
    private Button buttonFinish;

    // List to store items
    private List<Item> itemList;

    // Firebase
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    // Customer details
    private String customerName;
    private String phoneNumber;
    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        // Get customer details from the intent
        Intent intent = getIntent();

        customerName = intent.getStringExtra("customerName");
        phoneNumber = intent.getStringExtra("phoneNumber");
        address = intent.getStringExtra("address");

        spinnerProductName = findViewById(R.id.spinnerItemName);
        editTextQuantity = findViewById(R.id.editTextQuantity);
        editTextRate = findViewById(R.id.editTextRate);
        spinnerGST = findViewById(R.id.spinnerGST);
        buttonAddMoreItems = findViewById(R.id.buttonAddMoreItems);
        buttonFinish = findViewById(R.id.buttonFinish);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.gst_values,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGST.setAdapter(adapter);

        // Initialize the item list
        itemList = new ArrayList<>();

        // Firebase initialization
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Set click listeners for buttons
        buttonAddMoreItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItemToList();
            }
        });

        buttonFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItemToList();
                openBillPreview();
            }
        });

        // Fetch product names for the spinner and rates from the database
        fetchProducts();
    }

    private void fetchProducts() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference productsRef = databaseReference.child("products").child(userId);

            // Fetch product names from the database
            productsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<String> productNames = new ArrayList<>();
                    for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                        Product product = productSnapshot.getValue(Product.class);
                        if (product != null) {
                            productNames.add(product.getProductName());
                        }
                    }

                    // Populate the spinner with product names
                    ArrayAdapter<String> productNameAdapter = new ArrayAdapter<>(
                            Item_details.this,
                            android.R.layout.simple_spinner_item,
                            productNames
                    );
                    productNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerProductName.setAdapter(productNameAdapter);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    showToast("Failed to fetch product names: " + databaseError.getMessage());
                }
            });

            // Set the item name spinner listener to update the rate EditText
            spinnerProductName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    // Fetch the selected product and update the rate EditText
                    String selectedProductName = spinnerProductName.getSelectedItem().toString();
                    updateRateFromProductName(selectedProductName);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // Do nothing
                }
            });
        } else {
            showToast("User not logged in");
        }
    }

    private void updateRateFromProductName(String selectedProductName) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference productsRef = databaseReference.child("products").child(userId);

            // Fetch the rate of the selected product from the database
            productsRef.orderByChild("productName").equalTo(selectedProductName).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                        Product product = productSnapshot.getValue(Product.class);
                        if (product != null) {
                            // Update the rate EditText with the selected product's rate
                            editTextRate.setText(String.valueOf(product.getRate()));
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    showToast("Failed to fetch product rate: " + databaseError.getMessage());
                }
            });
        } else {
            showToast("User not logged in");
        }
    }

    private void addItemToList() {
        String itemName = spinnerProductName.getSelectedItem().toString().trim();
        String quantityStr = editTextQuantity.getText().toString().trim();
        String rateStr = editTextRate.getText().toString().trim();

        // Check if any of the fields are empty
        if (itemName.isEmpty() || quantityStr.isEmpty() || rateStr.isEmpty()) {
            // If itemList is still empty, display a Toast message
            if (itemList.isEmpty()) {
                Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
            }
            return; // Do not proceed further if fields are empty
        }

        int quantity = Integer.parseInt(quantityStr);
        double rate = Double.parseDouble(rateStr);
        String gstPercentage = spinnerGST.getSelectedItem().toString().trim();

        // Create a new item and add it to the list
        // Assuming you have a way to obtain the Product instance (replace "yourProductInstance" with the actual instance)
        Product product = new Product();
        Item newItem = new Item(itemName, quantity, rate, gstPercentage, product.getId());
        itemList.add(newItem);

        // Clear the input fields for the next item
        editTextQuantity.getText().clear();
    }
    private  void fetchProductId(String selectedProductName, Item newItem) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference productsRef = databaseReference.child("products").child(userId);

            // Fetch the product ID from the database based on the selected product name
            productsRef.orderByChild("productName").equalTo(selectedProductName)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                                Product product = productSnapshot.getValue(Product.class);
                                if (product != null) {
                                    // Set the prod_id for the item
                                    newItem.setProdId(product.getProdId());
                                    // Add the item to the list
                                    itemList.add(newItem);
                                    // Clear the input fields for the next item
                                    editTextQuantity.getText().clear();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            showToast("Failed to fetch product ID: " + databaseError.getMessage());
                        }
                    });
        } else {
            showToast("User not logged in");
        }
    }

    private void openBillPreview() {
        // Check if itemList is not empty before redirecting
        if (!itemList.isEmpty()) {
            // Pass the item list and customer details to the Bill_Preview activity
            Intent intent = new Intent(Item_details.this, Bill_Preview.class);
            intent.putExtra("itemList", new ArrayList<>(itemList));
            intent.putExtra("customerName", customerName);
            intent.putExtra("phoneNumber", phoneNumber);
            intent.putExtra("address", address);
            // Include prod_id for each item
            for (Item item : itemList) {
                intent.putExtra("prod_id_" + itemList.indexOf(item), item.getProdId());
            }
            startActivity(intent);
        } else {
            // Display a Toast message indicating that the item list is empty
            Toast.makeText(this, "Please add at least one item before finishing", Toast.LENGTH_SHORT).show();
        }
    }

    private void showToast(String message) {
        Toast.makeText(Item_details.this, message, Toast.LENGTH_SHORT).show();
    }
}
