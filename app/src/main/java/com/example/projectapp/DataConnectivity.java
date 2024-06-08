package com.example.projectapp;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataConnectivity {
    private String email,passwd;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private FirebaseAuth mAuth;
    public DataConnectivity(String email,String passwd){
        this.email = email;
        this.passwd = passwd;
        this.mAuth = FirebaseAuth.getInstance();
    }

    protected CompletableFuture<String> signUpUser(){
        CompletableFuture<String> signupResult = new CompletableFuture<>();

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                mAuth.createUserWithEmailAndPassword(email,passwd)
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful())
                            {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if(user != null)
                                {
                                    user.sendEmailVerification()
                                            .addOnSuccessListener(avoid ->{
                                                signupResult.complete("Registration successfully verification email sent.");
                                            })
                                            .addOnFailureListener(e ->{
                                                signupResult.complete(e.getMessage());
                                            });
                                }
                            }
                            else{
                                signupResult.complete(""+task.getException().getMessage());
                            }
                        });
            }
        });

        return signupResult;
    }
}
