package com.example.gwtalenttrade;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private Button logoutBtn;
    private TextView textViewExploreAll;
    private TextView textViewRecentPosts;
    FirebaseUser user;
    TextView nameUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logoutBtn= findViewById(R.id.logoutBtn);
        user = FirebaseAuth.getInstance().getCurrentUser();
        nameUser= findViewById(R.id.nameUser);
        textViewExploreAll = findViewById(R.id.textViewExploreAll);
        textViewRecentPosts= findViewById(R.id.textViewRecentPosts);

        if(user==null){
            startActivity(new Intent(getApplicationContext(), firstPage.class));
            finish();
        } else{
            nameUser.setText(user.getEmail());
        }

        textViewExploreAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Listings.class));
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), firstPage.class));
                finish();
            }
        });

    }
}