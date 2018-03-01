package com.example.rapha.popularmovies.utils;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class MovieDbNetworkUtils {

    private final static String TAG = MovieDbNetworkUtils.class.getSimpleName();

    private final static String MOVIE_DB_BASE_URL = "https://api.themoviedb.org/3/movie";
    private final static String POPULAR_PATH = "popular";
    private final static String TOP_RATED_PATH = "top_rated";

    private final static String API_KEY_PARA = "api_key";

    public static String fetchPopularMovies(String apiKey) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) buildQueryURL(POPULAR_PATH, apiKey).openConnection();
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

    private static URL buildQueryURL(String path, String apiKey) {
        Uri uri = Uri.parse(MOVIE_DB_BASE_URL);
        uri = uri.buildUpon()
                .appendEncodedPath(path)
                .appendQueryParameter(API_KEY_PARA, apiKey)
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
