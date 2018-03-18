package com.example.rapha.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class MoviesDatabaseContract {

    public static final String CONTENT_AUTHORITY = "com.example.rapha.popularmovies";

    public static final String PATH_MOVIES = "movies";
    public static final String PATH_REVIEWS = "reviews";
    public static final String PATH_TRAILERS = "trailers";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI
                .buildUpon()
                .appendEncodedPath(PATH_MOVIES)
                .build();
        public static final String TABLE_NAME = "movies";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_POSTER_PATH = "poster_url";
        public static final String COLUMN_RUNTIME = "duration";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_IS_FAVORITE = "is_favorite";

        public static Uri buildMovieEntryUri(int movieId) {
            return CONTENT_URI.buildUpon().appendEncodedPath(String.valueOf(movieId)).build();
        }
    }


    public static final class ReviewEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI
                .buildUpon()
                .appendEncodedPath(PATH_REVIEWS)
                .build();
        public static final String TABLE_NAME = "reviews";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_AUTHOR = "author";
    }

    public static final class TrailerEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI
                .buildUpon()
                .appendEncodedPath(PATH_TRAILERS)
                .build();
        public static final String TABLE_NAME = "trailers";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_YOUTUBE_KEY = "youtube_key";
    }
}
