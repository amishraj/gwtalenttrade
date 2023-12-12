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

    // List to hold post data and poster to hold poster information
    private List<Post> posts;
    private User poster;

    // Constructor
    public PostingsAdapter(List<Post> posts) {
        this.posts = posts;
    }

    // Update posts list and notify adapter
    public void setPostings(List<Post> posts) {
        this.posts = posts;
        notifyDataSetChanged();
    }

    // Getter for posts list
    public List<Post> getPostings() {
        return this.posts;
    }

    // ViewHolder class for post items
    public static class PostingViewHolder extends RecyclerView.ViewHolder {
        // UI components for displaying post information
        public TextView titleTextView, descriptionTextView, categoryTextView;
        public Button contactButton;

        public PostingViewHolder(View itemView) {
            super(itemView);
            // Initialize UI components
            titleTextView = itemView.findViewById(R.id.textViewTitle);
            descriptionTextView = itemView.findViewById(R.id.textViewDescription);
            categoryTextView = itemView.findViewById(R.id.textViewCategory);
            contactButton = itemView.findViewById(R.id.buttonContact);
        }
    }

    // Inflates the item layout and creates the ViewHolder
    @NonNull
    @Override
    public PostingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_posting, parent, false);
        return new PostingViewHolder(view);
    }

    // Binds data to each item in the RecyclerView
    @Override
    public void onBindViewHolder(@NonNull PostingViewHolder holder, int position) {
        Post post = posts.get(position);
        // Set post details in ViewHolder
        holder.titleTextView.setText(post.getTitle());
        holder.descriptionTextView.setText(post.getDescription());
        holder.categoryTextView.setText(post.getCategory());

        // Set onClickListener for contact button
        holder.contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Logic to handle request creation and submission to Firebase
                Request request = new Request();
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                //if user exists
                if (currentUser != null) {
                    //get db reference and using that get the user reference
                    DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());
                    userReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            if (user != null) {

                                //set the requested by and post fields of the request
                                request.setRequestedBy(user);
                                request.setPost(post);
                                DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("users");

                                //query to retrieve the posted by user based on email
                                Query userQuery = usersReference.orderByChild("email").equalTo(post.getPostedBy());
                                userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                            User user = userSnapshot.getValue(User.class);

                                            //update the posted by property of the request if user exists
                                            if (user != null) {
                                                request.setPostedBy(user);

                                                //deny user if they try access their own service
                                                //comparing requested by and posted by users
                                                if(user.equals(request.getRequestedBy())){
                                                    Toast.makeText(v.getContext(), "Cannot request your own service: " + post.getTitle(), Toast.LENGTH_SHORT).show();
                                                    return;
                                                }

                                                //status of the request=open
                                                request.setStatus("open");

                                                //get unique key and store the request in the requestsReference
                                                DatabaseReference requestsReference = FirebaseDatabase.getInstance().getReference("requests");
                                                String requestId = requestsReference.push().getKey();
                                                request.setId(requestId);
                                                requestsReference.child(requestId).setValue(request);

                                                Toast.makeText(v.getContext(), "Request sent for service: " + post.getTitle(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }

                                    //error handling
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });
                            }
                        }

                        //error handling
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }

                    });
                }
            }
        });
    }

    // Method to fetch user information for a post
    private void getUser(Post post) {
        DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("users");

        Query userQuery = usersReference.orderByChild("email").equalTo(post.getPostedBy());
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Logic to extract user information from Firebase
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (user != null) {
                       poster = user;
                    }
                }
            }

            //error handling
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    // Returns the total number of items in the list
    @Override
    public int getItemCount() {
        return posts.size();
    }
}
