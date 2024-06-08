package com.example.projectapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Profile_Details extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private Button backbtn;
    private Button updateButton;
    private TextView textViewShopName;
    private TextView textViewAddress;
    private TextView textViewPhoneNo;
    private TextView textViewGSTNo;
    private TextView textViewShopEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);

        backbtn = findViewById(R.id.buttonGoToHomePage);
        updateButton = findViewById(R.id.buttonUpdateProfile);
        textViewShopName = findViewById(R.id.textViewDisplayShopName);
        textViewAddress = findViewById(R.id.textViewDisplayAddress);
        textViewPhoneNo = findViewById(R.id.textViewDisplayPhoneNo);
        textViewGSTNo = findViewById(R.id.textViewDisplayGSTNo);
        textViewShopEmail = findViewById(R.id.textViewDisplayShopEmail);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the update profile dialog
                showUpdateDialog();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("shops");
            String userId = user.getUid();

            // Retrieve data from the Firebase Realtime Database
            databaseReference.child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()) {
                        DataSnapshot dataSnapshot = task.getResult();

                        if (dataSnapshot.exists()) {
                            // User has a profile, retrieve and display data
                            String shopName = dataSnapshot.child("shopName").getValue(String.class);
                            String shopAddress = dataSnapshot.child("address").getValue(String.class);
                            String shopPhone = dataSnapshot.child("phoneNo").getValue(String.class);
                            String shopGST = dataSnapshot.child("gstNo").getValue(String.class);
                            String shopEmail = user.getEmail();

                            // Display the data in TextViews
                            textViewShopName.setText("Shop Name: " + shopName);
                            textViewAddress.setText("Address: " + shopAddress);
                            textViewPhoneNo.setText("Phone Number: " + shopPhone);
                            textViewGSTNo.setText("GST Number: " + shopGST);
                            textViewShopEmail.setText("Shop Email: " + shopEmail);
                        } else {
                            // User does not have a profile
                            Toast.makeText(Profile_Details.this, "User profile not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Handle errors
                        Toast.makeText(Profile_Details.this, "Error retrieving user profile", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    // Method to show the update profile dialog
    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_update_profile, null);
        builder.setView(dialogView);
        builder.setTitle("Update Profile");

        final EditText editTextShopName = dialogView.findViewById(R.id.editTextShopName);
        final EditText editTextAddress = dialogView.findViewById(R.id.editTextAddress);
        final EditText editTextPhoneNo = dialogView.findViewById(R.id.editTextPhoneNo);
        final EditText editTextGSTNo = dialogView.findViewById(R.id.editTextGSTNo);

        // Retrieve current values
        String currentShopName = textViewShopName.getText().toString().substring("Shop Name: ".length());
        String currentAddress = textViewAddress.getText().toString().substring("Address: ".length());
        String currentPhoneNo = textViewPhoneNo.getText().toString().substring("Phone Number: ".length());
        String currentGSTNo = textViewGSTNo.getText().toString().substring("GST Number: ".length());

        // Set capitalized values in EditText fields
        editTextShopName.setText(currentShopName);
        editTextAddress.setText(currentAddress);
        editTextPhoneNo.setText(currentPhoneNo);
        editTextGSTNo.setText(currentGSTNo);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Validate and update the profile fields with the new values
                String newShopName = editTextShopName.getText().toString().toUpperCase();
                String newAddress = editTextAddress.getText().toString();
                String newPhoneNo = editTextPhoneNo.getText().toString();
                String newGSTNo = editTextGSTNo.getText().toString().toUpperCase();

                if (validateAndUpdate(newShopName, newAddress, newPhoneNo, newGSTNo)) {
                    updateProfileField("shopName", newShopName);
                    updateProfileField("address", newAddress);
                    updateProfileField("phoneNo", newPhoneNo);
                    updateProfileField("gstNo", newGSTNo);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    // Method to validate and update the profile fields
    private boolean validateAndUpdate(String newShopName, String newAddress, String newPhoneNo, String newGSTNo) {
        if (TextUtils.isEmpty(newShopName) || TextUtils.isEmpty(newAddress) || TextUtils.isEmpty(newPhoneNo) || TextUtils.isEmpty(newGSTNo)) {
            Toast.makeText(Profile_Details.this, "All fields must be filled", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate phone number
        if (newPhoneNo.length() != 10) {
            Toast.makeText(Profile_Details.this, "Phone number must be 10 digits", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate GST number (Add your validation logic here)
        if (!isValidGSTNumber(newGSTNo)) {
            Toast.makeText(Profile_Details.this, "Invalid GST number", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Display a single Toast message for successful update
        Toast.makeText(Profile_Details.this, "Profile Updated successfully", Toast.LENGTH_SHORT).show();

        return true;
    }

    // Method to validate GST number (Add your validation logic here)
    // Method to validate GST number
    private boolean isValidGSTNumber(String gstNumber) {
        // GST validation regex
        String gstRegex = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$";
        return gstNumber.matches(gstRegex);
    }


    // Method to update the profile field in the database
    private void updateProfileField(final String fieldName, final String newValue) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference userRef = databaseReference.child(userId);

            userRef.child(fieldName).setValue(newValue)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Update the UI immediately after modifying the data
                                updateUI(fieldName, newValue);
                            } else {
                                Toast.makeText(Profile_Details.this, "Error updating " + fieldName, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    // Method to update the UI
    private void updateUI(String fieldName, String newValue) {
        switch (fieldName) {
            case "shopName":
                textViewShopName.setText("Shop Name: " + newValue);
                break;
            case "address":
                textViewAddress.setText("Address: " + newValue);
                break;
            case "phoneNo":
                textViewPhoneNo.setText("Phone Number: " + newValue);
                break;
            case "gstNo":
                textViewGSTNo.setText("GST Number: " + newValue);
                break;
        }
    }
}