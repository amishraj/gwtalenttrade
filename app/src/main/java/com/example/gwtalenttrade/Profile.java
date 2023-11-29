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
import android.widget.Toast;

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
    private RecyclerView recyclerViewMyRequests;
    private PosterAdapter postingsAdapter;
    private MyRequestAdapter myRequestAdapter;
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

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("posts");

        recyclerViewMyPosts = findViewById(R.id.recyclerViewMyPosts);
        recyclerViewMyPosts.setLayoutManager(new LinearLayoutManager(this));
        postingsAdapter = new PosterAdapter(new ArrayList<>());
        recyclerViewMyPosts.setAdapter(postingsAdapter);
        
        recyclerViewMyRequests = findViewById(R.id.recyclerViewMyRequests);
        recyclerViewMyRequests.setLayoutManager(new LinearLayoutManager(this));
        myRequestAdapter = new MyRequestAdapter(new ArrayList<>());
        recyclerViewMyRequests.setAdapter(myRequestAdapter);

        displayMyPosts();
        displayMyRequests();

        textViewName = findViewById(R.id.textViewName);
        textViewGWID = findViewById(R.id.textViewGWID);
        textViewEmail = findViewById(R.id.textViewEmail);
        textViewDOB = findViewById(R.id.textViewDOB);
        textViewPhone = findViewById(R.id.textViewPhone);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            userReference = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());
            userReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
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

    private void displayMyRequests() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("requests");
            Query requestsQuery = databaseReference.orderByChild("requestedBy/email").equalTo(user.getEmail());
            requestsQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<Request> myRequests = new ArrayList<>();
                    for (DataSnapshot requestSnapshot : dataSnapshot.getChildren()) {
                        Request request = requestSnapshot.getValue(Request.class);
                        if (request != null) {
                            myRequests.add(request);
                        }
                    }
                    myRequestAdapter.setRequests(myRequests);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });


        }

    }

    private void displayMyPosts() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            Query postsQuery = databaseReference.orderByChild("postedBy").equalTo(user.getEmail());
            postsQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<Post> myPosts = new ArrayList<>();
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
                }
            });
        }
    }
}