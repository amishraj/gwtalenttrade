package com.example.gwtalenttrade;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class firstPage extends AppCompatActivity {

    // UI components for creating an account and signing in
    Button createAcc;
    Button signIn;
    FirebaseAuth mAuth;

    // Check if a user is already logged in when the app starts
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        System.out.println(currentUser);
        if(currentUser != null){
            startActivity(new Intent(firstPage.this, MainActivity.class));
            finish();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_page);

        // Initializing buttons and FirebaseAuth
        Button createAcc= findViewById(R.id.btnCreateAccount);
        Button signIn= findViewById(R.id.btnSignIn);

        mAuth= FirebaseAuth.getInstance();

        // Set onClickListener for the sign-in button
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Direct the user to the SignInActivity when clicked
                Intent intent = new Intent(firstPage.this, SignInActivity.class);
                startActivity(intent);
            }
        });

        // Set onClickListener for the create account button
        createAcc.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // Direct the user to the CreateAccount activity when clicked
                Intent intent = new Intent(firstPage.this, CreateAccount.class);
                startActivity(intent);
            }
        });
    }
}