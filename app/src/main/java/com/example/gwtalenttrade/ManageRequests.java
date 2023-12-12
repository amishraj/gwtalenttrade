package com.example.gwtalenttrade;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ManageRequests extends AppCompatActivity {

    // UI component to display list of requests
    private RecyclerView recyclerViewRequests;
    private RequestAdapter requestsAdapter;
    private List<Request> requestsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_requests);

        // Retrieve postId passed from the previous activity
        String postId = getIntent().getStringExtra("postId");

        // Setting up RecyclerView and its adapter to display requests
        recyclerViewRequests = findViewById(R.id.recyclerViewRequests);
        recyclerViewRequests.setLayoutManager(new LinearLayoutManager(this));
        requestsList = new ArrayList<>();
        requestsAdapter = new RequestAdapter(requestsList);
        recyclerViewRequests.setAdapter(requestsAdapter);

        // Fetch and display requests related to the postId
        displayRequests(postId);
    }

    // Method to fetch and display requests from Firebase database
    private void displayRequests(String postId) {
        // Reference to 'requests' node in Firebase database
        DatabaseReference requestsReference = FirebaseDatabase.getInstance().getReference("requests");

        // Query to find requests matching the given postId
        Query requestsQuery = requestsReference.orderByChild("post/postId").equalTo(postId);
        requestsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Clear existing data in the list
                requestsList.clear();
                // Iterate through each request and add to the list
                for (DataSnapshot requestSnapshot : dataSnapshot.getChildren()) {
                    Request request = requestSnapshot.getValue(Request.class);
                    if (request != null) {
                        requestsList.add(request);

                    }
                }
                // Notify the adapter to update the UI with new data
                requestsAdapter.notifyDataSetChanged();
            }

            //error handling
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ManageRequests", "Failed to fetch requests: " + databaseError.getMessage());
            }
        });
    }
}