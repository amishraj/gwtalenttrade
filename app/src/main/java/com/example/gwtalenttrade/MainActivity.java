package com.example.gwtalenttrade;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.gwtalenttrade.databinding.ActivityListingsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button logoutBtn;
    private TextView textViewExploreAll;
    private TextView textViewRecentPosts;
    FirebaseUser user;
    TextView nameUser;

    private RecyclerView recyclerView;
    private PostingsAdapter postingsAdapter;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);

        // Set layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Initialize the adapter
        postingsAdapter = new PostingsAdapter(new ArrayList<>());
        recyclerView.setAdapter(postingsAdapter);

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference().child("posts");

        // Read the last 3 recent posts
        readRecentPosts();

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

    private void readRecentPosts() {
        databaseReference.orderByChild("timestamp").limitToLast(3)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<Post> recentPosts = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            try {
                                Post post = snapshot.getValue(Post.class);
                                recentPosts.add(post);
                            } catch (Exception e) {
                                // Handle parsing exception if needed
                            }
                        }

                        // Reverse the list to show the most recent posts first
                        Collections.reverse(recentPosts);

                        // Update the adapter with the recent posts
                        postingsAdapter.setPostings(recentPosts);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle errors, if any
                    }
                });
    }
}