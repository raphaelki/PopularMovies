package com.example.rapha.popularmovies.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.rapha.popularmovies.R;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class MovieDbNetworkUtils {

    public final static String POPULAR_PATH = "popular";
    public final static String TOP_RATED_PATH = "top_rated";
    private final static String TAG = MovieDbNetworkUtils.class.getSimpleName();
    private final static String MOVIE_DB_BASE_URL = "https://api.themoviedb.org/3/movie";
    private final static String PAGE_PARA = "page";
    private final static String API_KEY_PARA = "api_key";
    private final static String LANGUAGE = "language";

    public static String fetchMovies(Context context, String apiKey, String page, String sortOrder) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) buildQueryURL(context, sortOrder, apiKey, page).openConnection();
        try {
            BufferedInputStream stream = new BufferedInputStream(connection.getInputStream());
            Scanner scanner = new Scanner(stream);
            scanner.useDelimiter("\\A");
            String jsonResponse = "";
            if (scanner.hasNext()) {
                jsonResponse = scanner.next();
            }
            scanner.close();
            stream.close();
            return jsonResponse;
        } finally {
            connection.disconnect();
        }
    }

    private static URL buildQueryURL(Context context, String sortOrder, String apiKey, String page) {
        Uri uri = Uri.parse(MOVIE_DB_BASE_URL);
        uri = uri.buildUpon()
                .appendEncodedPath(sortOrder)
                .appendQueryParameter(API_KEY_PARA, apiKey)
                .appendQueryParameter(LANGUAGE, context.getResources().getString(R.string.query_localization))
                .appendQueryParameter(PAGE_PARA, page)
                .build();
        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "built URL: " + url);
        return url;
    }

}
