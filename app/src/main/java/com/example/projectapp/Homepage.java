package com.example.projectapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class Homepage extends AppCompatActivity {

    public CardView profileCard,createbillCard,paidbillCard,unpaidbillCard,gstinfoCard,appinfoCard,productCard;
    private ImageView logOut;
    private TextView appinfotxt;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        sharedPreferences = getApplicationContext().getSharedPreferences("UserData",MODE_PRIVATE);
        if (!sharedPreferences.contains("email")) {
            startActivity(new Intent(this, Login_Act.class));
        }

        appinfotxt = findViewById(R.id.viewtxthp);
        profileCard = findViewById(R.id.profilebtn);
        createbillCard =findViewById(R.id.createbillbtn);
        paidbillCard = findViewById(R.id.paidbillbtn);
        unpaidbillCard = findViewById(R.id.unpaidbillbtn);
        gstinfoCard = findViewById(R.id.gstinfobtn);
//        appinfoCard = findViewById(R.id.appinfobtn);
        productCard = findViewById(R.id.productbtn);
        logOut = findViewById(R.id.logOut);


        appinfotxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent appinfointend = new Intent(Homepage.this, App_Info.class);
                startActivity(appinfointend);

            }
        });

        profileCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileintent = new Intent(Homepage.this, Profile_Details.class);// new profile activity
                startActivity(profileintent);
            }
        });

        createbillCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createbillintent = new Intent(Homepage.this, Customer_details.class);
                startActivity(createbillintent);
            }
        });

        paidbillCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent paidbillsintent = new Intent(Homepage.this, PaidBillActivity.class);
                startActivity(paidbillsintent);

            }
        });

        unpaidbillCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent unpaidbillsintent = new Intent(Homepage.this, UnpaidBillActivity.class);
                startActivity(unpaidbillsintent);
            }
        });

        gstinfoCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gstinfointent = new Intent(Homepage.this, GST_info.class);
                startActivity(gstinfointent);
            }
        });

        productCard.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent productintent = new Intent(Homepage.this, Product_Act.class);
                startActivity(productintent);
            }
        });

        logOut.setOnClickListener(v -> {
            sharedPreferences = getApplicationContext().getSharedPreferences("UserData",MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            startActivity(new Intent(getApplicationContext(), Login_Act.class));
            finish();
        });
    }
}