package com.example.rapha.popularmovies.data.remote;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.rapha.popularmovies.services.TmdbFetchIntentService;
import com.example.rapha.popularmovies.utils.Constants;

public class RemoteRepository {

    private Context context;

    public RemoteRepository(Context context) {
        this.context = context;
    }

    public void initialFetch() {
        resetCurrentPagesInSharedPrefs();
        callIntentService(TmdbFetchIntentService.FETCH_POPULAR_MOVIES_ACTION, null);
        callIntentService(TmdbFetchIntentService.FETCH_TOP_RATED_MOVIES_ACTION, null);
    }

    private void resetCurrentPagesInSharedPrefs() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(Constants.POPULAR_MOVIES_PAGE_KEY, 1);
        editor.putInt(Constants.TOP_RATED_MOVIES_PAGE_KEY, 1);
        editor.commit();
    }

    public void fetchAdditionalPopularMovies() {
        callIntentService(TmdbFetchIntentService.FETCH_POPULAR_MOVIES_ACTION, null);
    }

    public void fetchAdditionalTopRatedMovies() {
        callIntentService(TmdbFetchIntentService.FETCH_TOP_RATED_MOVIES_ACTION, null);
    }

    public void fetchMovieDetails(int movieId) {
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.MOVIE_ID_BUNDLE_KEY, movieId);
        callIntentService(TmdbFetchIntentService.FETCH_MOVIE_DETAILS_ACTION, bundle);
    }

    public void fetchMovieTrailers(int movieId) {
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.MOVIE_ID_BUNDLE_KEY, movieId);
        callIntentService(TmdbFetchIntentService.FETCH_MOVIE_TRAILERS_ACTION, bundle);
    }

    public void fetchMovieReviews(int movieId) {
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.MOVIE_ID_BUNDLE_KEY, movieId);
        callIntentService(TmdbFetchIntentService.FETCH_MOVIE_REVIEWS_ACTION, bundle);
    }

    private void callIntentService(String action, @Nullable Bundle bundle) {
        Intent intentService = new Intent(context, TmdbFetchIntentService.class);
        if (bundle != null) intentService.putExtras(bundle);
        intentService.setAction(action);
        context.startService(intentService);
    }
}
