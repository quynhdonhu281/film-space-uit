package com.example.filmspace_mobile.data.repository;

import com.example.filmspace_mobile.BuildConfig;
import com.example.filmspace_mobile.data.api.ApiService;
import com.example.filmspace_mobile.data.model.movie.Genre;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class GenreRepository {
    private static final String TAG = "GenreRepository";
    
    private final ApiService apiService;

    @Inject
    public GenreRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    /**
     * Fetch all genres from the API
     * @param callback Callback to handle success or failure
     */
    public void getAllGenres(RepositoryCallback<List<Genre>> callback) {
        apiService.getAllGenres().enqueue(new Callback<List<Genre>>() {
            @Override
            public void onResponse(Call<List<Genre>> call, Response<List<Genre>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = getHttpErrorMessage(response.code());
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<List<Genre>> call, Throwable t) {
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
                return "Genres not found.";
            case 500:
            case 502:
            case 503:
                return "Server error. Please try again later.";
            default:
                return "Failed to load genres. Please try again.";
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
