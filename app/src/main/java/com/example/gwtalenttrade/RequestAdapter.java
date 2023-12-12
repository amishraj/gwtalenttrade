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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {

    // List to hold request data
    private List<Request> requests;

    // Constructor
    public RequestAdapter(List<Request> requests) {
        this.requests = requests;
    }

    // Update requests list and notify adapter
    public void setRequests(List<Request> requests) {
        this.requests = requests;
        notifyDataSetChanged();
    }

    // ViewHolder class for request items
    public static class RequestViewHolder extends RecyclerView.ViewHolder {
        // UI components for displaying request information
        public TextView textViewPostTitle, textViewPostDescription, textViewPostCategory, textViewRequestedBy, textViewRequestStatus;
        public Button buttonAccept;
        public Button buttonDecline;

        public RequestViewHolder(View itemView) {
            super(itemView);
            // Initialize UI components
            textViewPostTitle = itemView.findViewById(R.id.textViewPostTitle);
            textViewPostDescription = itemView.findViewById(R.id.textViewPostDescription);
            textViewPostCategory = itemView.findViewById(R.id.textViewPostCategory);
            textViewRequestedBy= itemView.findViewById(R.id.textViewRequestedBy);
            textViewRequestStatus= itemView.findViewById(R.id.textViewRequestStatus);
            buttonAccept= itemView.findViewById(R.id.buttonAccept);
            buttonDecline= itemView.findViewById(R.id.buttonDecline);

        }
    }

    // Inflates the item layout and creates the ViewHolder
    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_request, parent, false);
        return new RequestViewHolder(view);
    }

    // Binds data to each item in the RecyclerView
    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        Request request = requests.get(position);
        // Set request details in ViewHolder
        holder.textViewPostTitle.setText(request.getPost().getTitle());
        holder.textViewPostDescription.setText(request.getPost().getDescription());
        holder.textViewPostCategory.setText(request.getPost().getCategory());
        holder.textViewRequestedBy.setText(request.getRequestedBy().fullName+" "+request.getRequestedBy().email);
        holder.textViewRequestStatus.setText(request.getStatus().toUpperCase());

        // Set onClickListener for accept button
        holder.buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //fetch request ID and current status of the request
                String requestId = request.getId();
                String currentStatus = request.getStatus();

                //if request is already accepted, notify the user
                if ("accepted".equals(currentStatus)) {
                    Toast.makeText(v.getContext(), "You have already accepted this request", Toast.LENGTH_SHORT).show();
                } else if ("open".equals(currentStatus)) {
                    //if status is open, accept that request
                    //find the reference to the request and then set the status value to "accepted"
                    DatabaseReference requestsReference = FirebaseDatabase.getInstance().getReference("requests");
                    requestsReference.child(requestId).child("status").setValue("accepted")
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                //on success, notify the user
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(v.getContext(), "Request accepted", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                //on failure, handle the error case
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(v.getContext(), "Failed to accept request", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        // Set onClickListener for decline button
        holder.buttonDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //similar to accept case
            String requestId = request.getId();
            String currentStatus = request.getStatus();
            if ("accepted".equals(currentStatus)) {
                Toast.makeText(v.getContext(), "You have already accepted this request", Toast.LENGTH_SHORT).show();
                return;
            }

            //if request is declined, get the reference to the request an delete it
            DatabaseReference requestsReference = FirebaseDatabase.getInstance().getReference("requests");
            requestsReference.child(requestId).removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        //on delete success, notify the user
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(v.getContext(), "Request declined", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        //on failure, handle the error case
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(v.getContext(), "Failed to decline request", Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        });


    }

    // Returns the total number of items in the list
    @Override
    public int getItemCount() {
        return requests.size();
    }
}
