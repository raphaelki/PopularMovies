package com.example.rapha.popularmovies.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.rapha.popularmovies.BuildConfig;
import com.example.rapha.popularmovies.R;
import com.example.rapha.popularmovies.data.local.LocalRepository;
import com.example.rapha.popularmovies.data.models.MoviesResponse;
import com.example.rapha.popularmovies.data.models.ReviewResponse;
import com.example.rapha.popularmovies.data.models.VideoResponse;
import com.example.rapha.popularmovies.data.remote.TmdbApi;
import com.example.rapha.popularmovies.utils.Constants;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class TmdbFetchIntentService extends IntentService {

    public static final String FETCH_POPULAR_MOVIES_ACTION = "fetch_popular_movies";
    public static final String FETCH_TOP_RATED_MOVIES_ACTION = "fetch_top_rated_movies";
    public static final String FETCH_MOVIE_TRAILERS_ACTION = "fetch_movie_trailers";
    public static final String FETCH_MOVIE_REVIEWS_ACTION = "fetch_movie_reviews";

    private final String TAG = getClass().getSimpleName();
    private String apiKey = BuildConfig.TMDB_API_KEY;
    private String localization;
    private TmdbApi tmdbApi;
    private IntentServiceBroadcaster intentServiceBroadcaster;
    private int currentPopularMoviesPage;
    private int currentTopRatedMoviesPage;
    private LocalRepository localRepository;

    public TmdbFetchIntentService() {
        super("TmdbFetchIntentService");
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "Creating Fetcher Intent Service");
        super.onCreate();
        createApiService();
        localization = getString(R.string.query_localization);
        intentServiceBroadcaster = new IntentServiceBroadcaster(this);
        loadCurrentPagesFromSharedPrefs();
        localRepository = LocalRepository.getInstance(this);
    }

    @Override
    public void onDestroy() {
        saveCurrentPagesToSharedPrefs();
        localRepository.cleanDatabase();
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
            case FETCH_MOVIE_TRAILERS_ACTION:
                fetchMovieTrailers(movieId);
                break;
            case FETCH_MOVIE_REVIEWS_ACTION:
                fetchMovieReviews(movieId);
                break;
            default:
                throw new RuntimeException("Unknown action: " + intent.getAction());
        }
        Log.d(TAG, "Fetching Finished");
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
                localRepository.insertTrailers(videoResponse.getVideos(), movieId);
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
                localRepository.insertReviews(reviewResponse.getReviews(), movieId);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createApiService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.TMDB_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        tmdbApi = retrofit.create(TmdbApi.class);
    }

    private void fetchPopularMovies() {
        Call<MoviesResponse> movieListCall = tmdbApi.getPopularMovies(apiKey, currentPopularMoviesPage, localization);
        try {
            MoviesResponse moviesResponse = movieListCall.execute().body();
            if (moviesResponse != null) {
                boolean isInitialFetch = currentPopularMoviesPage == 1;
                localRepository.insertMovies(moviesResponse.getMovies(), true, isInitialFetch);
                currentPopularMoviesPage++;
                intentServiceBroadcaster.fireBroadcastEvent(Constants.FETCH_POPULAR_MOVIES_FINISHED_MESSAGE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fetchTopRatedMovies() {
        Call<MoviesResponse> movieListCall = tmdbApi.getTopRatedMovies(apiKey, currentTopRatedMoviesPage, localization);
        try {
            MoviesResponse moviesResponse = movieListCall.execute().body();
            if (moviesResponse != null) {
                boolean isInitialFetch = currentTopRatedMoviesPage == 1;
                localRepository.insertMovies(moviesResponse.getMovies(), false, isInitialFetch);
                currentTopRatedMoviesPage++;
                intentServiceBroadcaster.fireBroadcastEvent(Constants.FETCH_TOP_RATED_MOVIES_FINISHED_MESSAGE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
