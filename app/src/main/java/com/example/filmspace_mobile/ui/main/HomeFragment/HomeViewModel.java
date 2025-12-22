package com.example.filmspace_mobile.ui.main.HomeFragment;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.filmspace_mobile.data.model.movie.Genre;
import com.example.filmspace_mobile.data.model.movie.Movie;

import java.util.List;

public class HomeViewModel extends AndroidViewModel {
    private static final String TAG = "HomeViewModel";

    // LiveData for slider movies
    private final MutableLiveData<List<Movie>> sliderMoviesLiveData = new MutableLiveData<>();
    
    // LiveData for best movies
    private final MutableLiveData<List<Movie>> bestMoviesLiveData = new MutableLiveData<>();
    
    // LiveData for upcoming movies
    private final MutableLiveData<List<Movie>> upcomingMoviesLiveData = new MutableLiveData<>();
    
    // LiveData for genres
    private final MutableLiveData<List<Genre>> genresLiveData = new MutableLiveData<>();
    
    // Loading states
    private final MutableLiveData<Boolean> moviesLoadingLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> genresLoadingLiveData = new MutableLiveData<>();
    
    // Error states
    private final MutableLiveData<String> moviesErrorLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> genresErrorLiveData = new MutableLiveData<>();

    public HomeViewModel(@NonNull Application application) {
        super(application);
    }

    // Getters for LiveData
    public LiveData<List<Movie>> getSliderMovies() {
        return sliderMoviesLiveData;
    }

    public LiveData<List<Movie>> getBestMovies() {
        return bestMoviesLiveData;
    }

    public LiveData<List<Movie>> getUpcomingMovies() {
        return upcomingMoviesLiveData;
    }

    public LiveData<List<Genre>> getGenres() {
        return genresLiveData;
    }

    public LiveData<Boolean> getMoviesLoading() {
        return moviesLoadingLiveData;
    }

    public LiveData<Boolean> getGenresLoading() {
        return genresLoadingLiveData;
    }

    public LiveData<String> getMoviesError() {
        return moviesErrorLiveData;
    }

    public LiveData<String> getGenresError() {
        return genresErrorLiveData;
    }

    // Setters for updating data from Fragment (temporary until Repository pattern is implemented)
    public void setMovies(List<Movie> movies) {
        this.sliderMoviesLiveData.setValue(movies);
        this.bestMoviesLiveData.setValue(movies);
        this.upcomingMoviesLiveData.setValue(movies);
    }

    public void setGenres(List<Genre> genres) {
        this.genresLiveData.setValue(genres);
    }

    public void setMoviesLoading(boolean isLoading) {
        this.moviesLoadingLiveData.setValue(isLoading);
    }

    public void setGenresLoading(boolean isLoading) {
        this.genresLoadingLiveData.setValue(isLoading);
    }

    public void setMoviesError(String error) {
        this.moviesErrorLiveData.setValue(error);
    }

    public void setGenresError(String error) {
        this.genresErrorLiveData.setValue(error);
    }
}
