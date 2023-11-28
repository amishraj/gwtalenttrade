package com.example.gwtalenttrade;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

        public PostingViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.textViewTitle);
            descriptionTextView = itemView.findViewById(R.id.textViewDescription);
            categoryTextView = itemView.findViewById(R.id.textViewCategory);
            btnEdit = itemView.findViewById(R.id.buttonEdit);
            btnDelete= itemView.findViewById(R.id.buttonDelete);
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
        holder.categoryTextView.setText("Category: " + post.getCategory());

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
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}
