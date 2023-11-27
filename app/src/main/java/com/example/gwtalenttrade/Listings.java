package com.example.gwtalenttrade;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import com.example.gwtalenttrade.databinding.ActivityListingsBinding;

import java.util.ArrayList;
import java.util.List;

public class Listings extends AppCompatActivity {

private ActivityListingsBinding binding;
    private RecyclerView recyclerView;
    private PostingsAdapter postingsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityListingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        recyclerView = findViewById(R.id.recyclerView);

        // Set layout manager (HorizontalLinearLayoutManager for horizontal scrolling)
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        // Set up data (Replace with your actual data)
        List<Posting> postings = getPostings(); // Implement this method to fetch your data
        postingsAdapter = new PostingsAdapter(postings);
        recyclerView.setAdapter(postingsAdapter);

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        toolBarLayout.setTitle(getTitle());

        FloatingActionButton fab = binding.fab;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Create your own post here!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                startActivity(new Intent(Listings.this, createPost.class));
            }
        });
    }

    // Method to get sample data (Replace with your actual data fetching logic)
    private List<Posting> getPostings() {
        List<Posting> postings = new ArrayList<>();

        postings.add(new Posting("Job Title 1", "Description for Job 1", "Category 1"));
        postings.add(new Posting("Job Title 2", "Description for Job 2", "Category 2"));
        // Add more postings as needed

        return postings;
    }
}