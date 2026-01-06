package com.example.filmspace_mobile.data.api;

import com.example.filmspace_mobile.data.model.auth.*;
import com.example.filmspace_mobile.data.model.history.AddToHistoryRequest;
import com.example.filmspace_mobile.data.model.history.WatchHistoryResponse;
import com.example.filmspace_mobile.data.model.movie.Cast;
import com.example.filmspace_mobile.data.model.movie.CreateReviewRequest;
import com.example.filmspace_mobile.data.model.movie.Genre;
import com.example.filmspace_mobile.data.model.movie.Movie;
import com.example.filmspace_mobile.data.model.movie.MovieViewResponse;
import com.example.filmspace_mobile.data.model.movie.RecommendationsResponse;
import com.example.filmspace_mobile.data.model.movie.Review;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // Authentication endpoints
    @POST("api/Auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("api/Auth/register")
    Call<RegisterResponse> register(@Body RegisterRequest request);

    @POST("api/Auth/verify-otp")
    Call<VerifyOTPResponse> verifyOtp(@Body VerifyOTPRequest request);

    @POST("api/Auth/resend-otp")
    Call<ResendOTPResponse> resendOtp(@Body ResendOTPRequest request);

    @POST("api/Auth/forgot-password")
    Call<ForgotPasswordResponse> forgotPassword(@Body ForgotPasswordRequest request);

    @POST("api/Auth/reset-password")
    Call<ResetPasswordResponse> resetPassword(@Body ResetPasswordRequest request);

    @POST("api/Auth/logout")
    Call<Void> logout();

    // User endpoints
    @GET("api/users/{userId}")
    Call<UserResponse> getUserById(@Path("userId") int userId);

    @Multipart
    @PUT("api/users/{userId}")
    Call<UserResponse> updateUser(
            @Path("userId") int userId,
            @Part("username") RequestBody username,
            @Part("name") RequestBody name,
            @Part("email") RequestBody email,
            @Part MultipartBody.Part avatar
    );

    @PUT("api/users/{userId}")
    Call<UserResponse> updateUserWithoutAvatar(
            @Path("userId") int userId,
            @Body UpdateUserRequest request
    );

    // Movie endpoints
    @GET("api/Movies")
    Call<List<Movie>> getAllMovies();

    // Pagination endpoints for better performance
    @GET("api/Movies")
    Call<List<Movie>> getMoviesPaginated(@Query("page") int page, @Query("pageSize") int pageSize);

    @GET("api/Movies/{id}/rating")
    Call<List<Movie>> getTopRatedMovies(@Path("id") int movieId, @Query("limit") int limit);

    // Fallback method to get all movies for top rated
    @GET("api/Movies")
    Call<List<Movie>> getAllMoviesForTopRated();

    @GET("api/Movies/{id}")
    Call<Movie> getMovieById(@Path("id") int movieId);

    @GET("api/Movies/genre/{id}")
    Call<List<Movie>> getMoviesByGenre(@Path("id") int genreId);

    @GET("api/genres")
    Call<List<Genre>> getAllGenres();

    // Watch History endpoints
    @GET("api/history/watched")
    Call<WatchHistoryResponse> getWatchHistory(@Query("page") int page, @Query("pageSize") int pageSize);

    @POST("api/history")
    Call<Void> addToHistory(@Body AddToHistoryRequest request);

    @DELETE("api/history/{movieId}")
    Call<Void> deleteFromHistory(@Path("movieId") int movieId);

    @DELETE("api/history/clear")
    Call<Void> clearAllHistory();

    @GET("api/casts")
    Call<List<Cast>> getAllCasts();

    // Review endpoints
    @POST("api/Reviews")
    Call<Review> createReview(@Body CreateReviewRequest request);
    @GET("api/history/movie/{movieId}/views")
    Call<MovieViewResponse> getMovieViews(@Path("movieId") int movieId);
    @GET("api/history/recommendations")
    Call<RecommendationsResponse> getRecommendedMovies(@Query("limit") int limit);
}
