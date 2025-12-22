package com.example.filmspace_mobile.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.filmspace_mobile.data.api.ApiService;
import com.example.filmspace_mobile.data.api.RetrofitClient;
import com.example.filmspace_mobile.data.model.movie.Movie;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieViewModel extends AndroidViewModel {
    private final ApiService apiService;

    // Movies
    private final MutableLiveData<List<Movie>> allMoviesLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> moviesErrorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> moviesLoadingLiveData = new MutableLiveData<>();

    // Movies by genre
    private final MutableLiveData<List<Movie>> moviesByGenreLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> moviesByGenreErrorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> moviesByGenreLoadingLiveData = new MutableLiveData<>();

    public MovieViewModel(@NonNull Application application) {
        super(application);
        apiService = RetrofitClient.getApiService();
    }

    // Getters for LiveData
    public LiveData<List<Movie>> getAllMovies() { return allMoviesLiveData; }
    public LiveData<String> getMoviesError() { return moviesErrorLiveData; }
    public LiveData<Boolean> getMoviesLoading() { return moviesLoadingLiveData; }

    public LiveData<List<Movie>> getMoviesByGenre() { return moviesByGenreLiveData; }
    public LiveData<String> getMoviesByGenreError() { return moviesByGenreErrorLiveData; }
    public LiveData<Boolean> getMoviesByGenreLoading() { return moviesByGenreLoadingLiveData; }

    // Fetch all movies
    public void fetchAllMovies() {
        moviesLoadingLiveData.setValue(true);
        apiService.getAllMovies().enqueue(new Callback<List<Movie>>() {
            @Override
            public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {
                moviesLoadingLiveData.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    allMoviesLiveData.setValue(response.body());
                } else {
                    moviesErrorLiveData.setValue("Failed to load movies");
                }
            }

            @Override
            public void onFailure(Call<List<Movie>> call, Throwable t) {
                moviesLoadingLiveData.setValue(false);
                moviesErrorLiveData.setValue("Error: " + t.getMessage());
            }
        });
    }

    // Fetch movies by genre
    public void fetchMoviesByGenre(int genreId) {
        moviesByGenreLoadingLiveData.setValue(true);
        apiService.getMoviesByGenre(genreId).enqueue(new Callback<List<Movie>>() {
            @Override
            public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {
                moviesByGenreLoadingLiveData.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    moviesByGenreLiveData.setValue(response.body());
                } else {
                    moviesByGenreErrorLiveData.setValue("Failed to load movies");
                }
            }

            @Override
            public void onFailure(Call<List<Movie>> call, Throwable t) {
                moviesByGenreLoadingLiveData.setValue(false);
                moviesByGenreErrorLiveData.setValue("Error: " + t.getMessage());
            }
        });
    }
}
