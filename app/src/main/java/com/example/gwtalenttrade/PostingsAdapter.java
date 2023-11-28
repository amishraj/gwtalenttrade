package com.example.gwtalenttrade;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PostingsAdapter extends RecyclerView.Adapter<PostingsAdapter.PostingViewHolder> {

    private List<Post> posts; // Posting is a custom data class

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
        holder.categoryTextView.setText("Category: " + post.getCategory());

        // Set OnClickListener for the Contact Button
        holder.contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle contact button click
                // You can implement the logic to contact the poster
                Toast.makeText(v.getContext(), "Contact button clicked for " + post.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}
