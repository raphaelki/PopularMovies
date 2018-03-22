package com.example.rapha.popularmovies.data.local;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.Date;

public class LocalRepository {

    private final String[] booleanTrueSelectionArgs = {"1"};
    private ContentResolver contentResolver;

    public LocalRepository(Context context) {
        this.contentResolver = context.getContentResolver();
    }

    public Cursor getPopularMovies() {
        String[] projection = new String[]{
                MoviesDatabaseContract.MovieEntry._ID,
                MoviesDatabaseContract.MovieEntry.COLUMN_IS_FAVORITE,
                MoviesDatabaseContract.MovieEntry.COLUMN_TITLE,
                MoviesDatabaseContract.MovieEntry.COLUMN_POSTER_PATH
        };
        String selection = MoviesDatabaseContract.MovieEntry.COLUMN_IS_POPULAR + " = ?";
        String sortOrder = MoviesDatabaseContract.MovieEntry.COLUMN_POPULARITY + " DESC";
        return contentResolver.query(MoviesDatabaseContract.MovieEntry.CONTENT_URI,
                projection,
                selection,
                booleanTrueSelectionArgs,
                sortOrder);
    }

    public Cursor getTopRatedMovies() {
        String[] projection = new String[]{
                MoviesDatabaseContract.MovieEntry._ID,
                MoviesDatabaseContract.MovieEntry.COLUMN_IS_FAVORITE,
                MoviesDatabaseContract.MovieEntry.COLUMN_TITLE,
                MoviesDatabaseContract.MovieEntry.COLUMN_POSTER_PATH
        };
        String selection = MoviesDatabaseContract.MovieEntry.COLUMN_IS_TOP_RATED + " = ?";
        String sortOrder = MoviesDatabaseContract.MovieEntry.COLUMN_RATING + " DESC";
        return contentResolver.query(MoviesDatabaseContract.MovieEntry.CONTENT_URI,
                projection,
                selection,
                booleanTrueSelectionArgs,
                sortOrder);
    }

    public Cursor getFavoriteMovies() {
        String[] projection = new String[]{
                MoviesDatabaseContract.MovieEntry._ID,
                MoviesDatabaseContract.MovieEntry.COLUMN_IS_FAVORITE,
                MoviesDatabaseContract.MovieEntry.COLUMN_TITLE,
                MoviesDatabaseContract.MovieEntry.COLUMN_POSTER_PATH
        };
        String selection = MoviesDatabaseContract.MovieEntry.COLUMN_IS_FAVORITE + " = ?";
        String sortOrder = MoviesDatabaseContract.MovieEntry.COLUMN_DATE_ADDED_TO_FAVORITES + " DESC";
        return contentResolver.query(MoviesDatabaseContract.MovieEntry.CONTENT_URI,
                projection,
                selection,
                booleanTrueSelectionArgs,
                sortOrder);
    }

    public Cursor getMovie(int movieId) {
//        String[] projection = new String[]{
//                MoviesDatabaseContract.MovieEntry._ID,
//                MoviesDatabaseContract.MovieEntry.COLUMN_IS_FAVORITE,
//                MoviesDatabaseContract.MovieEntry.COLUMN_TITLE,
//                MoviesDatabaseContract.MovieEntry.COLUMN_POSTER_PATH
//        };
        return contentResolver.query(MoviesDatabaseContract.MovieEntry.buildMovieEntryUri(movieId),
                null,
                null,
                null,
                null);
    }

    public void changeMovieFavoriteStatus(int movieId, boolean isFavorite) {
        Date date = new Date();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MoviesDatabaseContract.MovieEntry.COLUMN_IS_FAVORITE, isFavorite);
        contentValues.put(MoviesDatabaseContract.MovieEntry.COLUMN_DATE_ADDED_TO_FAVORITES, isFavorite ? date.getTime() : null);
        contentResolver.update(MoviesDatabaseContract.MovieEntry.buildMovieEntryUri(movieId), contentValues, null, null);
    }

    public Cursor getTrailers(int movieId) {
        return contentResolver.query(MoviesDatabaseContract.TrailerEntry.buildTrailerEntryUri(movieId),
                null,
                null,
                null,
                null);
    }

    public Cursor getReviews(int movieId) {
        return contentResolver.query(MoviesDatabaseContract.ReviewEntry.buildReviewEntryUri(movieId),
                null,
                null,
                null,
                null);
    }
}
