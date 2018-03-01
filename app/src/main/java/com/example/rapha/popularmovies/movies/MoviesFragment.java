package com.example.rapha.popularmovies.movies;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rapha.popularmovies.R;
import com.example.rapha.popularmovies.data.Movie;
import com.example.rapha.popularmovies.utils.MovieDbJsonUtils;
import com.example.rapha.popularmovies.utils.MovieDbNetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

public class MoviesFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();

    public MoviesFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_movies, container, false);

        new MovieDbQueryTask().execute(getString(R.string.api_key));

        return view;
    }

    class MovieDbQueryTask extends AsyncTask<String, Void, List<Movie>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            for (Movie movie : movies) {
                Log.d(TAG, movie.getTitle());
            }
        }

        @Override
        protected List<Movie> doInBackground(String... strings) {
            String apiKey = strings[0];
            try {
                String jsonData = MovieDbNetworkUtils.fetchPopularMovies(apiKey);
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
}
