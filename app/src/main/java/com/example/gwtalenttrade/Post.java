package com.example.gwtalenttrade;

public class Post {
    private String postId;
    private String title;
    private String category;
    private String description;
    private String contactMethod;

    private String postedBy;

    // Empty constructor for Firebase
    public Post() {
    }

    public Post(String title, String category, String description, String contactMethod, String postedBy) {
        this.title = title;
        this.category = category;
        this.description = description;
        this.contactMethod = contactMethod;
        this.postedBy= postedBy;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContactMethod() {
        return contactMethod;
    }

    public void setContactMethod(String contactMethod) {
        this.contactMethod = contactMethod;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public String getPostId() {
        return postId;
    }

    public void setId(String id){
        this.postId=id;
    }

}
