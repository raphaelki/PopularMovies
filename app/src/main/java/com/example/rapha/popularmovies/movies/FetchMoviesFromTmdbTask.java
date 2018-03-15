package com.example.rapha.popularmovies.movies;

import android.content.Context;
import android.os.AsyncTask;

import com.example.rapha.popularmovies.BuildConfig;
import com.example.rapha.popularmovies.R;
import com.example.rapha.popularmovies.data.Movie;
import com.example.rapha.popularmovies.data.MovieList;
import com.example.rapha.popularmovies.data.TmdbApiService;
import com.example.rapha.popularmovies.listener.AsyncTaskListener;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FetchMoviesFromTmdbTask extends AsyncTask<String, Void, List<Movie>> {

    private AsyncTaskListener<List<Movie>> completionListener;
    private Context context;
    private TmdbApiService tmdbApiService;

    public FetchMoviesFromTmdbTask(Context context, AsyncTaskListener<List<Movie>> completionListener) {
        this.completionListener = completionListener;
        this.context = context;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(context.getString(R.string.tmdb_base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        tmdbApiService = retrofit.create(TmdbApiService.class);
    }

    @Override
    protected void onPostExecute(List<Movie> movies) {
        completionListener.onCompletion(movies);
    }

    @Override
    protected List<Movie> doInBackground(String... strings) {
        int pageToLoad = Integer.parseInt(strings[0]);
        String sortOrder = strings[1];
        Call<MovieList> call;
        switch (sortOrder) {
            case MoviesFragment.POPULAR_PATH:
                call = tmdbApiService.getPopularMovies(BuildConfig.TMDB_API_KEY, pageToLoad, context.getString(R.string.query_localization));
                break;
            case MoviesFragment.TOP_RATED_PATH:
                call = tmdbApiService.getTopRatedMovies(BuildConfig.TMDB_API_KEY, pageToLoad, context.getString(R.string.query_localization));
                break;
            default:
                throw new UnsupportedOperationException("Unknown sort criteria: " + sortOrder);
        }
        try {
            MovieList movieList = call.execute().body();
            return movieList.getMovies();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
