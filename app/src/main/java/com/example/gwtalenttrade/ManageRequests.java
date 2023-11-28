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

    private RecyclerView recyclerViewRequests;
    private RequestAdapter requestsAdapter;
    private List<Request> requestsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_requests);

        // Get the postId from the Intent
        String postId = getIntent().getStringExtra("postId");

        recyclerViewRequests = findViewById(R.id.recyclerViewRequests);
        recyclerViewRequests.setLayoutManager(new LinearLayoutManager(this));
        requestsList = new ArrayList<>();
        requestsAdapter = new RequestAdapter(requestsList);
        recyclerViewRequests.setAdapter(requestsAdapter);

        displayRequests(postId);
    }

    private void displayRequests(String postId) {
        DatabaseReference requestsReference = FirebaseDatabase.getInstance().getReference("requests");

        Query requestsQuery = requestsReference.orderByChild("post/postId").equalTo(postId);
        requestsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                requestsList.clear();
                for (DataSnapshot requestSnapshot : dataSnapshot.getChildren()) {
                    Request request = requestSnapshot.getValue(Request.class);
                    if (request != null) {
                        requestsList.add(request);

                    }
                }
                requestsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
                Log.e("ManageRequests", "Failed to fetch requests: " + databaseError.getMessage());
            }
        });
    }
}