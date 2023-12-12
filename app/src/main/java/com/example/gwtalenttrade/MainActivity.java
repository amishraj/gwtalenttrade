package com.example.gwtalenttrade;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gwtalenttrade.databinding.ActivityListingsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.appcompat.widget.SearchView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView textViewExploreAll;
    private TextView textViewRecentPosts;
    FirebaseUser user;
    TextView nameUser;

    ImageButton profileBtn;

    private RecyclerView recyclerView;
    private PostingsAdapter postingsAdapter;
    private DatabaseReference databaseReference;

    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initializing the searchView element
        searchView = findViewById(R.id.searchViewServices);

        //setting a listener on our search bar
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //this flow is called when a user hits enter after typing in a search query
                //navigates from MainActivity to Listings page with the search query variable attached
                Intent intent = new Intent(MainActivity.this, Listings.class);
                intent.putExtra("searchQuery", query);
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

        //initializing cards on the home page

        CardView cardViewTutoring = findViewById(R.id.cardViewTutoring);
        CardView cardViewHomeGoods= findViewById(R.id.cardViewHomeGoods);
        CardView cardViewFood = findViewById(R.id.cardViewFood);
        CardView cardViewTransport = findViewById(R.id.cardViewTransport);
        CardView cardViewFitness= findViewById(R.id.cardViewFitness);
        CardView cardViewMisc = findViewById(R.id.cardViewMisc);

        //for a click on any card, the respective category of service is sent as a string to the applyFilter() method

        cardViewTutoring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyFilter("Tutoring Services");
            }
        });

        cardViewHomeGoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyFilter("Homemade Goods and Crafts");
            }
        });

        cardViewFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyFilter("Meal Plans and Food Services");
            }
        });

        cardViewTransport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyFilter("Carpooling and Transportation");
            }
        });

        cardViewFitness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyFilter("Sports and Fitness Services");
            }
        });

        cardViewMisc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyFilter("Miscellaneous Services");
            }
        });

        //setting up initializations for the recent posts feature
        recyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //using the same adapter as the one in Listings for uniformity in design
        postingsAdapter = new PostingsAdapter(new ArrayList<>());
        recyclerView.setAdapter(postingsAdapter);

        //reading all posts and storing the reference
        databaseReference = FirebaseDatabase.getInstance().getReference().child("posts");

        //calling the read recent posts method to carry on the task flow
        readRecentPosts();

        //initializing some variables to enable displaying the user's name on the home page
        profileBtn = findViewById(R.id.profileBtn);
        user = FirebaseAuth.getInstance().getCurrentUser();
        nameUser= findViewById(R.id.nameUser);

        //clickable text views
        textViewExploreAll = findViewById(R.id.textViewExploreAll);
        textViewRecentPosts= findViewById(R.id.textViewRecentPosts);

        if(user==null){
            //if the user is not logged in, navigate back to the first page & end this activity
            startActivity(new Intent(getApplicationContext(), firstPage.class));
            finish();
        }
        else{
            //find user by email
            String userEmail = user.getEmail();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
            Query query = userRef.orderByChild("email").equalTo(userEmail);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //user found
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            //store the user profile object based on the User POJO class
                            User userData = snapshot.getValue(User.class);

                            //extract the user's name and set it as the Welcome name
                            String userName = userData.getFullName();
                            nameUser.setText(userName);
                        }
                    } else {
                        //error handling
                        Toast.makeText(MainActivity.this, "User not found!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }

        textViewExploreAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //navigate to all listings if the Explore All textview is clicked
                startActivity(new Intent(getApplicationContext(), Listings.class));
            }
        });

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //navigate to the profile if the profile ImageButton is clicked
                startActivity(new Intent(MainActivity.this, Profile.class));
            }
        });

    }

    private void applyFilter(String category) {
        //take the category entered via the cards and pass the information to the Listings class through the intent variable
        Intent intent = new Intent(MainActivity.this, Listings.class);
        intent.putExtra("filterCategory", category);
        startActivity(intent);
    }

    //method to read and display recent posts in reverse chronological order
    //limiting results to the last 3 in the query
    private void readRecentPosts() {
        databaseReference.orderByChild("timestamp").limitToLast(3)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<Post> recentPosts = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            try {
                                //look for recent posts and add it to the declared list
                                Post post = snapshot.getValue(Post.class);
                                recentPosts.add(post);
                            } catch (Exception e) {
                                //error handling
                            }
                        }

                        //reverse the list to achieve the desired order
                        Collections.reverse(recentPosts);

                        //passing the adapter the list of recent posts to display it on the MainActivity
                        postingsAdapter.setPostings(recentPosts);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }
}