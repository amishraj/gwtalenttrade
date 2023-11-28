package com.example.gwtalenttrade;

public class Request {

    private String id;
    private Post post;
    private User requestedBy;
    private User postedBy;
    private String status;

    public Request(Post post, User requestedBy, User postedBy, String status) {
        this.post = post;
        this.requestedBy = requestedBy;
        this.postedBy = postedBy;
        this.status = status;
    }

    public Request() {

    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public User getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(User requestedBy) {
        this.requestedBy = requestedBy;
    }

    public User getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(User postedBy) {
        this.postedBy = postedBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Request{" +
                "post=" + post +
                ", requestedBy=" + requestedBy +
                ", postedBy=" + postedBy +
                ", status='" + status + '\'' +
                '}';
    }
}
