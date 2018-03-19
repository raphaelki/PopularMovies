package com.example.rapha.popularmovies.services;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import com.example.rapha.popularmovies.BuildConfig;
import com.example.rapha.popularmovies.R;
import com.example.rapha.popularmovies.data.MoviesDatabaseContract;
import com.example.rapha.popularmovies.data.TmdbApiService;
import com.example.rapha.popularmovies.data.models.MovieList;
import com.example.rapha.popularmovies.utils.TmdbUtils;

import java.io.IOException;

import retrofit2.Call;

public class FetcherTasks {

    public static final String TAG = FetcherTasks.class.getSimpleName();

    public static void fetchPopularMovies(TmdbApiService tmdbApiService, Context context, int pageToLoad) {
        String apiKey = BuildConfig.TMDB_API_KEY;
        String localization = context.getString(R.string.query_localization);
        ContentResolver contentResolver = context.getContentResolver();
        Call<MovieList> movieListCall = tmdbApiService.getPopularMovies(apiKey, pageToLoad, localization);
        fetchMovieList(movieListCall, contentResolver, true);
    }

    public static void fetchTopRatedMovies(TmdbApiService tmdbApiService, Context context, int pageToLoad) {
        String apiKey = BuildConfig.TMDB_API_KEY;
        String localization = context.getString(R.string.query_localization);
        ContentResolver contentResolver = context.getContentResolver();
        Call<MovieList> movieListCall = tmdbApiService.getTopRatedMovies(apiKey, pageToLoad, localization);
        fetchMovieList(movieListCall, contentResolver, false);
    }

    private static void fetchMovieList(Call<MovieList> movieListCall, ContentResolver contentResolver, boolean isPopular) {
        try {
            MovieList movieList = movieListCall.execute().body();
            ContentValues[] contentValues = TmdbUtils.getMovieContentValuesFromMovieList(movieList.getMovies(), isPopular);
            contentResolver.delete(MoviesDatabaseContract.MovieEntry.CONTENT_URI, null, null);
            contentResolver.bulkInsert(MoviesDatabaseContract.MovieEntry.CONTENT_URI, contentValues);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //        ArrayList<MovieDetails> movieDetailsList = new ArrayList<>();
//        try {
//            MovieList movieList = movieListCall.execute().body();
//            for (Movie movie : movieList.getMovies()) {
//                Call<MovieDetails> movieDetailsCall = tmdbApiService.getMovieDetails(movie.getId(), apiKey, localization);
//                MovieDetails movieDetails = movieDetailsCall.execute().body();
//                movieDetailsList.add(movieDetails);
//                Log.d(TAG, "Got details for movie: " + movieDetails.getTitle());
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

}
