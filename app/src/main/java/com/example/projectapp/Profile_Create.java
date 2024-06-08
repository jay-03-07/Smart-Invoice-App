package com.example.projectapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Profile_Create extends AppCompatActivity {

    private EditText editTextShopName, editTextAddress, editTextPhoneNo, editTextGSTNo;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_create);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("shops");
        String userId = mAuth.getCurrentUser().getUid();

        initializeViews();

        Button submitButton = findViewById(R.id.buttonSubmit);
        Button clearButton = findViewById(R.id.buttonClear);

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFields();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndSaveProfile(userId);
            }
        });

        // Check if the user already has a profile
        checkIfProfileExists(userId);
    }

    private void initializeViews() {
        editTextShopName = findViewById(R.id.editTextShopName);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextPhoneNo = findViewById(R.id.editTextPhoneNo);
        editTextGSTNo = findViewById(R.id.editTextGSTNo);
    }

    private void clearFields() {
        editTextShopName.setText("");
        editTextAddress.setText("");
        editTextPhoneNo.setText("");
        editTextGSTNo.setText("");
    }

    private void validateAndSaveProfile(String userId) {
        FirebaseUser user = mAuth.getCurrentUser();
        String userEmail = user.getEmail();
        String shopName = editTextShopName.getText().toString().trim().toUpperCase();
        String shopAddress = editTextAddress.getText().toString().trim();
        String shopPhone = editTextPhoneNo.getText().toString().trim();
        String shopGST = editTextGSTNo.getText().toString().trim().toUpperCase();

        if (shopName.isEmpty() || shopAddress.isEmpty() || shopPhone.isEmpty() || shopGST.isEmpty()) {
            showToast("Please fill in all the fields");
            return;
        }

        if (shopPhone.length() != 10) {
            showToast("Please enter a 10-digit Phone Number");
            return;
        }

        if (!shopGST.matches("^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$")) {
            showToast("Invalid GST Number");
            return;
        }

        // Save profile information to the Realtime Database
        saveProfileToDatabase(userId,userEmail, shopName, shopAddress, shopPhone, shopGST);
    }

    private void saveProfileToDatabase(String userId,String userEmail, String shopName, String shopAddress, String shopPhone, String shopGST) {
        try {
            DatabaseReference userRef = databaseReference.child(userId);

            // Set the profile details as children of the user ID node
            userRef.child("shopEmail").setValue(userEmail); // Add user email to the profile
            userRef.child("shopName").setValue(shopName);
            userRef.child("address").setValue(shopAddress);
            userRef.child("phoneNo").setValue(shopPhone);
            userRef.child("gstNo").setValue(shopGST);

            // Update SharedPreferences to indicate that the user now has a profile
            updateSharedPreferences();

            // Redirect to the homepage
            redirectToHomepage();
        } catch (Exception e) {
            e.printStackTrace();
            showToast("Error saving profile to database");
        }
    }

    private void updateSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("hasProfile", true);
        editor.apply();

        SharedPreferences sharedPreferences1 = getSharedPreferences("UserData", MODE_PRIVATE);
        sharedPreferences1.edit().putBoolean("Home", true).apply();
    }

    private void showToast(String message) {
        Toast.makeText(Profile_Create.this, message, Toast.LENGTH_SHORT).show();
    }

    private void redirectToHomepage() {
        startActivity(new Intent(Profile_Create.this, Homepage.class));
        finish(); // Finish the Profile activity to prevent going back to it when pressing the back button
    }

    private void checkIfProfileExists(String userId) {
        // Check if the user ID is already present in the "shops" node
        databaseReference.child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot dataSnapshot = task.getResult();
                    if (dataSnapshot.exists()) {
                        // User already has a profile, redirect to homepage
                        redirectToHomepage();
                    }
                } else {
                    // Handle errors
                    showToast("Error checking user profile existence");
                }
            }
        });
    }
}
