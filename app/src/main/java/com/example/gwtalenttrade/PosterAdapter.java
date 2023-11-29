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
    private List<Post> posts;

    public PosterAdapter(List<Post> posts) {
        this.posts = posts;
    }
    public void setPostings(List<Post> posts) {
        this.posts = posts;
        notifyDataSetChanged();
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

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Edit post: " + post.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        holder.btnManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent manageRequestsIntent = new Intent(v.getContext(), ManageRequests.class);
                manageRequestsIntent.putExtra("postId", post.getPostId());
                v.getContext().startActivity(manageRequestsIntent);
            }
        });


    }

    private void getNumberOfRequests(String postId, final TextView textView) {
        DatabaseReference requestsReference = FirebaseDatabase.getInstance().getReference("requests");
        Query requestsQuery = requestsReference.orderByChild("post/postId").equalTo(postId);
        requestsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long numberOfRequests = dataSnapshot.getChildrenCount();
                textView.setText(String.valueOf(numberOfRequests));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    @Override
    public int getItemCount() {
        return posts.size();
    }
}
