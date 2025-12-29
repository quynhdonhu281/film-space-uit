package com.example.filmspace_mobile.data.repository;


import com.example.filmspace_mobile.BuildConfig;
import com.example.filmspace_mobile.data.api.ApiService;
import com.example.filmspace_mobile.data.model.movie.Movie;
import com.example.filmspace_mobile.data.model.movie.RecommendationsResponse;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class MovieRepository {
    private static final String TAG = "MovieRepository";
    
    private final ApiService apiService;

    @Inject
    public MovieRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    /**
     * Fetch all movies from the API
     * @param callback Callback to handle success or failure
     */
    public void getAllMovies(RepositoryCallback<List<Movie>> callback) {
        apiService.getAllMovies().enqueue(new Callback<List<Movie>>() {
            @Override
            public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = getHttpErrorMessage(response.code());
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<List<Movie>> call, Throwable t) {
                String errorMessage = getNetworkErrorMessage(t);
                callback.onError(errorMessage);
            }
        });
    }

    /**
     * Fetch movies by genre ID
     * @param genreId The genre ID to filter movies
     * @param callback Callback to handle success or failure
     */
    public void getMoviesByGenre(int genreId, RepositoryCallback<List<Movie>> callback) {
        apiService.getMoviesByGenre(genreId).enqueue(new Callback<List<Movie>>() {
            @Override
            public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = getHttpErrorMessage(response.code());
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<List<Movie>> call, Throwable t) {
                String errorMessage = getNetworkErrorMessage(t);
                callback.onError(errorMessage);
            }
        });
    }

    /**
     * Fetch movie by ID with full details (cast, episodes, reviews)
     * @param movieId The movie ID to fetch
     * @param callback Callback to handle success or failure
     */
    public void getMovieById(int movieId, RepositoryCallback<Movie> callback) {
        apiService.getMovieById(movieId).enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = getHttpErrorMessage(response.code());
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                String errorMessage = getNetworkErrorMessage(t);
                callback.onError(errorMessage);
            }
        });
    }

    /**
     * Fetch recommended movies for the user
     * @param limit The number of recommendations to fetch
     * @param callback Callback to handle success or failure
     */
    public void getRecommendedMovies(int limit, RepositoryCallback<List<Movie>> callback) {
        apiService.getRecommendedMovies(limit).enqueue(new Callback<RecommendationsResponse>() {
            @Override
            public void onResponse(Call<RecommendationsResponse> call, Response<RecommendationsResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<Movie> movies = response.body().getData();
                    // Log for debugging
                    if (BuildConfig.DEBUG && movies != null && !movies.isEmpty()) {
                        android.util.Log.d(TAG, "Recommended movies count: " + movies.size());
                        android.util.Log.d(TAG, "First movie title: " + (movies.get(0).getTitle() != null ? movies.get(0).getTitle() : "NULL"));
                    }
                    callback.onSuccess(movies);
                } else {
                    String errorMessage = getHttpErrorMessage(response.code());
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<RecommendationsResponse> call, Throwable t) {
                String errorMessage = getNetworkErrorMessage(t);
                callback.onError(errorMessage);
            }
        });
    }

    /**
     * Get user-friendly error message based on HTTP status code
     */
    private String getHttpErrorMessage(int code) {
        switch (code) {
            case 400:
                return "Invalid request. Please check your input.";
            case 401:
                return "Unauthorized. Please login again.";
            case 403:
                return "Access forbidden.";
            case 404:
                return "Movies not found.";
            case 500:
            case 502:
            case 503:
                return "Server error. Please try again later.";
            default:
                return "Failed to load movies. Please try again.";
        }
    }

    /**
     * Get user-friendly error message based on exception type
     */
    private String getNetworkErrorMessage(Throwable t) {
        if (t instanceof UnknownHostException) {
            return "No internet connection. Please check your network.";
        } else if (t instanceof SocketTimeoutException) {
            return "Request timed out. Please try again.";
        } else {
            if (BuildConfig.DEBUG) {
                return "Error: " + t.getMessage();
            }
            return "Something went wrong. Please try again.";
        }
    }
}
