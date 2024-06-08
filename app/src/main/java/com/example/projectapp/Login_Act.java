package com.example.projectapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Login_Act extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private TextView forgetpswd, signupbtn;
    private static final String SHOPS_REFERENCE = "shops";

    // Reference to the Realtime Database
    private DatabaseReference databaseReference;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("shops");

        emailEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBar);
        forgetpswd = findViewById(R.id.forgetpswd);
        signupbtn = findViewById(R.id.singupnow);
        sharedPreferences = getApplicationContext().getSharedPreferences("UserData",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loginUser();
            }
        });

        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signupintent = new Intent(Login_Act.this, Signup_Act.class);
                startActivity(signupintent);
            }
        });

        forgetpswd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent forgetintent = new Intent(Login_Act.this, ForgetPassword.class);
                startActivity(forgetintent);
            }
        });
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            if (email.isEmpty()) {
                showToast(getString(R.string.enter_email));
            } else {
                showToast(getString(R.string.enter_password));
            }
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        loginButton.setVisibility(View.INVISIBLE);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);
                    loginButton.setVisibility(View.VISIBLE);

                    if (task.isSuccessful()) {
                        // User successfully logged in
                        showToast(getString(R.string.login_successful));
                        editor.putString("email",email);
                        editor.apply();
                        checkUserProfileExistence();
                    } else {
                        // If sign in fails, display a message to the user.
                        showToast(getString(R.string.auth_failed));
                    }
                });
    }

    private void showToast(String message) {
        Toast.makeText(Login_Act.this, message, Toast.LENGTH_SHORT).show();
    }
    private void checkUserProfileExistence() {
        String userId = mAuth.getCurrentUser().getUid();

        // Check if the user has a profile by checking the Realtime Database
        databaseReference.child(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot dataSnapshot = task.getResult();

                if (dataSnapshot.exists()) {
                    // User has a profile
                    String address = dataSnapshot.child("address").getValue(String.class);
                    String gstNo = dataSnapshot.child("gstNo").getValue(String.class);
                    String phoneNo = dataSnapshot.child("phoneNo").getValue(String.class);
                    String shopName = dataSnapshot.child("shopName").getValue(String.class);

                    // Redirect to the homepage with the retrieved data
                    editor.putBoolean("Home",true);
                    editor.apply();
                    Intent homepageIntent = new Intent(Login_Act.this, Homepage.class);
                    homepageIntent.putExtra("address", address);
                    homepageIntent.putExtra("gstNo", gstNo);
                    homepageIntent.putExtra("phoneNo", phoneNo);
                    homepageIntent.putExtra("shopName", shopName);
                    startActivity(homepageIntent);
                    finish();
                } else {
                    // User does not have a profile
                    Intent profileCreateIntent = new Intent(Login_Act.this, Profile_Create.class);
                    startActivity(profileCreateIntent);
                    finish();
                }
            } else {
                // Handle errors
                Toast.makeText(Login_Act.this, "Error checking user profile", Toast.LENGTH_SHORT).show();
            }
        });
    }
}