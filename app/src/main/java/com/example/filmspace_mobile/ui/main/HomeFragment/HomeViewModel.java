package com.example.filmspace_mobile.ui.main.HomeFragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.filmspace_mobile.data.local.UserSessionManager;
import com.example.filmspace_mobile.data.model.movie.Genre;
import com.example.filmspace_mobile.data.model.movie.Movie;
import com.example.filmspace_mobile.data.repository.GenreRepository;
import com.example.filmspace_mobile.data.repository.MovieRepository;
import com.example.filmspace_mobile.data.repository.RepositoryCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class HomeViewModel extends ViewModel {
    private static final String TAG = "HomeViewModel";

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final UserSessionManager sessionManager;

    // LiveData for slider movies
    private final MutableLiveData<List<Movie>> sliderMoviesLiveData = new MutableLiveData<>();
    
    // LiveData for all movies (recommended)
    private final MutableLiveData<List<Movie>> allMoviesLiveData = new MutableLiveData<>();
    
    // LiveData for recommended movies from API
    private final MutableLiveData<List<Movie>> recommendedMoviesLiveData = new MutableLiveData<>();
    
    // LiveData for top rating movies
    private final MutableLiveData<List<Movie>> topRatingMoviesLiveData = new MutableLiveData<>();
    
    // LiveData for best movies (kept for compatibility)
    private final MutableLiveData<List<Movie>> bestMoviesLiveData = new MutableLiveData<>();
    
    // LiveData for upcoming movies (kept for compatibility)
    private final MutableLiveData<List<Movie>> upcomingMoviesLiveData = new MutableLiveData<>();
    
    // LiveData for genres
    private final MutableLiveData<List<Genre>> genresLiveData = new MutableLiveData<>();
    
    // Loading states
    private final MutableLiveData<Boolean> moviesLoadingLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> genresLoadingLiveData = new MutableLiveData<>();
    
    // Error states
    private final MutableLiveData<String> moviesErrorLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> genresErrorLiveData = new MutableLiveData<>();

    @Inject
    public HomeViewModel(MovieRepository movieRepository, GenreRepository genreRepository, UserSessionManager sessionManager) {
        this.movieRepository = movieRepository;
        this.genreRepository = genreRepository;
        this.sessionManager = sessionManager;
    }

    // Getters for LiveData
    public LiveData<List<Movie>> getSliderMovies() {
        return sliderMoviesLiveData;
    }

    public LiveData<List<Movie>> getAllMovies() {
        return allMoviesLiveData;
    }

    public LiveData<List<Movie>> getRecommendedMovies() {
        return recommendedMoviesLiveData;
    }

    public LiveData<List<Movie>> getTopRatingMovies() {
        return topRatingMoviesLiveData;
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

    // Load all movies
    public void loadMovies() {
        moviesLoadingLiveData.setValue(true);

        movieRepository.getAllMovies(new RepositoryCallback<List<Movie>>() {
            @Override
            public void onSuccess(List<Movie> data) {
                moviesLoadingLiveData.setValue(false);
                sliderMoviesLiveData.setValue(data);
                bestMoviesLiveData.setValue(data);
                upcomingMoviesLiveData.setValue(data);
                allMoviesLiveData.setValue(data);
                
                // Sort by rating descending and take top 5-10 movies
                if (data != null && !data.isEmpty()) {
                    List<Movie> topMovies = new ArrayList<>(data);
                    Collections.sort(topMovies, new Comparator<Movie>() {
                        @Override
                        public int compare(Movie m1, Movie m2) {
                            return Double.compare(m2.getRating(), m1.getRating());
                        }
                    });
                    int topCount = Math.min(10, topMovies.size());
                    topRatingMoviesLiveData.setValue(topMovies.subList(0, topCount));
                }
            }

            @Override
            public void onError(String error) {
                moviesLoadingLiveData.setValue(false);
                moviesErrorLiveData.setValue(error);
            }
        });
    }

    // Load recommended movies from API
    public void loadRecommendedMovies() {
        // Only load recommendations if user is logged in
        if (!sessionManager.isLoggedIn()) {
            // User not logged in, clear recommended movies
            recommendedMoviesLiveData.setValue(new ArrayList<>());
            return;
        }
        
        moviesLoadingLiveData.setValue(true);

        movieRepository.getRecommendedMovies(10, new RepositoryCallback<List<Movie>>() {
            @Override
            public void onSuccess(List<Movie> data) {
                moviesLoadingLiveData.setValue(false);
                recommendedMoviesLiveData.setValue(data);
            }

            @Override
            public void onError(String error) {
                moviesLoadingLiveData.setValue(false);
                // Fallback: use all movies sorted by rating as recommended
                if (allMoviesLiveData.getValue() != null) {
                    List<Movie> movies = new ArrayList<>(allMoviesLiveData.getValue());
                    Collections.sort(movies, new Comparator<Movie>() {
                        @Override
                        public int compare(Movie m1, Movie m2) {
                            return Double.compare(m2.getRating(), m1.getRating());
                        }
                    });
                    int count = Math.min(10, movies.size());
                    recommendedMoviesLiveData.setValue(movies.subList(0, count));
                } else {
                    moviesErrorLiveData.setValue(error);
                }
            }
        });
    }

    // Load all genres
    public void loadGenres() {
        genresLoadingLiveData.setValue(true);

        genreRepository.getAllGenres(new RepositoryCallback<List<Genre>>() {
            @Override
            public void onSuccess(List<Genre> data) {
                genresLoadingLiveData.setValue(false);
                genresLiveData.setValue(data);
            }

            @Override
            public void onError(String error) {
                genresLoadingLiveData.setValue(false);
                genresErrorLiveData.setValue(error);
            }
        });
    }

    // Setters for updating data from Fragment (for backward compatibility)
    public void setMovies(List<Movie> movies) {
        this.sliderMoviesLiveData.setValue(movies);
        this.bestMoviesLiveData.setValue(movies);
        this.upcomingMoviesLiveData.setValue(movies);
        this.allMoviesLiveData.setValue(movies);
        
        // Sort by rating descending and take top 5-10 movies
        if (movies != null && !movies.isEmpty()) {
            List<Movie> topMovies = new ArrayList<>(movies);
            Collections.sort(topMovies, new Comparator<Movie>() {
                @Override
                public int compare(Movie m1, Movie m2) {
                    return Double.compare(m2.getRating(), m1.getRating());
                }
            });
            int topCount = Math.min(10, topMovies.size());
            this.topRatingMoviesLiveData.setValue(topMovies.subList(0, topCount));
        }
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
