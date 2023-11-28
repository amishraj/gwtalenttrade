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

    private List<Post> posts; // Posting is a custom data class

    // Constructor to set the data
    public PosterAdapter(List<Post> posts) {
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
        public Button btnEdit;
        public Button btnDelete;
        public TextView requestsCountLabel;

        public Button btnManage;

        public PostingViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.textViewTitle);
            descriptionTextView = itemView.findViewById(R.id.textViewDescription);
            categoryTextView = itemView.findViewById(R.id.textViewCategory);
            requestsCountLabel= itemView.findViewById(R.id.requestsCountLabel);
            btnEdit = itemView.findViewById(R.id.buttonEdit);
            btnDelete= itemView.findViewById(R.id.buttonDelete);
            btnManage= itemView.findViewById(R.id.buttonManage);
        }
    }

    @NonNull
    @Override
    public PostingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_poster, parent, false);
        return new PostingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostingViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.titleTextView.setText(post.getTitle());
        holder.descriptionTextView.setText(post.getDescription());
        holder.categoryTextView.setText(post.getCategory());
        
        getNumberOfRequests(post.getPostId(),holder.requestsCountLabel);

        // Set OnClickListener for the Contact Button
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle contact button click
                // You can implement the logic to contact the poster
                Toast.makeText(v.getContext(), "Edit post: " + post.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("posts");

                // Assuming post.getId() returns the unique identifier of the post in the database
                String postId = post.getPostId();

                // Remove the post from the database
                databaseReference.child(postId).removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Post deleted successfully
                                Toast.makeText(v.getContext(), "Post deleted: " + post.getTitle(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle the failure to delete the post
                                Toast.makeText(v.getContext(), "Failed to delete post", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        holder.btnManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the ManageRequests activity
                Intent manageRequestsIntent = new Intent(v.getContext(), ManageRequests.class);

                // Pass the postId to the ManageRequests activity
                manageRequestsIntent.putExtra("postId", post.getPostId());

                // Start the ManageRequests activity
                v.getContext().startActivity(manageRequestsIntent);
            }
        });


    }

    private void getNumberOfRequests(String postId, final TextView textView) {
        DatabaseReference requestsReference = FirebaseDatabase.getInstance().getReference("requests");

        // Query the database for requests related to the specified post
        Query requestsQuery = requestsReference.orderByChild("post/postId").equalTo(postId);
        requestsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get the count of requests
                long numberOfRequests = dataSnapshot.getChildrenCount();

                // Set the count to the TextView
                textView.setText(String.valueOf(numberOfRequests));
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
