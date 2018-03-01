package com.example.rapha.popularmovies.data;

public class Movie {

    private String title;
    private String posterURL;
    private String originalTitle;
    private String plot;
    private String releaseDate;
    private String userRating;

    public Movie(String title, String posterURL, String originalTitle, String plot, String releaseDate, String userRating) {
        this.title = title;
        this.posterURL = posterURL;
        this.originalTitle = originalTitle;
        this.plot = plot;
        this.releaseDate = releaseDate;
        this.userRating = userRating;
    }
}
