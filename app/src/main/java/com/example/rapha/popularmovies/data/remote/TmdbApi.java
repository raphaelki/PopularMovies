package com.example.rapha.popularmovies.data.remote;

import com.example.rapha.popularmovies.data.models.MovieDetails;
import com.example.rapha.popularmovies.data.models.MovieList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TmdbApi {

    @GET("popular")
    Call<MovieList> getPopularMovies(@Query("api_key") String api_key, @Query("page") int page, @Query("language") String language);

    @GET("top_rated")
    Call<MovieList> getTopRatedMovies(@Query("api_key") String api_key, @Query("page") int page, @Query("language") String language);

    @GET("{movie_id}?append_to_response=videos,reviews")
    Call<MovieDetails> getMovieDetails(@Path("movie_id") int movieId, @Query("api_key") String api_key, @Query("language") String language);
}
