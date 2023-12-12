package com.example.gwtalenttrade;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class createPost extends AppCompatActivity {

    // UI components
    private EditText editTextTitle, editTextDescription;
    private Spinner categorySpinner;
    private RadioGroup radioGroupContact;
    private Button btnPost;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        // Initialize UI components
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        categorySpinner = findViewById(R.id.categorySpinner);
        radioGroupContact = findViewById(R.id.radioGroupContact);
        btnPost = findViewById(R.id.btnPost);
        user = FirebaseAuth.getInstance().getCurrentUser();

        // Populate category dropdown with options
        List<String> categories = new ArrayList<>();
        categories.add("Tutoring Services");
        categories.add("Homemade Goods and Crafts");
        categories.add("Meal Plans and Food Services");
        categories.add("Carpooling and Transportation");
        categories.add("Sports and Fitness Services");
        categories.add("Miscellaneous Services");

        // Setup ArrayAdapter for dropdown
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        // Set listener for the post button
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Collect data from input fields
                String title = editTextTitle.getText().toString();
                String category = categorySpinner.getSelectedItem().toString();
                String description = editTextDescription.getText().toString();
                String contactMethod = getSelectedContactMethod();
                String postedBy= user.getEmail();

                // Create Post object and save to Firebase
                Post post = new Post(title, category, description, contactMethod, postedBy);

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("posts");

                // Generate unique ID for new post and save to Firebase
                String postId = databaseReference.push().getKey(); // Get a unique key for the post
                post.setId(postId);
                databaseReference.child(postId).setValue(post);

                // Notify user of successful submission
                Toast.makeText(createPost.this, "Post submitted successfully", Toast.LENGTH_SHORT).show();

                // Close activity after submission
                finish();

                // Debugging: Print post details if needed
//                String postData = "Title: " + title +
//                        "\nCategory: " + category +
//                        "\nDescription: " + description +
//                        "\nContact Method: " + contactMethod;
            }
        });
    }

    // Method to get the selected contact method from RadioGroup
    private String getSelectedContactMethod() {
        int selectedRadioButtonId = radioGroupContact.getCheckedRadioButtonId();

        // Return the text of the selected RadioButton
        if (selectedRadioButtonId != -1) {
            RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
            return selectedRadioButton.getText().toString();
        }

        return "";
    }
}
