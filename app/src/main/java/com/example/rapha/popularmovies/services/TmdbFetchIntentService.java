package com.example.rapha.popularmovies.services;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.rapha.popularmovies.BuildConfig;
import com.example.rapha.popularmovies.R;
import com.example.rapha.popularmovies.data.local.MoviesDatabaseContract;
import com.example.rapha.popularmovies.data.models.MovieList;
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

    private final String TAG = getClass().getSimpleName();
    String apiKey = BuildConfig.TMDB_API_KEY;
    String localization;
    ContentResolver contentResolver;
    private TmdbApi tmdbApi;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     * <p>
     * Used to name the worker thread, important only for debugging.
     */
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
    }

    private void createApiService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(this.getString(R.string.tmdb_base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        tmdbApi = retrofit.create(TmdbApi.class);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "onHandleIntent: " + intent.getAction());
        int pageToLoad = intent.getIntExtra("page", 1);
        switch (intent.getAction()) {
            case "fetch_popular_movies":
                fetchPopularMovies();
                break;
            case "fetch_top_rated_movies":
                fetchTopRatedMovies();
                break;
            case "initial_fetch":
                int pagesToLoad = intent.getIntExtra("pagesToLoad", 1);
                break;
            default:
                throw new RuntimeException("Unknown action: " + intent.getAction());
        }
        cleanDatabase();
        notifyBraodcastReceiver();
        Log.d(TAG, "fetchingFinished");
    }

    private void notifyBraodcastReceiver() {
        Intent fetchingFinishedIntent = new Intent(Constants.FETCHING_DATA_FINISHED_ACTION);
        LocalBroadcastManager.getInstance(this).sendBroadcast(fetchingFinishedIntent);
    }

    private void cleanDatabase() {
        contentResolver.delete(MoviesDatabaseContract.MovieEntry.CONTENT_URI,
                MoviesDatabaseContract.MovieEntry.COLUMN_IS_POPULAR + " = ? AND " +
                        MoviesDatabaseContract.MovieEntry.COLUMN_IS_FAVORITE + " = ? AND " +
                        MoviesDatabaseContract.MovieEntry.COLUMN_IS_TOP_RATED + " = ?",
                new String[]{"0", "0", "0"});
    }

    private void fetchPopularMovies() {
        Call<MovieList> movieListCall = tmdbApi.getPopularMovies(apiKey, 1, localization);
        try {
            MovieList movieList = movieListCall.execute().body();
            List<Integer> favoriteIds = getListOfFavoriteIds(contentResolver);
            if (movieList != null) {
                setBooleanColumnToFalse(MoviesDatabaseContract.MovieEntry.COLUMN_IS_POPULAR);
                ContentValues[] contentValues = TmdbUtils.getMovieContentValuesFromMovieList(movieList.getMovies(), true, favoriteIds);
                contentResolver.bulkInsert(MoviesDatabaseContract.MovieEntry.CONTENT_URI, contentValues);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fetchTopRatedMovies() {
        Call<MovieList> movieListCall = tmdbApi.getTopRatedMovies(apiKey, 1, localization);
        try {
            MovieList movieList = movieListCall.execute().body();
            List<Integer> favoriteIds = getListOfFavoriteIds(contentResolver);
            if (movieList != null) {
                setBooleanColumnToFalse(MoviesDatabaseContract.MovieEntry.COLUMN_IS_TOP_RATED);
                ContentValues[] contentValues = TmdbUtils.getMovieContentValuesFromMovieList(movieList.getMovies(), false, favoriteIds);
                contentResolver.bulkInsert(MoviesDatabaseContract.MovieEntry.CONTENT_URI, contentValues);
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
