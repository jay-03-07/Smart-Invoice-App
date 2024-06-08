// Product_Act.java
package com.example.projectapp;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

import androidx.appcompat.app.AlertDialog;
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

public class Product_Act extends AppCompatActivity {

    private EditText editTextProductName, editTextRate;
    private Button buttonAddMoreItems;
    private ListView listViewProducts;

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    private List<Product> productList;

    private ProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        editTextProductName = findViewById(R.id.editViewItemName);
        editTextRate = findViewById(R.id.editTextRate);
        buttonAddMoreItems = findViewById(R.id.buttonAddMoreItems);
        listViewProducts = findViewById(R.id.listViewProducts);

        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(this, productList);
        listViewProducts.setAdapter(productAdapter);

        listViewProducts.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // Handle long press (show update and delete options)
                showOptionsDialog(position);
                return true;
            }
        });

        buttonAddMoreItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProduct();
            }
        });

        // Fetch existing products from the database
        fetchProducts();
    }
    @Override
    public void onBackPressed() {
        // Handle back button press
        super.onBackPressed();
        // You can add additional functionality here if needed
    }
    private void addProduct() {
        String productName = editTextProductName.getText().toString().trim();
        String rateString = editTextRate.getText().toString().trim();

        if (productName.isEmpty() || rateString.isEmpty()) {
            showToast("Please enter product name and rate");
            return;
        }

        double rate = Double.parseDouble(rateString);

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference productsRef = databaseReference.child("products").child(userId);

            // Generate a unique key for each product
            String productId = productsRef.push().getKey();

            // Create a Product object
            Product product = new Product(productName, rate);

            // Save the product to the database using the unique key
            productsRef.child(productId).setValue(product);

            // Clear the input fields
            editTextProductName.setText("");
            editTextRate.setText("");

            showToast("Product added successfully");
        } else {
            showToast("User not logged in");
        }

        fetchProducts();
    }


    private void deleteProduct(int position) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference productsRef = databaseReference.child("products").child(userId);

            // Get the product ID of the item to be deleted
            String productId = productList.get(position).getId();

            // Delete the product from the database
            productsRef.child(productId).removeValue();

            // Remove the item from the list
            productList.remove(position);
            productAdapter.notifyDataSetChanged();

            showToast("Product deleted successfully");
        } else {
            showToast("User not logged in");
        }
    }

    private void updateProduct(int position, String newProductName, double newRate) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference productsRef = databaseReference.child("products").child(userId);

            // Get the product ID of the item to be updated
            String productId = productList.get(position).getId();

            // Update the product in the database
            Product updatedProduct = new Product(newProductName, newRate);
            productsRef.child(productId).setValue(updatedProduct);

            // Update the item in the list
            productList.get(position).setProductName(newProductName);
            productList.get(position).setRate(newRate);
            productAdapter.notifyDataSetChanged();

            showToast("Product updated successfully");
        } else {
            showToast("User not logged in");
        }
    }

    private void showOptionsDialog(final int position) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Options")
                .setItems(new CharSequence[]{"Update", "Delete"}, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            // Update option selected
                            showUpdateDialog(position);
                            break;
                        case 1:
                            // Delete option selected
                            showDeleteAlertDialog(position);
                            break;
                    }
                }).show();
    }

    private void showUpdateDialog(final int position) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Update Product");

        View view = getLayoutInflater().inflate(R.layout.update_dialog, null);
        dialogBuilder.setView(view);

        EditText editTextUpdateProductName = view.findViewById(R.id.editTextUpdateProductName);
        EditText editTextUpdateRate = view.findViewById(R.id.editTextUpdateRate);

        editTextUpdateProductName.setText(productList.get(position).getProductName());
        editTextUpdateRate.setText(String.valueOf(productList.get(position).getRate()));

        dialogBuilder.setPositiveButton("Update", (dialog, which) -> {
            String newProductName = editTextUpdateProductName.getText().toString().trim();
            String newRateString = editTextUpdateRate.getText().toString().trim();

            if (!newProductName.isEmpty() && !newRateString.isEmpty()) {
                double newRate = Double.parseDouble(newRateString);
                updateProduct(position, newProductName, newRate);
            } else {
                showToast("Please enter valid values");
            }
        });

        dialogBuilder.setNegativeButton("Cancel", null);

        dialogBuilder.show();
    }

    private void showDeleteAlertDialog(final int position) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Delete Product");
        dialogBuilder.setMessage("Are you sure you want to delete this product?");

        dialogBuilder.setPositiveButton("Delete", (dialog, which) -> {
            deleteProduct(position);
        });

        dialogBuilder.setNegativeButton("Cancel", null);

        dialogBuilder.show();
    }

    private void fetchProducts() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference productsRef = databaseReference.child("products").child(userId);

            // Clear existing list before fetching new data
            productList.clear();

            // Fetch product data from the database
            productsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                        Product product = productSnapshot.getValue(Product.class);
                        if (product != null) {
                            product.setId(productSnapshot.getKey());
                            productList.add(product);
                        }
                    }
                    productAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    showToast("Failed to fetch products: " + databaseError.getMessage());
                }
            });
        } else {
            showToast("User not logged in");
        }
    }

    private void showToast(String message) {
        Toast.makeText(Product_Act.this, message, Toast.LENGTH_SHORT).show();
    }
}
