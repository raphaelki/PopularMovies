package com.example.rapha.popularmovies.data.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieList {

    @SerializedName("page")
    private Integer page = 0;
    @SerializedName("results")
    private List<Movie> movies;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }
}
