package com.example.rapha.popularmovies.data;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;

import com.example.rapha.popularmovies.data.local.LocalRepository;
import com.example.rapha.popularmovies.data.remote.RemoteRepository;

public class MovieRepository {

    private static MovieRepository INSTANCE = null;

    private RemoteRepository remoteRepository;
    private LocalRepository localRepository;

    private MovieRepository(ContentResolver contentResolver) {
        remoteRepository = new RemoteRepository();
        localRepository = new LocalRepository(contentResolver);
    }

    ;

    public static MovieRepository getInstance(ContentResolver contentResolver) {
        if (INSTANCE == null) INSTANCE = new MovieRepository(contentResolver);
        return INSTANCE;
    }

    public Cursor getPopularMovies() {
        return localRepository.getPopularMovies();
    }

    public Cursor getTopRatedMovies() {
        return localRepository.getTopRatedMovies();
    }

    public Cursor getFavoriteMovies() {
        return localRepository.getFavoriteMovies();
    }

    public Cursor getMovie(int movieId) {
        return localRepository.getMovie(movieId);
    }

    public void changeMovieFavoriteStatus(int movieId, boolean isFavorite) {
        localRepository.changeMovieFavoriteStatus(movieId, isFavorite);
    }

    public void initialRemoteFetch(Context context) {
        remoteRepository.initialFetch(context);
    }
}
