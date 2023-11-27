package com.example.gwtalenttrade;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class firstPage extends AppCompatActivity {

    Button createAcc;
    Button signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_page);

        Button createAcc= findViewById(R.id.btnCreateAccount);
        Button signIn= findViewById(R.id.btnSignIn);

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