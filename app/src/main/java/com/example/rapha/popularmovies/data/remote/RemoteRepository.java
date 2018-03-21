package com.example.rapha.popularmovies.data.remote;

import android.content.Context;
import android.content.Intent;

import com.example.rapha.popularmovies.services.TmdbFetchIntentService;

public class RemoteRepository {

    public void initialFetch(Context context) {
        callIntentService(context, "fetch_popular_movies");
        callIntentService(context, "fetch_top_rated_movies");
    }

    private void callIntentService(Context context, String action) {
        Intent intentService = new Intent(context, TmdbFetchIntentService.class);
        intentService.setAction(action);
        context.startService(intentService);
    }
}
