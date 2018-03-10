package com.example.rapha.popularmovies.movies;

import android.content.Context;
import android.os.AsyncTask;

import com.example.rapha.popularmovies.BuildConfig;
import com.example.rapha.popularmovies.data.Movie;
import com.example.rapha.popularmovies.listener.AsyncTaskListener;
import com.example.rapha.popularmovies.utils.MovieDbJsonUtils;
import com.example.rapha.popularmovies.utils.MovieDbNetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

public class FetchMoviesFromTmdbTask extends AsyncTask<String, Void, List<Movie>> {

    private final String API_KEY = BuildConfig.TMDB_API_KEY;
    private AsyncTaskListener<List<Movie>> completionListener;
    private Context context;

    public FetchMoviesFromTmdbTask(Context context, AsyncTaskListener<List<Movie>> completionListener) {
        this.completionListener = completionListener;
        this.context = context;
    }

    @Override
    protected void onPostExecute(List<Movie> movies) {
        completionListener.onCompletion(movies);
    }

    @Override
    protected List<Movie> doInBackground(String... strings) {
        String pageToLoad = strings[0];
        String sortOrder = strings[1];
        try {
            String jsonData = MovieDbNetworkUtils.fetchMovies(context, API_KEY, pageToLoad, sortOrder);
            try {
                return MovieDbJsonUtils.parseMovieDbJson(jsonData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
