package com.example.gwtalenttrade;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

// SignInActivity.java
import android.content.Intent;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.TextView;
        import androidx.appcompat.app.AppCompatActivity;

public class SignInActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);

        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Collect data and process login (hard-coded for now)
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();

                // Hard-coded user credentials for demonstration
                String hardcodedEmail = "test@example.com";
                String hardcodedPassword = "password123";

                if (email.equals(hardcodedEmail) && password.equals(hardcodedPassword)) {
                    // Login successful, navigate to another activity
                    startActivity(new Intent(SignInActivity.this, HomeActivity.class));
                } else {
                    // Login failed, show an error message or handle accordingly
                    // For example, Toast.makeText(SignInActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        TextView createNowTextView = findViewById(R.id.createNowTextView);
        createNowTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, CreateAccountActivity.class));
            }
        });
    }
}
