package com.example.gwtalenttrade;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class firstPage extends AppCompatActivity {

    Button createAcc;
    Button signIn;
    FirebaseAuth mAuth;
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
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

        Button createAcc= findViewById(R.id.btnCreateAccount);
        Button signIn= findViewById(R.id.btnSignIn);

        mAuth= FirebaseAuth.getInstance();
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(firstPage.this, SignInActivity.class);

                // Start the new activity
                startActivity(intent);
            }
        });

        createAcc.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(firstPage.this, CreateAccount.class);

                // Start the new activity
                startActivity(intent);
            }
        });
    }
}