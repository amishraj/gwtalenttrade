package com.example.gwtalenttrade;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Profile extends AppCompatActivity {

    private TextView textViewName, textViewGWID, textViewEmail, textViewDOB, textViewPhone;
    private DatabaseReference userReference;
    private RecyclerView recyclerViewMyPosts;
    private PosterAdapter postingsAdapter;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    private Button logoutBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        logoutBtn= findViewById(R.id.logoutBtn);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), firstPage.class));
                finish();
            }
        });

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("posts");

        // Initialize RecyclerView
        recyclerViewMyPosts = findViewById(R.id.recyclerViewMyPosts);
        recyclerViewMyPosts.setLayoutManager(new LinearLayoutManager(this));
        postingsAdapter = new PosterAdapter(new ArrayList<>());
        recyclerViewMyPosts.setAdapter(postingsAdapter);

        // Fetch and display user's posts
        displayMyPosts();

        // Initialize views
        textViewName = findViewById(R.id.textViewName);
        textViewGWID = findViewById(R.id.textViewGWID);
        textViewEmail = findViewById(R.id.textViewEmail);
        textViewDOB = findViewById(R.id.textViewDOB);
        textViewPhone = findViewById(R.id.textViewPhone);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            // Initialize the Realtime Database reference
            userReference = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());

            // Read from the database
            userReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Retrieve user data from the snapshot
                    User user = dataSnapshot.getValue(User.class);

                    // Check if the user data is not null
                    if (user != null) {
                        // Set user data to the views
                        textViewName.setText(user.getFullName());
                        textViewGWID.setText("GWID: " + user.getGwid());
                        textViewEmail.setText("Email: " + user.getEmail());
                        textViewDOB.setText("DOB: " + user.getDob());
                        textViewPhone.setText("Phone: " + user.getPhone());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

            });
        }
    }

    private void displayMyPosts() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {

            // Query the database for user's posts
            Query postsQuery = databaseReference.orderByChild("postedBy").equalTo(user.getEmail());
            postsQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<Post> myPosts = new ArrayList<>();
                    // Iterate through the posts and add them to the adapter
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Post post = postSnapshot.getValue(Post.class);
                        if (post != null) {
                            myPosts.add(post);
                        }
                    }
                    postingsAdapter.setPostings(myPosts);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle database error
                }
            });
        }
    }
}