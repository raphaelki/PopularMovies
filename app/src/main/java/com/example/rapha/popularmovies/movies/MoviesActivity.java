package com.example.rapha.popularmovies.movies;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.rapha.popularmovies.R;
import com.example.rapha.popularmovies.data.Movie;
import com.example.rapha.popularmovies.utils.MovieDbNetworkUtils;

import java.io.IOException;
import java.util.List;

public class MoviesActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_overview);
        new MovieDbQueryTask().execute(getString(R.string.api_key));
    }

    class MovieDbQueryTask extends AsyncTask<String, Void, List<Movie>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            super.onPostExecute(movies);
        }

        @Override
        protected List<Movie> doInBackground(String... strings) {
            String apiKey = strings[0];
            try {
                String jsonData = MovieDbNetworkUtils.fetchPopularMovies(apiKey);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
