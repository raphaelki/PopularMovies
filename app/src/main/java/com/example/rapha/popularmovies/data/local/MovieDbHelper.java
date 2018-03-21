package com.example.rapha.popularmovies.data.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MovieDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 2;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys=ON;");
        db.execSQL(assembleMovieTableCreateCommand());
        db.execSQL(assembleReviewTableCreateCommand());
        db.execSQL(assembleTrailerTableCreateCommand());
    }

    private String assembleMovieTableCreateCommand() {
        return "CREATE TABLE " + MoviesDatabaseContract.MovieEntry.TABLE_NAME + " (" +
                MoviesDatabaseContract.MovieEntry._ID + " INTEGER PRIMARY KEY ON CONFLICT REPLACE, " +
                MoviesDatabaseContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MoviesDatabaseContract.MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                MoviesDatabaseContract.MovieEntry.COLUMN_RATING + " REAL NOT NULL, " +
                MoviesDatabaseContract.MovieEntry.COLUMN_RUNTIME + " INTEGER, " +
                MoviesDatabaseContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MoviesDatabaseContract.MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                MoviesDatabaseContract.MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MoviesDatabaseContract.MovieEntry.COLUMN_POPULARITY + " REAL NOT NULL, " +
                MoviesDatabaseContract.MovieEntry.COLUMN_IS_POPULAR + " INTEGER NOT NULL, " +
                MoviesDatabaseContract.MovieEntry.COLUMN_IS_TOP_RATED + " INTEGER NOT NULL, " +
                MoviesDatabaseContract.MovieEntry.COLUMN_DATE_ADDED_TO_FAVORITES + " INTEGER, " +
                MoviesDatabaseContract.MovieEntry.COLUMN_IS_FAVORITE + " BOOLEAN NOT NULL" + ");";
    }

    private String assembleReviewTableCreateCommand() {
        return "CREATE TABLE " + MoviesDatabaseContract.ReviewEntry.TABLE_NAME + " (" +
                MoviesDatabaseContract.ReviewEntry._ID + " INTEGER PRIMARY KEY, " +
                MoviesDatabaseContract.ReviewEntry.COLUMN_AUTHOR + " TEXT NOT NULL, " +
                MoviesDatabaseContract.ReviewEntry.COLUMN_CONTENT + " TEXT NOT NULL, " +
                MoviesDatabaseContract.ReviewEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                "FOREIGN KEY (" + MoviesDatabaseContract.ReviewEntry.COLUMN_MOVIE_ID + ") REFERENCES " + MoviesDatabaseContract.MovieEntry.TABLE_NAME + "(" + MoviesDatabaseContract.MovieEntry._ID + ") ON UPDATE CASCADE ON DELETE CASCADE" +
                ");";
    }

    private String assembleTrailerTableCreateCommand() {
        return "CREATE TABLE " + MoviesDatabaseContract.TrailerEntry.TABLE_NAME + " (" +
                MoviesDatabaseContract.TrailerEntry._ID + " INTEGER PRIMARY KEY, " +
                MoviesDatabaseContract.TrailerEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MoviesDatabaseContract.TrailerEntry.COLUMN_YOUTUBE_KEY + " TEXT NOT NULL, " +
                MoviesDatabaseContract.TrailerEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                "FOREIGN KEY (" + MoviesDatabaseContract.TrailerEntry.COLUMN_MOVIE_ID + ") REFERENCES " + MoviesDatabaseContract.MovieEntry.TABLE_NAME + "(" + MoviesDatabaseContract.MovieEntry._ID + ") ON UPDATE CASCADE ON DELETE CASCADE" +
                ");";
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MoviesDatabaseContract.MovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MoviesDatabaseContract.TrailerEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MoviesDatabaseContract.ReviewEntry.TABLE_NAME);
        onCreate(db);
    }
}
