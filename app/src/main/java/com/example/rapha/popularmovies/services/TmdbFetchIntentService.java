package com.example.rapha.popularmovies.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.rapha.popularmovies.R;
import com.example.rapha.popularmovies.data.TmdbApiService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class TmdbFetchIntentService extends IntentService {

    private final String TAG = getClass().getSimpleName();

    private TmdbApiService tmdbApiService;

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
        Log.d(TAG, "Creating service");
        super.onCreate();
        createApiService();
    }

    private void createApiService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(this.getString(R.string.tmdb_base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        tmdbApiService = retrofit.create(TmdbApiService.class);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        int pageToLoad = intent.getIntExtra("page", 1);
        switch (intent.getAction()) {
            case "fetch_popular_movies":
                FetcherTasks.fetchPopularMovies(tmdbApiService, this, pageToLoad);
                break;
            case "fetch_top_rated_movies":
                FetcherTasks.fetchTopRatedMovies(tmdbApiService, this, pageToLoad);
                break;
            default:
                throw new RuntimeException("Unknown action: " + intent.getAction());
        }
    }
}
