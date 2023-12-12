package com.example.gwtalenttrade;

public interface DataFetchCompleteListener {

    //callback interface to make sure category/ search term is applied only if all posts have been fetched first
    // used in Listings.class
    void onDataFetchComplete();
}