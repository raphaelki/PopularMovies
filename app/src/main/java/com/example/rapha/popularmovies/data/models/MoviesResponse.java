package com.example.rapha.popularmovies.data.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MoviesResponse {

    @SerializedName("results")
    private List<Movie> movies;

    public Integer getPage() {
        return 0;
    }

    public List<Movie> getMovies() {
        return movies;
    }
}
