package com.example.gwtalenttrade;

public class Posting {
    private String title;
    private String description;
    private String category;

    // Constructor
    public Posting(String title, String description, String category) {
        this.title = title;
        this.description = description;
        this.category = category;
    }

    // Getter methods
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }
}
