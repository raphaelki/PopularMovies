package com.example.rapha.popularmovies.data;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TmdbApiService {

    @GET("popular")
    Call<MovieList> getPopularMovies(@Query("api_key") String api_key, @Query("page") int page, @Query("language") String language);

    @GET("top_rated")
    Call<MovieList> getTopRatedMovies(@Query("api_key") String api_key, @Query("page") int page, @Query("language") String language);
}
