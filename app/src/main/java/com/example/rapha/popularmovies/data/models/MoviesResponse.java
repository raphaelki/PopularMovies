package com.example.rapha.popularmovies.data.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MoviesResponse {

    @SerializedName("page")
    private Integer page = 0;
    @SerializedName("results")
    private List<Movie> movies;

    public Integer getPage() {
        return page;
    }

    public List<Movie> getMovies() {
        return movies;
    }
}
