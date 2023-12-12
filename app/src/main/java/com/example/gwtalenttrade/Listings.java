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

        //initializing buttons for different filter categories
        btnTutoring = findViewById(R.id.btnTutoring);
        homemadeGoods = findViewById(R.id.homemadeGoods);
        btnMealPlans = findViewById(R.id.btnMealPlans);
        btnTransport = findViewById(R.id.btnTransport);
        btnSports = findViewById(R.id.btnSports);
        btnMisc = findViewById(R.id.btnMisc);
        btnReset= findViewById(R.id.btnReset);

        //for each button, defining a click listener that sends the respective category of the service as a string to the applyFilter() method
        btnTutoring.setOnClickListener(v -> applyFilter("Tutoring Services"));
        homemadeGoods.setOnClickListener(v -> applyFilter("Homemade Goods and Crafts"));
        btnMealPlans.setOnClickListener(v -> applyFilter("Meal Plans and Food Services"));
        btnTransport.setOnClickListener(v -> applyFilter("Carpooling and Transportation"));
        btnSports.setOnClickListener(v -> applyFilter("Sports and Fitness Services"));
        btnMisc.setOnClickListener(v -> applyFilter("Miscellaneous Services"));
        btnReset.setOnClickListener(v -> resetFilters());

        //create a database reference and call the readDataFromDatabase() method to fetch all posts
        recyclerView = findViewById(R.id.recyclerView);
        databaseReference = FirebaseDatabase.getInstance().getReference("posts");
        readDataFromDatabase(new DataFetchCompleteListener() {
            @Override
            public void onDataFetchComplete() {
                // Now that data is fetched, check for filters/search queries

                Intent intent = getIntent();
                if (intent.hasExtra("filterCategory")) {
                    //retrieve the filter category fetched and apply the filters (if any)
                    filterCategory = intent.getStringExtra("filterCategory");
                    if(filterCategory!=null){
                        applyFilter(filterCategory);
                    }
                }

                //retrieve the search query and filter listings accordingly (if any)
                if (intent != null && intent.hasExtra("searchQuery")) {
                    String searchQuery = intent.getStringExtra("searchQuery");
                    filterListingsBySearchQuery(searchQuery);
                }
            }
        });


        //setting up the recyclerview for all posts along with the postings adapter that stores and displays our listings
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        postingsAdapter = new PostingsAdapter(new ArrayList<>());
        recyclerView.setAdapter(postingsAdapter);

        // setting up the toolbar for the scroll views activity
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        toolBarLayout.setTitle(getTitle());

        //setting up the fab to listen to a click and navigate to the create post class
        FloatingActionButton fab = binding.fab;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Listings.this, createPost.class));
            }
        });
    }

    //this method applies the filter based on the category string
    private void applyFilter(String category) {
        //checks for a category that equals the provided category string
        databaseReference.orderByChild("category").equalTo(category)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    //if found, assign it to a list of posts
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //declare an empty list to store the filtered posts in
                        List<Post> posts = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            //fetch the filtered posts and add it to the posts list
                            try {
                                Post post = snapshot.getValue(Post.class);
                                posts.add(post);
                            } catch (Exception e) {
                                System.err.println("Error parsing Posting from DataSnapshot: " + e.getMessage());
                            }
                        }
                        //set the filtered posts in the postings adapter to display them accurately
                        postingsAdapter.setPostings(posts);
                    }

                    //error handling
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Snackbar.make(recyclerView, "Error reading data from Firebase", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });
    }

    //fetches all posts again and resets the filters
    private void resetFilters() {
        readDataFromDatabase(new DataFetchCompleteListener() {
            @Override
            public void onDataFetchComplete() {
            }
        });
    }

    //fetch posts based on a search query string
    private void filterListingsBySearchQuery(String searchQuery) {
        //first of all, convert the entire text to lowercase
        String lowercaseQuery = searchQuery.toLowerCase();

        //fetch datasnapshot using the db reference
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    //empty list to store the queried/filtered posts in
                    List<Post> filteredPosts = new ArrayList<>();

                    //look at all snapshots in the snapshot children i.e. nested db entries
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        try {
                            //fetch each post, convert to lowercase and if the post contains our search term
                            // whether in title, description or category, add it to the list of posts to be shown
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

                    //update the adapter with the filtered posts
                    postingsAdapter.setPostings(filteredPosts);
                } else {
                    Snackbar.make(recyclerView, "Data does not exist", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }
            }

            //error handling
            @Override
            public void onCancelled(DatabaseError error) {
                Snackbar.make(recyclerView, "Error reading data from Firebase", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    //simplest method that fetches all posts from the db using a databaseReference
    private void readDataFromDatabase(DataFetchCompleteListener listener) {
        databaseReference.addValueEventListener(new ValueEventListener() {

            //if data snapshot is found, execute this method
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //create an empty list of posts and add each fetched post to it from the data snapshot children
                List<Post> posts = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        Post post = snapshot.getValue(Post.class);
                        posts.add(post);
                    } catch (Exception e) {
                        //error handling
                        System.err.println("Error parsing Posting from DataSnapshot: " + e.getMessage());
                    }
                }

                //update the adapter with fetched posts
                postingsAdapter.setPostings(posts);

                //update the callback function
                if (listener != null) {
                    listener.onDataFetchComplete();
                }
            }

            //error handling
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Snackbar.make(recyclerView, "Error reading data from Firebase", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                //update the callback function
                if (listener != null) {
                    listener.onDataFetchComplete();
                }
            }
        });
    }
}
