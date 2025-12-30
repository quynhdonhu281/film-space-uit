package com.example.filmspace_mobile.ui.main.HomeFragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.os.Handler;
import android.os.Looper;

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
    private final UserSessionManager sessionManager;
    
    // Handler for staggering network requests to prevent ANR
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    // LiveData for slider movies (ViewPager2)
    private final MutableLiveData<List<Movie>> sliderMoviesLiveData = new MutableLiveData<>();
    
    // LiveData for recommended movies from API
    private final MutableLiveData<List<Movie>> recommendedMoviesLiveData = new MutableLiveData<>();
    
    // LiveData for top rating movies
    private final MutableLiveData<List<Movie>> topRatingMoviesLiveData = new MutableLiveData<>();
    
    // Loading state
    private final MutableLiveData<Boolean> moviesLoadingLiveData = new MutableLiveData<>();
    
    // Error state
    private final MutableLiveData<String> moviesErrorLiveData = new MutableLiveData<>();

    @Inject
    public HomeViewModel(MovieRepository movieRepository, UserSessionManager sessionManager) {
        this.movieRepository = movieRepository;
        this.sessionManager = sessionManager;
    }

    // Getters for LiveData
    public LiveData<List<Movie>> getSliderMovies() {
        return sliderMoviesLiveData;
    }

    public LiveData<List<Movie>> getRecommendedMovies() {
        return recommendedMoviesLiveData;
    }

    public LiveData<List<Movie>> getTopRatingMovies() {
        return topRatingMoviesLiveData;
    }

    public LiveData<Boolean> getMoviesLoading() {
        return moviesLoadingLiveData;
    }

    public LiveData<String> getMoviesError() {
        return moviesErrorLiveData;
    }

    // Load slider and top rating movies
    public void loadMovies() {
        moviesLoadingLiveData.setValue(true);

        movieRepository.getAllMovies(new RepositoryCallback<List<Movie>>() {
            @Override
            public void onSuccess(List<Movie> data) {
                moviesLoadingLiveData.setValue(false);
                
                // Limit movies to prevent memory issues and UI lag
                // Keep first 50 movies for slider to reduce memory footprint
                List<Movie> limitedData = data;
                if (data != null && data.size() > 50) {
                    limitedData = new ArrayList<>(data.subList(0, 50));
                }
                
                // Set slider movies
                sliderMoviesLiveData.setValue(limitedData);
                
                // Sort by rating descending and take top 10 movies
                if (limitedData != null && !limitedData.isEmpty()) {
                    List<Movie> topMovies = new ArrayList<>(limitedData);
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
        
        // Stagger the recommended movies load - wait 300ms after main movies load
        // This prevents multiple simultaneous network requests that can cause ANR
        mainHandler.postDelayed(() -> {
            movieRepository.getRecommendedMovies(10, new RepositoryCallback<List<Movie>>() {
                @Override
                public void onSuccess(List<Movie> data) {
                    recommendedMoviesLiveData.setValue(data);
                }

                @Override
                public void onError(String error) {
                    moviesErrorLiveData.setValue(error);
                    // Fallback: show empty list if API fails
                    recommendedMoviesLiveData.setValue(new ArrayList<>());
                }
            });
        }, 300); // 300ms delay after main movies load completes
    }

    // Setter for updating slider and top rating movies manually
    public void setMovies(List<Movie> movies) {
        this.sliderMoviesLiveData.setValue(movies);
        
        // Sort by rating descending and take top 10 movies
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

    public void setMoviesLoading(boolean isLoading) {
        this.moviesLoadingLiveData.setValue(isLoading);
    }

    public void setMoviesError(String error) {
        this.moviesErrorLiveData.setValue(error);
    }
}
