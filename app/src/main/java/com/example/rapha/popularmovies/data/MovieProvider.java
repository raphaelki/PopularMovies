package com.example.rapha.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


public class MovieProvider extends ContentProvider {

    private final static int CODE_MOVIES = 100;
    private final static int CODE_MOVIE = 101;
    private final static int CODE_REVIEWS = 200;
    private final static int CODE_TRAILERS = 300;
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private final String TAG = getClass().getSimpleName();
    private MovieDbHelper databaseHelper;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesDatabaseContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, MoviesDatabaseContract.PATH_MOVIES, CODE_MOVIES);
        matcher.addURI(authority, MoviesDatabaseContract.PATH_MOVIES + "/#", CODE_MOVIE);
        matcher.addURI(authority, MoviesDatabaseContract.PATH_REVIEWS, CODE_REVIEWS);
        matcher.addURI(authority, MoviesDatabaseContract.PATH_TRAILERS, CODE_TRAILERS);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        databaseHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIES:
                cursor = databaseHelper.getReadableDatabase().query(MoviesDatabaseContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_MOVIE:
                String movieId = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{movieId};
                cursor = databaseHelper.getReadableDatabase().query(MoviesDatabaseContract.MovieEntry.TABLE_NAME,
                        projection,
                        MoviesDatabaseContract.MovieEntry._ID + " = ?",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_REVIEWS:
                cursor = databaseHelper.getReadableDatabase().query(MoviesDatabaseContract.ReviewEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_TRAILERS:
                cursor = databaseHelper.getReadableDatabase().query(MoviesDatabaseContract.TrailerEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Uri could not be matched: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new RuntimeException("getType is not supported by MovieProvider");
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = databaseHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIES:
                long _id = db.insert(MoviesDatabaseContract.MovieEntry.TABLE_NAME, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                if (_id != -1) return ContentUris.withAppendedId(uri, _id);
        }
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = databaseHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIES:
                int rowsDeleted = db.delete(MoviesDatabaseContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                if (rowsDeleted != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;
            default:
                throw new UnsupportedOperationException("Uri could not be matched: " + uri);
        }
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIES:
                return bulkInsertToTable(MoviesDatabaseContract.MovieEntry.TABLE_NAME, values, uri);
        }
        return super.bulkInsert(uri, values);
    }

    private int bulkInsertToTable(String table, ContentValues[] values, Uri uri) {
        final SQLiteDatabase db = databaseHelper.getWritableDatabase();
        int rowsInserted = 0;
        db.beginTransaction();
        try {
            for (ContentValues value : values) {
                long _id = db.insert(table, null, value);
                if (_id != 1) {
                    rowsInserted++;
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        if (rowsInserted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsInserted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = databaseHelper.getWritableDatabase();
        int updatedEntries;
        String[] selectionArguments = new String[]{uri.getLastPathSegment()};
        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIE:
                updatedEntries = db.update(MoviesDatabaseContract.MovieEntry.TABLE_NAME,
                        values,
                        MoviesDatabaseContract.MovieEntry._ID + " = ?",
                        selectionArguments);
                break;
            default:
                throw new RuntimeException("Update is not supported for uri: " + uri);
        }
        return updatedEntries;
    }
}
