package com.example.projectapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);

        SharedPreferences sharedPreferences = getSharedPreferences("UserData",MODE_PRIVATE);
        String email = sharedPreferences.getString("email","");

        if (!email.trim().isEmpty()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (sharedPreferences.contains("Home")) {
                        startActivity(new Intent(getApplicationContext(), Homepage.class));
                        finish();
                    } else {
                        startActivity(new Intent(getApplicationContext(), Profile_Create.class));
                        finish();
                    }
                }
            }, 1000);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(getApplicationContext(), Login_Act.class));
                    finish();
                }
            }, 1000);
        }
    }
}
