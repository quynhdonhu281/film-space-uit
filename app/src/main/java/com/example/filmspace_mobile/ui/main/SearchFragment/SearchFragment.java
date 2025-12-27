package com.example.filmspace_mobile.ui.main.SearchFragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.filmspace_mobile.R;
import com.example.filmspace_mobile.databinding.FragmentSearchBinding;
import com.example.filmspace_mobile.ui.adapters.MovieHorizontalAdapter;
import com.example.filmspace_mobile.ui.adapters.SearchGridAdapter;
import com.example.filmspace_mobile.ui.main.HomeFragment.HomeViewModel;
import com.example.filmspace_mobile.ui.movie.MovieDetailActivity;
import com.example.filmspace_mobile.viewmodel.MovieViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SearchFragment extends Fragment {
    FragmentSearchBinding binding;
    private MovieViewModel movieViewModel;
    private HomeViewModel homeViewModel;
    private MovieHorizontalAdapter topRatingAdapter;
    private SearchGridAdapter searchGridAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModels
        movieViewModel = new ViewModelProvider(this).get(MovieViewModel.class);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        setupRecyclerViews();
        setupSearchView();
        setupObservers();
        
        // Load initial data
        movieViewModel.fetchAllMovies();
    }

    private void setupRecyclerViews() {
        // Setup top rating RecyclerView (initial state)
        binding.topRatingRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false)
        );
        topRatingAdapter = new MovieHorizontalAdapter(movie -> openMovieDetail(movie.getId()));
        binding.topRatingRecyclerView.setAdapter(topRatingAdapter);

        // Setup search results RecyclerView (grid layout)
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        binding.searchResultsRecyclerView.setLayoutManager(gridLayoutManager);
        searchGridAdapter = new SearchGridAdapter(movie -> openMovieDetail(movie.getId()));
        binding.searchResultsRecyclerView.setAdapter(searchGridAdapter);
    }

    private void setupSearchView() {
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText == null || newText.trim().isEmpty()) {
                    // Show initial state when search is empty
                    showInitialState();
                } else {
                    // Perform search as user types
                    performSearch(newText);
                }
                return true;
            }
        });

        // Handle search view close button
        binding.searchView.setOnCloseListener(() -> {
            showInitialState();
            return false;
        });
    }

    private void setupObservers() {
        // Observe all movies from MovieViewModel
        movieViewModel.getAllMovies().observe(getViewLifecycleOwner(), movies -> {
            if (movies != null && !movies.isEmpty()) {
                homeViewModel.setMovies(movies);
            }
        });

        // Observe top rating movies for initial display
        homeViewModel.getTopRatingMovies().observe(getViewLifecycleOwner(), movies -> {
            if (movies != null && !movies.isEmpty()) {
                topRatingAdapter.setMovies(movies);
            }
        });
    }

    private void performSearch(String query) {
        if (query == null || query.trim().isEmpty()) {
            showInitialState();
            return;
        }

        // Get all movies and filter by search query
        movieViewModel.getAllMovies().observe(getViewLifecycleOwner(), allMovies -> {
            if (allMovies != null) {
                List<com.example.filmspace_mobile.data.model.movie.Movie> filteredMovies = 
                    allMovies.stream()
                        .filter(movie -> movie.getTitle().toLowerCase()
                                .contains(query.toLowerCase().trim()))
                        .collect(Collectors.toList());

                if (filteredMovies.isEmpty()) {
                    showEmptyState();
                } else {
                    showSearchResults(filteredMovies);
                }
            }
        });
    }

    private void showInitialState() {
        binding.initialLayout.setVisibility(View.VISIBLE);
        binding.searchResultsLayout.setVisibility(View.GONE);
        binding.emptyLayout.setVisibility(View.GONE);
    }

    private void showSearchResults(List<com.example.filmspace_mobile.data.model.movie.Movie> movies) {
        binding.initialLayout.setVisibility(View.GONE);
        binding.searchResultsLayout.setVisibility(View.VISIBLE);
        binding.emptyLayout.setVisibility(View.GONE);
        searchGridAdapter.setMovies(movies);
    }

    private void showEmptyState() {
        binding.initialLayout.setVisibility(View.GONE);
        binding.searchResultsLayout.setVisibility(View.GONE);
        binding.emptyLayout.setVisibility(View.VISIBLE);
    }

    private void openMovieDetail(int movieId) {
        Intent intent = new Intent(getContext(), MovieDetailActivity.class);
        intent.putExtra("movieId", movieId);
        startActivity(intent);
        // Add slide animation
        if (getActivity() != null) {
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}