package com.example.filmspace_mobile.data.api;

import com.example.filmspace_mobile.data.model.auth.*;
import com.example.filmspace_mobile.data.model.movie.Genre;
import com.example.filmspace_mobile.data.model.movie.Movie;
import com.example.filmspace_mobile.data.model.movie.RecommendationsResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
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

    @GET("api/Movies/{id}")
    Call<Movie> getMovieById(@Path("id") int movieId);

    @GET("api/Movies/genre/{id}")
    Call<List<Movie>> getMoviesByGenre(@Path("id") int genreId);

    @GET("api/genres")
    Call<List<Genre>> getAllGenres();

    @GET("api/history/recommendations")
    Call<RecommendationsResponse> getRecommendedMovies(@Query("limit") int limit);
}
