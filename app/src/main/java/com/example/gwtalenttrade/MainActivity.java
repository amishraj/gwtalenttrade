package com.example.gwtalenttrade;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button goToCreateAcc = findViewById(R.id.createacc);

        // Set a click listener for the button
        goToCreateAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Define the intent to navigate to the next activity
                Intent intent = new Intent(MainActivity.this, CreateAccount.class);

                // Start the new activity
                startActivity(intent);
            }
        });
    }
}