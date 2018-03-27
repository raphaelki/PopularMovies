package com.example.rapha.popularmovies.data.local;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.example.rapha.popularmovies.data.models.Movie;
import com.example.rapha.popularmovies.data.models.Review;
import com.example.rapha.popularmovies.data.models.Video;
import com.example.rapha.popularmovies.utils.TmdbUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LocalRepository {

    private static LocalRepository INSTANCE;
    private final String TAG = getClass().getSimpleName();
    private final ContentResolver contentResolver;

    private LocalRepository(Context context) {
        this.contentResolver = context.getContentResolver();
    }

    public static LocalRepository getInstance(Context context) {
        if (INSTANCE == null) INSTANCE = new LocalRepository(context);
        return INSTANCE;
    }

    public int cleanDatabase() {
        int moviesDeleted = contentResolver.delete(MoviesDatabaseContract.MovieEntry.CONTENT_URI,
                MoviesDatabaseContract.MovieEntry.COLUMN_IS_POPULAR + " = ? AND " +
                        MoviesDatabaseContract.MovieEntry.COLUMN_IS_FAVORITE + " = ? AND " +
                        MoviesDatabaseContract.MovieEntry.COLUMN_IS_TOP_RATED + " = ?",
                new String[]{"0", "0", "0"});
        Log.d(TAG, moviesDeleted + " movies deleted from database");
        return moviesDeleted;
    }

    public int insertTrailers(List<Video> videos, int movieId) {
        ContentValues[] trailerContentValues = TmdbUtils.getTrailerContentValuesformVideoList(videos, movieId);
        int trailersInserted = contentResolver.bulkInsert(MoviesDatabaseContract.TrailerEntry.CONTENT_URI, trailerContentValues);
        Log.d(TAG, trailersInserted + " trailers inserted to database");
        return trailersInserted;
    }

    public int insertReviews(List<Review> reviews, int movieId) {
        ContentValues[] reviewContentValues = TmdbUtils.getReviewContentValuesformVideoList(reviews, movieId);
        int reviewsInserted = contentResolver.bulkInsert(MoviesDatabaseContract.ReviewEntry.CONTENT_URI, reviewContentValues);
        Log.d(TAG, reviewsInserted + " reviews inserted to database");
        return reviewsInserted;
    }

    public int insertMovies(List<Movie> videos, boolean isPopular, boolean isInitialFetch) {
        if (isInitialFetch) {
            setBooleanColumnToFalse(isPopular ? MoviesDatabaseContract.MovieEntry.COLUMN_IS_POPULAR : MoviesDatabaseContract.MovieEntry.COLUMN_IS_TOP_RATED);
        }
        List<Integer> favoriteIds = getListOfFavoriteIds();
        ContentValues[] contentValues = TmdbUtils.getMovieContentValuesFromMovieList(videos, isPopular, favoriteIds);
        int moviesInserted = contentResolver.bulkInsert(MoviesDatabaseContract.MovieEntry.CONTENT_URI, contentValues);
        Log.d(TAG, moviesInserted + " movies inserted into database");
        return moviesInserted;
    }

    public void changeMovieFavoriteStatus(int movieId, boolean isFavorite) {
        Date date = new Date();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MoviesDatabaseContract.MovieEntry.COLUMN_IS_FAVORITE, isFavorite);
        contentValues.put(MoviesDatabaseContract.MovieEntry.COLUMN_DATE_ADDED_TO_FAVORITES, isFavorite ? date.getTime() : null);
        contentResolver.update(MoviesDatabaseContract.MovieEntry.buildMovieEntryUri(movieId), contentValues, null, null);
    }

    private List<Integer> getListOfFavoriteIds() {
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

    private void setBooleanColumnToFalse(String column) {
        ContentValues contentValues = new ContentValues(1);
        contentValues.put(column, false);
        contentResolver.update(MoviesDatabaseContract.MovieEntry.CONTENT_URI,
                contentValues,
                null,
                null);
    }
}
