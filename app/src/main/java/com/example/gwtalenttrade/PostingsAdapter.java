package com.example.gwtalenttrade;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class PostingsAdapter extends RecyclerView.Adapter<PostingsAdapter.PostingViewHolder> {

    private List<Post> posts; // Posting is a custom data class
    private User poster;

    // Constructor to set the data
    public PostingsAdapter(List<Post> posts) {
        this.posts = posts;
    }
    // Setter method for updating data
    public void setPostings(List<Post> posts) {
        this.posts = posts;
        notifyDataSetChanged(); // Notify the adapter that the data has changed
    }


    // ViewHolder class
    public static class PostingViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView, descriptionTextView, categoryTextView;
        public Button contactButton;

        public PostingViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.textViewTitle);
            descriptionTextView = itemView.findViewById(R.id.textViewDescription);
            categoryTextView = itemView.findViewById(R.id.textViewCategory);
            contactButton = itemView.findViewById(R.id.buttonContact);
        }
    }

    @NonNull
    @Override
    public PostingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_posting, parent, false);
        return new PostingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostingViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.titleTextView.setText(post.getTitle());
        holder.descriptionTextView.setText(post.getDescription());
        holder.categoryTextView.setText(post.getCategory());

        // Set OnClickListener for the Contact Button
        holder.contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // REQUEST button click
                Request request = new Request();
                //get current user

                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                if (currentUser != null) {
                    // Initialize the Realtime Database reference
                    DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());

                    // Read from the database
                    userReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // Retrieve user data from the snapshot
                            User user = dataSnapshot.getValue(User.class);

                            // Check if the user data is not null
                            if (user != null) {
                                // Set user data to the views
                                request.setRequestedBy(user);
                                request.setPost(post);  // Assuming post is the current post being viewed
                                DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("users");

                                // Query the database for the user based on email
                                Query userQuery = usersReference.orderByChild("email").equalTo(post.getPostedBy());
                                userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        // Assuming there's only one user with the given email
                                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                            User user = userSnapshot.getValue(User.class);

                                            // Check if the user data is not null
                                            if (user != null) {
                                                request.setPostedBy(user);  // Assuming getUser() returns the user who posted the post
                                                if(user.equals(request.getRequestedBy())){
                                                    Toast.makeText(v.getContext(), "Cannot request your own service: " + post.getTitle(), Toast.LENGTH_SHORT).show();
                                                    return;
                                                }
                                                request.setStatus("open");

                                                DatabaseReference requestsReference = FirebaseDatabase.getInstance().getReference("requests");
                                                String requestId = requestsReference.push().getKey(); // Get a unique key for the request
                                                request.setId(requestId);
                                                requestsReference.child(requestId).setValue(request);

                                                Toast.makeText(v.getContext(), "Request sent for service: " + post.getTitle(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        // Handle database error
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }

                    });
                }
            }
        });
    }

    private void getUser(Post post) {
        DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("users");

        // Query the database for the user based on email
        Query userQuery = usersReference.orderByChild("email").equalTo(post.getPostedBy());
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Assuming there's only one user with the given email
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);

                    // Check if the user data is not null
                    if (user != null) {
                       poster = user;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}
