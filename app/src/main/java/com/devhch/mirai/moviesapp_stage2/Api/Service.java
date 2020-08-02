package com.devhch.mirai.moviesapp_stage2.Api;

import com.devhch.mirai.moviesapp_stage2.Models.MoviesResponse;
import com.devhch.mirai.moviesapp_stage2.Models.Review;
import com.devhch.mirai.moviesapp_stage2.Models.TrailerResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created By Hamza Chaouki [Mirai Dev].
 * On 7/5/2020
 */
public interface Service {

    @GET("movie/popular")
    Call<MoviesResponse> getPopularMovies(@Query("api_key") String apiKey);

    @GET("movie/top_rated")
    Call<MoviesResponse> getTopRatedMovies(@Query("api_key") String apiKey);

    @GET("movie/{movie_id}/videos")
    Call<TrailerResponse> getMovieTrailer(@Path("movie_id") int id, @Query("api_key") String apiKey);

    //Reviews
    @GET("movie/{movie_id}/reviews")
    Call<Review> getReview(@Path("movie_id") int id, @Query("api_key") String apiKey);
}

