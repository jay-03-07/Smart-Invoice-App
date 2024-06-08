package com.example.projectapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Signup_Act extends AppCompatActivity {

    EditText emailsighup,pswdsighup,conpswdsighup;
    Button createButton;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        TextView signuptxt=findViewById(R.id.signupText);
        createButton = findViewById(R.id.createButton);
        emailsighup = findViewById(R.id.emailsighup);
        pswdsighup = findViewById(R.id.pswdsighup);
        conpswdsighup = findViewById(R.id.conpswdsighup);
        progressBar = findViewById(R.id.progressBar);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailsighup.getText().toString().trim();
                String passwd = pswdsighup.getText().toString().trim();
                String cpasswd = conpswdsighup.getText().toString().trim();

                progressBar.setVisibility(View.VISIBLE);
                createButton.setVisibility(View.INVISIBLE);

                if (email.isEmpty() || passwd.isEmpty() || cpasswd.isEmpty()) {
                    createButton.setVisibility(View.VISIBLE); // Set it back to VISIBLE
                    Toast.makeText(Signup_Act.this, "Please fill the field", Toast.LENGTH_SHORT).show();
                    return;
                } else if (passwd.equals(cpasswd)) {
                    DataConnectivity dc = new DataConnectivity(email, passwd);
                    dc.signUpUser().thenAccept(result -> {
                        runOnUiThread(() -> {
                            Toast.makeText(Signup_Act.this, "" + result, Toast.LENGTH_SHORT).show();
                            if (result.equals("Registration successfully verification email sent.")) {
                                Intent loginIntent = new Intent(Signup_Act.this, Login_Act.class);
                                loginIntent.putExtra("email", email);
                                startActivity(loginIntent);
                                finish();
                            } else {
                                progressBar.setVisibility(View.INVISIBLE);
                                createButton.setVisibility(View.VISIBLE);
                            }
                        });
                    });
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    createButton.setVisibility(View.VISIBLE);
                    Toast.makeText(Signup_Act.this, "Password does not match", Toast.LENGTH_SHORT).show();
                }
            }
        });

        signuptxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}