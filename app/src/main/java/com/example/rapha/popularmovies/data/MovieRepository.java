package com.example.rapha.popularmovies.data;

import android.content.Context;
import android.database.Cursor;

import com.example.rapha.popularmovies.data.local.LocalRepository;
import com.example.rapha.popularmovies.data.remote.RemoteRepository;

public class MovieRepository {

    private static MovieRepository INSTANCE = null;

    private RemoteRepository remoteRepository;
    private LocalRepository localRepository;

    private MovieRepository(Context context) {
        remoteRepository = new RemoteRepository(context);
        localRepository = new LocalRepository(context);
    }

    public static MovieRepository getInstance(Context context) {
        if (INSTANCE == null) INSTANCE = new MovieRepository(context);
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

    public Cursor getTrailers(int movieId) {
        return localRepository.getTrailers(movieId);
    }

    public Cursor getReviews(int movieId) {
        return localRepository.getReviews(movieId);
    }

    public void changeMovieFavoriteStatus(int movieId, boolean isFavorite) {
        localRepository.changeMovieFavoriteStatus(movieId, isFavorite);
    }

    public void initialRemoteFetch() {
        remoteRepository.initialFetch();
    }

    public void fetchAdditionalPopularMovies() {
        remoteRepository.fetchAdditionalPopularMovies();
    }

    public void fetchAdditionalTopRatedMovies() {
        remoteRepository.fetchAdditionalTopRatedMovies();
    }

    public void fetchTrailers(int movieId) {
        remoteRepository.fetchMovieTrailers(movieId);
    }

    public void fetchReviews(int movieId) {
        remoteRepository.fetchMovieReviews(movieId);
    }
}
