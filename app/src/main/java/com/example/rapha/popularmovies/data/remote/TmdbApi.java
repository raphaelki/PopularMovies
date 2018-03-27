package com.example.rapha.popularmovies.data.remote;

import com.example.rapha.popularmovies.data.models.MoviesResponse;
import com.example.rapha.popularmovies.data.models.ReviewResponse;
import com.example.rapha.popularmovies.data.models.VideoResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TmdbApi {

    @GET("popular")
    Call<MoviesResponse> getPopularMovies(@Query("api_key") String api_key, @Query("page") int page, @Query("language") String language);

    @GET("top_rated")
    Call<MoviesResponse> getTopRatedMovies(@Query("api_key") String api_key, @Query("page") int page, @Query("language") String language);

    @GET("{movie_id}/videos")
    Call<VideoResponse> getTrailers(@Path("movie_id") int movieId, @Query("api_key") String api_key, @Query("language") String language);

    @GET("{movie_id}/reviews")
    Call<ReviewResponse> getReviews(@Path("movie_id") int movieId, @Query("api_key") String api_key, @Query("language") String language);
}
