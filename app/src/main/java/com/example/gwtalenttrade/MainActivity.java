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

        searchView = findViewById(R.id.searchViewServices);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
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
        CardView cardViewTutoring = findViewById(R.id.cardViewTutoring);
        CardView cardViewHomeGoods= findViewById(R.id.cardViewHomeGoods);
        CardView cardViewFood = findViewById(R.id.cardViewFood);
        CardView cardViewTransport = findViewById(R.id.cardViewTransport);
        CardView cardViewFitness= findViewById(R.id.cardViewFitness);
        CardView cardViewMisc = findViewById(R.id.cardViewMisc);

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


        recyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        postingsAdapter = new PostingsAdapter(new ArrayList<>());
        recyclerView.setAdapter(postingsAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("posts");

        readRecentPosts();

        profileBtn = findViewById(R.id.profileBtn);
        user = FirebaseAuth.getInstance().getCurrentUser();
        nameUser= findViewById(R.id.nameUser);
        textViewExploreAll = findViewById(R.id.textViewExploreAll);
        textViewRecentPosts= findViewById(R.id.textViewRecentPosts);

        if(user==null){
            startActivity(new Intent(getApplicationContext(), firstPage.class));
            finish();
        }
        else{
            String userEmail = user.getEmail();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
            Query query = userRef.orderByChild("email").equalTo(userEmail);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User userData = snapshot.getValue(User.class);
                            String userName = userData.getFullName();
                            nameUser.setText(userName);
                        }
                    } else {
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
                startActivity(new Intent(getApplicationContext(), Listings.class));
            }
        });

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Profile.class));
            }
        });

    }

    private void applyFilter(String category) {
        Intent intent = new Intent(MainActivity.this, Listings.class);
        intent.putExtra("filterCategory", category);
        startActivity(intent);
    }

    private void readRecentPosts() {
        databaseReference.orderByChild("timestamp").limitToLast(3)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<Post> recentPosts = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            try {
                                Post post = snapshot.getValue(Post.class);
                                recentPosts.add(post);
                            } catch (Exception e) {
                            }
                        }

                        Collections.reverse(recentPosts);

                        postingsAdapter.setPostings(recentPosts);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }
}