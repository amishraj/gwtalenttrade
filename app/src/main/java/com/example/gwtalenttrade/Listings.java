package com.example.gwtalenttrade;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.view.View;
import com.example.gwtalenttrade.databinding.ActivityListingsBinding;

import java.util.ArrayList;
import java.util.List;

public class Listings extends AppCompatActivity {

private ActivityListingsBinding binding;
    private RecyclerView recyclerView;
    private PostingsAdapter postingsAdapter;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityListingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        recyclerView = findViewById(R.id.recyclerView);
        databaseReference = FirebaseDatabase.getInstance().getReference("posts");

        // Set layout manager (HorizontalLinearLayoutManager for horizontal scrolling)
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        // Set up data (Replace with your actual data)
        //List<Posting> postings = getPostings(); // Implement this method to fetch your data
        postingsAdapter = new PostingsAdapter(new ArrayList<>()); // Initialize with empty list
        recyclerView.setAdapter(postingsAdapter);

        //postingsAdapter = new PostingsAdapter(postings);
       // recyclerView.setAdapter(postingsAdapter);

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        toolBarLayout.setTitle(getTitle());

        FloatingActionButton fab = binding.fab;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Create your own post here!", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                startActivity(new Intent(Listings.this, createPost.class));
            }
        });

        readDataFromDatabase();
    }

    // Method to get data
    private List<Post> getPostings() {
        List<Post> posts = new ArrayList<>();

        //posts.add(new Post("Job Title 1", "Description for Job 1", "Category 1"));
        //posts.add(new Post("Job Title 2", "Description for Job 2", "Category 2"));
        // Add more postings as needed

        return posts;
    }

    // Method to read data from Firebase Database
    private void readDataFromDatabase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Post> posts = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        Post post = snapshot.getValue(Post.class);
                        posts.add(post);
                    } catch (Exception e) {
                        System.err.println("Error parsing Posting from DataSnapshot: " + e.getMessage());
                    }
                }
                // Update the adapter with the new data
                postingsAdapter.setPostings(posts);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors, if any
                Snackbar.make(recyclerView, "Error reading data from Firebase", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}
