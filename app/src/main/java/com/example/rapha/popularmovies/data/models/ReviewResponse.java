package com.example.rapha.popularmovies.data.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReviewResponse {

    @SerializedName("id")
    private Integer id;
    @SerializedName("page")
    private Integer page;
    @SerializedName("results")
    private List<Review> reviews = null;
    @SerializedName("total_pages")
    private Integer totalPages;
    @SerializedName("total_results")
    private Integer totalReviews;

    public Integer getId() {
        return id;
    }

    public Integer getPage() {
        return page;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public Integer getTotalReviews() {
        return totalReviews;
    }
}