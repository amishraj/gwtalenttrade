package com.example.gwtalenttrade;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class PosterAdapter extends RecyclerView.Adapter<PosterAdapter.PostingViewHolder> {
    // List to hold post data
    private List<Post> posts;

    // Constructor
    public PosterAdapter(List<Post> posts) {
        this.posts = posts;
    }

    // Update posts list and notify adapter
    public void setPostings(List<Post> posts) {
        this.posts = posts;
        notifyDataSetChanged();
    }


    // ViewHolder class for post items
    public static class PostingViewHolder extends RecyclerView.ViewHolder {
        // UI components for displaying post information
        public TextView titleTextView, descriptionTextView, categoryTextView;
        public Button btnEdit;
        public Button btnDelete;
        public TextView requestsCountLabel;

        public Button btnManage;

        public PostingViewHolder(View itemView) {
            super(itemView);
            // Initialize UI components
            titleTextView = itemView.findViewById(R.id.textViewTitle);
            descriptionTextView = itemView.findViewById(R.id.textViewDescription);
            categoryTextView = itemView.findViewById(R.id.textViewCategory);
            requestsCountLabel= itemView.findViewById(R.id.requestsCountLabel);
            btnDelete= itemView.findViewById(R.id.buttonDelete);
            btnManage= itemView.findViewById(R.id.buttonManage);
        }
    }

    // Inflates the item layout and creates the ViewHolder
    @NonNull
    @Override
    public PostingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_poster, parent, false);
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

        // Get number of requests for each post
        getNumberOfRequests(post.getPostId(),holder.requestsCountLabel);

        // Set onClickListener for delete button
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Logic to delete a post from Firebase database based on the postId
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("posts");
                String postId = post.getPostId();
                databaseReference.child(postId).removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(v.getContext(), "Post deleted: " + post.getTitle(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(v.getContext(), "Failed to delete post", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        // Set onClickListener for manage requests button
        holder.btnManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Logic to navigate to ManageRequests activity
                Intent manageRequestsIntent = new Intent(v.getContext(), ManageRequests.class);
                manageRequestsIntent.putExtra("postId", post.getPostId());
                v.getContext().startActivity(manageRequestsIntent);
            }
        });


    }

    // Method to get the number of requests for a post
    private void getNumberOfRequests(String postId, final TextView textView) {
        DatabaseReference requestsReference = FirebaseDatabase.getInstance().getReference("requests");
        Query requestsQuery = requestsReference.orderByChild("post/postId").equalTo(postId);
        requestsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long numberOfRequests = dataSnapshot.getChildrenCount();
                textView.setText(String.valueOf(numberOfRequests));
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
