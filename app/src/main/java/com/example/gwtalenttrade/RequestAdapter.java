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

    private List<Request> requests;
    public RequestAdapter(List<Request> requests) {
        this.requests = requests;
    }
    public void setRequests(List<Request> requests) {
        this.requests = requests;
        notifyDataSetChanged();
    }

    // ViewHolder class
    public static class RequestViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewPostTitle, textViewPostDescription, textViewPostCategory, textViewRequestedBy, textViewRequestStatus;
        public Button buttonAccept;
        public Button buttonDecline;

        public RequestViewHolder(View itemView) {
            super(itemView);
            textViewPostTitle = itemView.findViewById(R.id.textViewPostTitle);
            textViewPostDescription = itemView.findViewById(R.id.textViewPostDescription);
            textViewPostCategory = itemView.findViewById(R.id.textViewPostCategory);
            textViewRequestedBy= itemView.findViewById(R.id.textViewRequestedBy);
            textViewRequestStatus= itemView.findViewById(R.id.textViewRequestStatus);
            buttonAccept= itemView.findViewById(R.id.buttonAccept);
            buttonDecline= itemView.findViewById(R.id.buttonDecline);

        }
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_request, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        Request request = requests.get(position);
        holder.textViewPostTitle.setText(request.getPost().getTitle());
        holder.textViewPostDescription.setText(request.getPost().getDescription());
        holder.textViewPostCategory.setText(request.getPost().getCategory());
        holder.textViewRequestedBy.setText(request.getRequestedBy().fullName+" "+request.getRequestedBy().email);
        holder.textViewRequestStatus.setText(request.getStatus().toUpperCase());

        holder.buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String requestId = request.getId();
                String currentStatus = request.getStatus();

                if ("accepted".equals(currentStatus)) {
                    Toast.makeText(v.getContext(), "You have already accepted this request", Toast.LENGTH_SHORT).show();
                } else if ("open".equals(currentStatus)) {
                    DatabaseReference requestsReference = FirebaseDatabase.getInstance().getReference("requests");
                    requestsReference.child(requestId).child("status").setValue("accepted")
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(v.getContext(), "Request accepted", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(v.getContext(), "Failed to accept request", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        holder.buttonDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            String requestId = request.getId();
            String currentStatus = request.getStatus();
            if ("accepted".equals(currentStatus)) {
                Toast.makeText(v.getContext(), "You have already accepted this request", Toast.LENGTH_SHORT).show();
                return;
            }

            DatabaseReference requestsReference = FirebaseDatabase.getInstance().getReference("requests");
            requestsReference.child(requestId).removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(v.getContext(), "Request declined", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(v.getContext(), "Failed to decline request", Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        });


    }

    @Override
    public int getItemCount() {
        return requests.size();
    }
}
