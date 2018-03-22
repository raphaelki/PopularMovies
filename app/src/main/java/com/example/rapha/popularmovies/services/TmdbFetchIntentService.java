package com.example.rapha.popularmovies.services;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.rapha.popularmovies.BuildConfig;
import com.example.rapha.popularmovies.R;
import com.example.rapha.popularmovies.data.local.MoviesDatabaseContract;
import com.example.rapha.popularmovies.data.models.MovieList;
import com.example.rapha.popularmovies.data.models.ReviewResponse;
import com.example.rapha.popularmovies.data.models.VideoResponse;
import com.example.rapha.popularmovies.data.remote.TmdbApi;
import com.example.rapha.popularmovies.utils.Constants;
import com.example.rapha.popularmovies.utils.TmdbUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class TmdbFetchIntentService extends IntentService {

    public static final String FETCH_POPULAR_MOVIES_ACTION = "fetch_popular_movies";
    public static final String FETCH_TOP_RATED_MOVIES_ACTION = "fetch_top_rated_movies";
    public static final String FETCH_MOVIE_DETAILS_ACTION = "fetch_movie_details";
    public static final String FETCH_MOVIE_TRAILERS_ACTION = "fetch_movie_trailers";
    public static final String FETCH_MOVIE_REVIEWS_ACTION = "fetch_movie_reviews";
    private final String TAG = getClass().getSimpleName();
    private String apiKey = BuildConfig.TMDB_API_KEY;
    private String localization;
    private ContentResolver contentResolver;
    private TmdbApi tmdbApi;
    private IntentServiceBroadcaster intentServiceBroadcaster;
    private int currentPopularMoviesPage;
    private int currentTopRatedMoviesPage;

    public TmdbFetchIntentService() {
        super("TmdbFetchIntentService");
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "Creating Fetcher Intent Service");
        super.onCreate();
        createApiService();
        contentResolver = getContentResolver();
        localization = getString(R.string.query_localization);
        intentServiceBroadcaster = new IntentServiceBroadcaster(this);
        loadCurrentPagesFromSharedPrefs();
    }

    @Override
    public void onDestroy() {
        saveCurrentPagesToSharedPrefs();
        cleanDatabase();
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "onHandleIntent: " + intent.getAction());
        if (!checkNetworkConnection()) {
            intentServiceBroadcaster.fireBroadcastEvent(Constants.NO_CONNECTION_MESSAGE);
            return;
        }
        int movieId = intent.getIntExtra(Constants.MOVIE_ID_BUNDLE_KEY, 0);
        switch (intent.getAction()) {
            case FETCH_POPULAR_MOVIES_ACTION:
                fetchPopularMovies();
                break;
            case FETCH_TOP_RATED_MOVIES_ACTION:
                fetchTopRatedMovies();
                break;
            case FETCH_MOVIE_DETAILS_ACTION:
                break;
            case FETCH_MOVIE_TRAILERS_ACTION:
                fetchMovieTrailers(movieId);
                break;
            case FETCH_MOVIE_REVIEWS_ACTION:
                fetchMovieReviews(movieId);
                break;
            default:
                throw new RuntimeException("Unknown action: " + intent.getAction());
        }
        Log.d(TAG, "fetchingFinished");
    }

    private boolean checkNetworkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    private void loadCurrentPagesFromSharedPrefs() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Constants.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
        currentPopularMoviesPage = sharedPreferences.getInt(Constants.POPULAR_MOVIES_PAGE_KEY, 1);
        currentTopRatedMoviesPage = sharedPreferences.getInt(Constants.TOP_RATED_MOVIES_PAGE_KEY, 1);
    }

    private void saveCurrentPagesToSharedPrefs() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Constants.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(Constants.TOP_RATED_MOVIES_PAGE_KEY, currentTopRatedMoviesPage);
        editor.putInt(Constants.POPULAR_MOVIES_PAGE_KEY, currentPopularMoviesPage);
        editor.commit();
    }

    private void fetchMovieTrailers(int movieId) {
        Call<VideoResponse> videoResultsCall = tmdbApi.getTrailers(movieId, apiKey, localization);
        try {
            VideoResponse videoResponse = videoResultsCall.execute().body();
            if (videoResponse != null) {
                ContentValues[] trailerContentValues = TmdbUtils.getTrailerContentValuesformVideoList(videoResponse.getVideos(), movieId);
                int trailersInserted = contentResolver.bulkInsert(MoviesDatabaseContract.TrailerEntry.CONTENT_URI, trailerContentValues);
                Log.d(TAG, trailersInserted + " trailers inserted to database");
                intentServiceBroadcaster.fireBroadcastEvent(Constants.FETCH_TRAILERS_FINISHED_MESSAGE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fetchMovieReviews(int movieId) {
        Call<ReviewResponse> reviewResultsCall = tmdbApi.getReviews(movieId, apiKey, localization);
        try {
            ReviewResponse reviewResponse = reviewResultsCall.execute().body();
            if (reviewResponse != null) {
                ContentValues[] reviewContentValues = TmdbUtils.getReviewContentValuesformVideoList(reviewResponse.getReviews(), movieId);
                int reviewsInserted = contentResolver.bulkInsert(MoviesDatabaseContract.ReviewEntry.CONTENT_URI, reviewContentValues);
                Log.d(TAG, reviewsInserted + " reviews inserted to database");
                intentServiceBroadcaster.fireBroadcastEvent(Constants.FETCH_REVIEWS_FINISHED_MESSAGE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createApiService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(this.getString(R.string.tmdb_base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        tmdbApi = retrofit.create(TmdbApi.class);
    }

    private void cleanDatabase() {
        contentResolver.delete(MoviesDatabaseContract.MovieEntry.CONTENT_URI,
                MoviesDatabaseContract.MovieEntry.COLUMN_IS_POPULAR + " = ? AND " +
                        MoviesDatabaseContract.MovieEntry.COLUMN_IS_FAVORITE + " = ? AND " +
                        MoviesDatabaseContract.MovieEntry.COLUMN_IS_TOP_RATED + " = ?",
                new String[]{"0", "0", "0"});
    }

    private void fetchPopularMovies() {
        Call<MovieList> movieListCall = tmdbApi.getPopularMovies(apiKey, currentPopularMoviesPage, localization);
        try {
            MovieList movieList = movieListCall.execute().body();
            List<Integer> favoriteIds = getListOfFavoriteIds(contentResolver);
            if (movieList != null) {
                if (currentPopularMoviesPage == 1)
                    setBooleanColumnToFalse(MoviesDatabaseContract.MovieEntry.COLUMN_IS_POPULAR);
                ContentValues[] contentValues = TmdbUtils.getMovieContentValuesFromMovieList(movieList.getMovies(), true, favoriteIds);
                contentResolver.bulkInsert(MoviesDatabaseContract.MovieEntry.CONTENT_URI, contentValues);
                intentServiceBroadcaster.fireBroadcastEvent(Constants.FETCH_POPULAR_MOVIES_FINISHED_MESSAGE);
                currentPopularMoviesPage++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fetchTopRatedMovies() {
        Call<MovieList> movieListCall = tmdbApi.getTopRatedMovies(apiKey, currentTopRatedMoviesPage, localization);
        try {
            MovieList movieList = movieListCall.execute().body();
            List<Integer> favoriteIds = getListOfFavoriteIds(contentResolver);
            if (movieList != null) {
                if (currentTopRatedMoviesPage == 1)
                    setBooleanColumnToFalse(MoviesDatabaseContract.MovieEntry.COLUMN_IS_TOP_RATED);
                ContentValues[] contentValues = TmdbUtils.getMovieContentValuesFromMovieList(movieList.getMovies(), false, favoriteIds);
                contentResolver.bulkInsert(MoviesDatabaseContract.MovieEntry.CONTENT_URI, contentValues);
                intentServiceBroadcaster.fireBroadcastEvent(Constants.FETCH_TOP_RATED_MOVIES_FINISHED_MESSAGE);
                currentTopRatedMoviesPage++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setBooleanColumnToFalse(String column) {
        ContentValues contentValues = new ContentValues(1);
        contentValues.put(column, false);
        contentResolver.update(MoviesDatabaseContract.MovieEntry.CONTENT_URI,
                contentValues,
                null,
                null);
    }

    private List<Integer> getListOfFavoriteIds(ContentResolver contentResolver) {
        String[] idProjection = {MoviesDatabaseContract.MovieEntry._ID};
        String[] favoriteSelectionArgs = {"1"};
        Cursor cursor = contentResolver.query(MoviesDatabaseContract.MovieEntry.CONTENT_URI,
                idProjection,
                MoviesDatabaseContract.MovieEntry.COLUMN_IS_FAVORITE + " = ?",
                favoriteSelectionArgs,
                null);
        ArrayList<Integer> favorites = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            favorites.add(cursor.getInt(cursor.getColumnIndex(MoviesDatabaseContract.MovieEntry._ID)));
            cursor.moveToNext();
        }
        cursor.close();
        return favorites;
    }
}
