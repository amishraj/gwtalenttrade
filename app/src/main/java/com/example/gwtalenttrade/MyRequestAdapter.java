package com.example.gwtalenttrade;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

public class MyRequestAdapter extends RecyclerView.Adapter<MyRequestAdapter.RequestViewHolder> {

    private List<Request> requests; // Posting is a custom data class

    // Constructor to set the data
    public MyRequestAdapter(List<Request> requests) {
        this.requests = requests;
    }
    // Setter method for updating data
    public void setRequests(List<Request> requests) {
        this.requests = requests;
        notifyDataSetChanged(); // Notify the adapter that the data has changed
    }

    // ViewHolder class
    public static class RequestViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewPostTitle, textViewPostDescription, textViewPostCategory, textViewRequestedBy, textViewRequestStatus;
        public Button btnContact, btnCancel;

        public RequestViewHolder(View itemView) {
            super(itemView);
            textViewPostTitle = itemView.findViewById(R.id.textViewPostTitle);
            textViewPostDescription = itemView.findViewById(R.id.textViewPostDescription);
            textViewPostCategory = itemView.findViewById(R.id.textViewPostCategory);
            textViewRequestedBy= itemView.findViewById(R.id.textViewRequestedBy);
            textViewRequestStatus= itemView.findViewById(R.id.textViewRequestStatus);
            btnContact= itemView.findViewById(R.id.btnContact);
            btnCancel= itemView.findViewById(R.id.btnCancel);

        }
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_myrequest, parent, false);
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

        holder.btnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String requestStatus = request.getStatus();

                if ("open".equals(requestStatus)) {
                    // Request is still pending
                    Toast.makeText(v.getContext(), "The request is still pending.", Toast.LENGTH_SHORT).show();
                } else if ("accepted".equals(requestStatus)) {
                    // Request is accepted, get the contact method from the post object
                    String contactMethod = request.getPost().getContactMethod();

                    // Show a popup based on the contact method
                    if ("Email".equals(contactMethod)) {
                        // Show a popup with the poster's email
                        showContactPopup(v.getContext(), "Email", request.getPostedBy().getEmail());
                    } else if ("Phone".equals(contactMethod)) {
                        // Show a popup with the poster's phone number
                        showContactPopup(v.getContext(), "Phone", request.getPostedBy().getPhone());
                    } else if ("Both".equals(contactMethod)) {
                        // Show a popup with both options
                        showContactPopup(v.getContext(), "Email", request.getPostedBy().getEmail(), "Phone", request.getPostedBy().getPhone());
                    }
                }
            }
        });

        holder.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            // Assuming request.getId() returns the unique identifier of the request in the database
            String requestId = request.getId();

            // Get a reference to the "requests" node in the database
            DatabaseReference requestsReference = FirebaseDatabase.getInstance().getReference("requests");

            // Remove the request from the database
            requestsReference.child(requestId).removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Request declined successfully
                            Toast.makeText(v.getContext(), "Request canceled", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle the failure to decline the request
                            Toast.makeText(v.getContext(), "Failed to cancel request", Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        });

    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    private void showContactPopup(Context context, String method1, String value1, String method2, String value2) {
        // Use AlertDialog to display contact options
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Contact Options");

        // Inflate the layout for the dialog
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_contact_options, null);
        builder.setView(dialogView);

        // Set the values in the dialog
        TextView textViewMethod1 = dialogView.findViewById(R.id.textViewMethod1);
        TextView textViewValue1 = dialogView.findViewById(R.id.textViewValue1);
        TextView textViewMethod2 = dialogView.findViewById(R.id.textViewMethod2);
        TextView textViewValue2 = dialogView.findViewById(R.id.textViewValue2);

        textViewMethod1.setText(method1);
        textViewValue1.setText(value1);
        textViewMethod2.setText(method2);
        textViewValue2.setText(value2);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle OK button click if needed
                dialog.dismiss();
            }
        });

        // Show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showContactPopup(Context context, String method, String value) {
        // Use AlertDialog to display contact option
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Contact Option");

        // Inflate the layout for the dialog
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_contact_option, null);
        builder.setView(dialogView);

        // Set the values in the dialog
        TextView textViewMethod = dialogView.findViewById(R.id.textViewMethod);
        TextView textViewValue = dialogView.findViewById(R.id.textViewValue);

        textViewMethod.setText(method);
        textViewValue.setText(value);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle OK button click if needed
                dialog.dismiss();
            }
        });

        // Show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
