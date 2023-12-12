package com.example.gwtalenttrade;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreen extends AppCompatActivity {

    // Constant to define the delay duration for the splash screen
    private static final long SPLASH_DELAY = 1500;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Handler to start the next activity after a set delay
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start the main activity after the delay
                startActivity(new Intent(SplashScreen.this, firstPage.class));
                // Finish the SplashScreen activity to remove it from the activity stack
                finish();
            }
        }, SPLASH_DELAY);
    }
}