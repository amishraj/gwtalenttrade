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

    private List<Request> requests;

    public MyRequestAdapter(List<Request> requests) {
        this.requests = requests;
    }
    public void setRequests(List<Request> requests) {
        this.requests = requests;
        notifyDataSetChanged();
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
                    Toast.makeText(v.getContext(), "The request is still pending.", Toast.LENGTH_SHORT).show();
                } else if ("accepted".equals(requestStatus)) {
                    String contactMethod = request.getPost().getContactMethod();

                    if ("Email".equals(contactMethod)) {
                        showContactPopup(v.getContext(), "Email", request.getPostedBy().getEmail());
                    } else if ("Phone".equals(contactMethod)) {
                        showContactPopup(v.getContext(), "Phone", request.getPostedBy().getPhone());
                    } else if ("Both".equals(contactMethod)) {
                        showContactPopup(v.getContext(), "Email", request.getPostedBy().getEmail(), "Phone", request.getPostedBy().getPhone());
                    }
                }
            }
        });

        holder.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            String requestId = request.getId();

            DatabaseReference requestsReference = FirebaseDatabase.getInstance().getReference("requests");

            requestsReference.child(requestId).removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(v.getContext(), "Request canceled", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
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
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Contact Options");

        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_contact_options, null);
        builder.setView(dialogView);

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
                dialog.dismiss();
            }
        });

        // Show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showContactPopup(Context context, String method, String value) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Contact Option");

        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_contact_option, null);
        builder.setView(dialogView);

        TextView textViewMethod = dialogView.findViewById(R.id.textViewMethod);
        TextView textViewValue = dialogView.findViewById(R.id.textViewValue);

        textViewMethod.setText(method);
        textViewValue.setText(value);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
