package com.example.filmspace_mobile.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.filmspace_mobile.data.model.movie.Movie;
import com.example.filmspace_mobile.data.repository.MovieRepository;
import com.example.filmspace_mobile.data.repository.RepositoryCallback;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MovieViewModel extends ViewModel {
    private static final String TAG = "MovieViewModel";
    
    private final MovieRepository movieRepository;

    // Movies
    private final MutableLiveData<List<Movie>> allMoviesLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> moviesErrorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> moviesLoadingLiveData = new MutableLiveData<>();

    // Movies by genre
    private final MutableLiveData<List<Movie>> moviesByGenreLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> moviesByGenreErrorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> moviesByGenreLoadingLiveData = new MutableLiveData<>();

    @Inject
    public MovieViewModel(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
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
        movieRepository.getAllMovies(new RepositoryCallback<List<Movie>>() {
            @Override
            public void onSuccess(List<Movie> movies) {
                moviesLoadingLiveData.setValue(false);
                allMoviesLiveData.setValue(movies);
            }

            @Override
            public void onError(String errorMessage) {
                moviesLoadingLiveData.setValue(false);
                moviesErrorLiveData.setValue(errorMessage);
            }
        });
    }

    // Fetch movies by genre
    public void fetchMoviesByGenre(int genreId) {
        moviesByGenreLoadingLiveData.setValue(true);
        movieRepository.getMoviesByGenre(genreId, new RepositoryCallback<List<Movie>>() {
            @Override
            public void onSuccess(List<Movie> movies) {
                moviesByGenreLoadingLiveData.setValue(false);
                moviesByGenreLiveData.setValue(movies);
            }

            @Override
            public void onError(String errorMessage) {
                moviesByGenreLoadingLiveData.setValue(false);
                moviesByGenreErrorLiveData.setValue(errorMessage);
            }
        });
    }
}
