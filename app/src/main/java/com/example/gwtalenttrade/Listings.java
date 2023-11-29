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
import android.widget.Button;

import com.example.gwtalenttrade.databinding.ActivityListingsBinding;

import java.util.ArrayList;
import java.util.List;

public class Listings extends AppCompatActivity {

private ActivityListingsBinding binding;
    private RecyclerView recyclerView;
    private PostingsAdapter postingsAdapter;
    private DatabaseReference databaseReference;
    private Button btnTutoring, homemadeGoods, btnMealPlans, btnTransport, btnSports, btnMisc, btnReset;

    private String filterCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        btnTutoring = findViewById(R.id.btnTutoring);
        homemadeGoods = findViewById(R.id.homemadeGoods);
        btnMealPlans = findViewById(R.id.btnMealPlans);
        btnTransport = findViewById(R.id.btnTransport);
        btnSports = findViewById(R.id.btnSports);
        btnMisc = findViewById(R.id.btnMisc);
        btnReset= findViewById(R.id.btnReset);

        btnTutoring.setOnClickListener(v -> applyFilter("Tutoring Services"));
        homemadeGoods.setOnClickListener(v -> applyFilter("Homemade Goods and Crafts"));
        btnMealPlans.setOnClickListener(v -> applyFilter("Meal Plans and Food Services"));
        btnTransport.setOnClickListener(v -> applyFilter("Carpooling and Transportation"));
        btnSports.setOnClickListener(v -> applyFilter("Sports and Fitness Services"));
        btnMisc.setOnClickListener(v -> applyFilter("Miscellaneous Services"));
        btnReset.setOnClickListener(v -> resetFilters());

        recyclerView = findViewById(R.id.recyclerView);
        databaseReference = FirebaseDatabase.getInstance().getReference("posts");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        postingsAdapter = new PostingsAdapter(new ArrayList<>());
        recyclerView.setAdapter(postingsAdapter);

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        toolBarLayout.setTitle(getTitle());

        FloatingActionButton fab = binding.fab;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Listings.this, createPost.class));
            }
        });

        readDataFromDatabase();

        Intent intent = getIntent();
        if (intent.hasExtra("filterCategory")) {
            filterCategory = intent.getStringExtra("filterCategory");
            if(filterCategory!=null){
                applyFilter(filterCategory);
            }
        }

        if (intent != null && intent.hasExtra("searchQuery")) {
            String searchQuery = intent.getStringExtra("searchQuery");
            filterListingsBySearchQuery(searchQuery);
        }
    }

    private void applyFilter(String category) {
        databaseReference.orderByChild("category").equalTo(category)
                .addListenerForSingleValueEvent(new ValueEventListener() {
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
                        postingsAdapter.setPostings(posts);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Snackbar.make(recyclerView, "Error reading data from Firebase", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });
    }

    private void resetFilters() {
        readDataFromDatabase();
    }

    private void filterListingsBySearchQuery(String searchQuery) {
        String lowercaseQuery = searchQuery.toLowerCase();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<Post> filteredPosts = new ArrayList<>();

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        try {
                            Post post = dataSnapshot.getValue(Post.class);
                            if (post != null &&
                                    (post.getTitle().toLowerCase().contains(lowercaseQuery) ||
                                            post.getDescription().toLowerCase().contains(lowercaseQuery) ||
                                            post.getCategory().toLowerCase().contains(lowercaseQuery))) {
                                filteredPosts.add(post);
                            }
                        } catch (Exception e) {
                            System.err.println("Error parsing Posting from DataSnapshot: " + e.getMessage());
                        }
                    }

                    postingsAdapter.setPostings(filteredPosts);
                } else {
                    Snackbar.make(recyclerView, "Data does not exist", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Snackbar.make(recyclerView, "Error reading data from Firebase", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

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
                postingsAdapter.setPostings(posts);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Snackbar.make(recyclerView, "Error reading data from Firebase", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}
