package com.example.gwtalenttrade;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class createPost extends AppCompatActivity {

    private EditText editTextTitle, editTextOtherCategory, editTextDescription;
    private Spinner categorySpinner;
    private RadioGroup radioGroupContact;
    private Button btnPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextOtherCategory = findViewById(R.id.editTextOtherCategory);
        editTextDescription = findViewById(R.id.editTextDescription);
        categorySpinner = findViewById(R.id.categorySpinner);
        radioGroupContact = findViewById(R.id.radioGroupContact);
        btnPost = findViewById(R.id.btnPost);

        // Populate the spinner with categories
        List<String> categories = new ArrayList<>();
        categories.add("Category 1");
        categories.add("Category 2");
        categories.add("Category 3");
        // Add more categories as needed

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        // Set listener for category selection
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Show/hide the "Other" category field based on the selected category
                String selectedCategory = (String) parentView.getItemAtPosition(position);
                if ("Other".equals(selectedCategory)) {
                    editTextOtherCategory.setVisibility(View.VISIBLE);
                } else {
                    editTextOtherCategory.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });

        // Set listener for the "Post" button
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Collect data and handle the post action
                String title = editTextTitle.getText().toString();
                String category = categorySpinner.getSelectedItem().toString();
                if ("Other".equals(category)) {
                    category = editTextOtherCategory.getText().toString();
                }
                String description = editTextDescription.getText().toString();
                String contactMethod = getSelectedContactMethod();

                // Handle the post action (for now, just display a toast with the collected data)
                String postData = "Title: " + title +
                        "\nCategory: " + category +
                        "\nDescription: " + description +
                        "\nContact Method: " + contactMethod;

                Toast.makeText(createPost.this, postData, Toast.LENGTH_LONG).show();
            }
        });
    }

    // Helper method to get the selected contact method from radio buttons
    private String getSelectedContactMethod() {
        int selectedRadioButtonId = radioGroupContact.getCheckedRadioButtonId();

        if (selectedRadioButtonId != -1) {
            RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
            return selectedRadioButton.getText().toString();
        }

        return "";
    }
}
