package com.example.rapha.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.rapha.popularmovies.data.MoviesDatabaseContract.MovieEntry;
import com.example.rapha.popularmovies.data.MoviesDatabaseContract.ReviewEntry;
import com.example.rapha.popularmovies.data.MoviesDatabaseContract.TrailerEntry;

public class MovieDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;

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
        return "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY, " +
                MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_RATING + " REAL NOT NULL, " +
                MovieEntry.COLUMN_RUNTIME + " INTEGER, " +
                MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_POPULARITY + " REAL NOT NULL, " +
                MovieEntry.COLUMN_IS_POPULAR + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_IS_TOP_RATED + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_IS_FAVORITE + " BOOLEAN NOT NULL" + ");";
    }

    private String assembleReviewTableCreateCommand() {
        return "CREATE TABLE " + ReviewEntry.TABLE_NAME + " (" +
                ReviewEntry._ID + " INTEGER PRIMARY KEY, " +
                ReviewEntry.COLUMN_AUTHOR + " TEXT NOT NULL, " +
                ReviewEntry.COLUMN_CONTENT + " TEXT NOT NULL, " +
                ReviewEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                "FOREIGN KEY (" + ReviewEntry.COLUMN_MOVIE_ID + ") REFERENCES " + MovieEntry.TABLE_NAME + "(" + MovieEntry._ID + ") ON UPDATE CASCADE ON DELETE CASCADE" +
                ");";
    }

    private String assembleTrailerTableCreateCommand() {
        return "CREATE TABLE " + TrailerEntry.TABLE_NAME + " (" +
                TrailerEntry._ID + " INTEGER PRIMARY KEY, " +
                TrailerEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                TrailerEntry.COLUMN_YOUTUBE_KEY + " TEXT NOT NULL, " +
                TrailerEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                "FOREIGN KEY (" + TrailerEntry.COLUMN_MOVIE_ID + ") REFERENCES " + MovieEntry.TABLE_NAME + "(" + MovieEntry._ID + ") ON UPDATE CASCADE ON DELETE CASCADE" +
                ");";
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TrailerEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ReviewEntry.TABLE_NAME);
        onCreate(db);
    }
}
