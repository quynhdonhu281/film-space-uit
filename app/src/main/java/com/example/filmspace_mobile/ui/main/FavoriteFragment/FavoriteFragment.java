package com.example.filmspace_mobile.ui.main.FavoriteFragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.filmspace_mobile.R;
import com.example.filmspace_mobile.data.local.UserSessionManager;
import com.example.filmspace_mobile.data.repository.HistoryRepository;
import com.example.filmspace_mobile.data.repository.RepositoryCallback;
import com.example.filmspace_mobile.databinding.FragmentFavoriteBinding;
import com.example.filmspace_mobile.ui.adapters.SearchGridAdapter;
import com.example.filmspace_mobile.ui.movie.MovieDetailActivity;
import com.example.filmspace_mobile.viewmodel.MovieViewModel;
import com.google.android.material.snackbar.Snackbar;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Fragment to display watch history in grid layout
 */
@AndroidEntryPoint
public class FavoriteFragment extends Fragment {
    private FragmentFavoriteBinding binding;
    private MovieViewModel movieViewModel;
    private SearchGridAdapter historyAdapter;

    @Inject
    UserSessionManager sessionManager;

    @Inject
    HistoryRepository historyRepository;

    public FavoriteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel
        movieViewModel = new ViewModelProvider(this).get(MovieViewModel.class);

        setupRecyclerView();
        loadMovies();
    }

    private void setupRecyclerView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        binding.historyRecyclerView.setLayoutManager(gridLayoutManager);
        historyAdapter = new SearchGridAdapter(movie -> openMovieDetail(movie.getId()));
        binding.historyRecyclerView.setAdapter(historyAdapter);
    }

    private void loadMovies() {
        // Check if user is logged in
        if (!sessionManager.isLoggedIn()) {
            binding.historyRecyclerView.setVisibility(View.GONE);
            binding.emptyStateLayout.setVisibility(View.VISIBLE);
            return;
        }

        // Show loading
        binding.historyRecyclerView.setVisibility(View.GONE);
        binding.emptyStateLayout.setVisibility(View.GONE);

        // Load watch history from API
        historyRepository.getWatchHistory(1, 50, new RepositoryCallback<>() {
            @Override
            public void onSuccess(com.example.filmspace_mobile.data.model.history.WatchHistoryResponse response) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (response.getData() != null && !response.getData().isEmpty()) {
                            historyAdapter.setMovies(response.getData());
                            binding.historyRecyclerView.setVisibility(View.VISIBLE);
                            binding.emptyStateLayout.setVisibility(View.GONE);
                        } else {
                            binding.historyRecyclerView.setVisibility(View.GONE);
                            binding.emptyStateLayout.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (getView() != null) {
                            Snackbar.make(getView(), R.string.failed_to_load_watch_history, Snackbar.LENGTH_SHORT).show();
                        }
                        binding.historyRecyclerView.setVisibility(View.GONE);
                        binding.emptyStateLayout.setVisibility(View.VISIBLE);
                    });
                }
            }
        });
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