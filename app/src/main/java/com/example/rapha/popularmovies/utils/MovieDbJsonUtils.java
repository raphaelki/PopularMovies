package com.example.rapha.popularmovies.utils;

import com.example.rapha.popularmovies.data.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MovieDbJsonUtils {

    private static final String RESULTS = "results";
    private static final String TITLE = "title";
    private static final String POSTER_PATH = "poster_path";
    private static final String PLOT = "overview";
    private static final String USER_RATING = "vote_average";
    private static final String ORIGINAL_TITLE = "original_title";
    private static final String RELEASE_DATE = "release_date";

    private static final String BASE_IMAGE_URL = "https://image.tmdb.org/t/p/w185";

    public static List<Movie> parseMovieDbJson(String json) throws JSONException {
        JSONObject jsonResponse = new JSONObject(json);
        JSONArray moviesJson = jsonResponse.optJSONArray(RESULTS);
        ArrayList<Movie> movieList = new ArrayList<>();
        for (int index = 0; index < moviesJson.length(); index++) {
            JSONObject movie = moviesJson.optJSONObject(index);
            String title = movie.getString(TITLE);
            String posterURL = BASE_IMAGE_URL + movie.getString(POSTER_PATH);
            String plot = movie.getString(PLOT);
            String userRating = movie.getString(USER_RATING);
            String originalTitle = movie.getString(ORIGINAL_TITLE);
            String releaseDate = movie.getString(RELEASE_DATE);
            movieList.add(new Movie(title, posterURL, originalTitle, plot, releaseDate, userRating));
        }
        return movieList;
    }
}
