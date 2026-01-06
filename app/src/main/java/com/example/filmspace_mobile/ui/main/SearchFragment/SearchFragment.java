package com.example.filmspace_mobile.ui.main.SearchFragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.filmspace_mobile.R;
import com.example.filmspace_mobile.data.model.movie.Movie;
import com.example.filmspace_mobile.data.model.movie.Cast;
import com.example.filmspace_mobile.data.repository.MovieRepository;
import com.example.filmspace_mobile.data.repository.RepositoryCallback;
import com.example.filmspace_mobile.databinding.FragmentSearchBinding;
import com.example.filmspace_mobile.ui.adapters.CastAdapter;
import com.example.filmspace_mobile.ui.adapters.GenreAdapter;
import com.example.filmspace_mobile.ui.adapters.SearchGridAdapter;
import com.example.filmspace_mobile.ui.movie.MovieDetailActivity;
import com.example.filmspace_mobile.viewmodel.GenreViewModel;
import com.example.filmspace_mobile.viewmodel.MovieViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject; // Sử dụng javax thay vì jakarta
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SearchFragment extends Fragment {
    private FragmentSearchBinding binding;
    private MovieViewModel movieViewModel;
    private GenreViewModel genreViewModel;

    private GenreAdapter genreAdapter;
    private CastAdapter actorAdapter;

    private SearchGridAdapter searchGridAdapter;

    @Inject
    MovieRepository movieRepository;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        movieViewModel = new ViewModelProvider(this).get(MovieViewModel.class);
        genreViewModel = new ViewModelProvider(this).get(GenreViewModel.class);

        setupExploreRecyclerViews();
        setupSearchRecyclerView();
        setupSearchView();
        setupObservers();

        // Load data ban đầu
        movieViewModel.fetchAllMovies();
        genreViewModel.fetchAllGenres();
        loadAllCasts();
    }

    private void setupExploreRecyclerViews() {
        // 1. Genres
        binding.genresRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        genreAdapter = new GenreAdapter(genre -> {
            binding.searchView.setQuery(genre.getName(), true);
        });
        binding.genresRecyclerView.setAdapter(genreAdapter);

        // 2. Actors (Lấy từ API riêng)
        binding.actorsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        actorAdapter = new CastAdapter(new ArrayList<>(), cast -> {
            binding.searchView.setQuery(cast.getName(), true);
        });
        binding.actorsRecyclerView.setAdapter(actorAdapter);


    }

    private void setupSearchRecyclerView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        binding.searchResultsRecyclerView.setLayoutManager(gridLayoutManager);
        searchGridAdapter = new SearchGridAdapter(movie -> openMovieDetail(movie.getId()));
        binding.searchResultsRecyclerView.setAdapter(searchGridAdapter);
    }

    private void setupSearchView() {
        TextView searchEditText = binding.searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        if (searchEditText != null) {
            searchEditText.setTextColor(Color.BLACK);
            searchEditText.setHintTextColor(Color.GRAY);
        }

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText == null || newText.trim().isEmpty()) {
                    showInitialState();
                } else {
                    performSearch(newText);
                }
                return true;
            }
        });
    }

    private void setupObservers() {
        // Observe thể loại
        genreViewModel.getGenres().observe(getViewLifecycleOwner(), genres -> {
            if (genres != null) genreAdapter.setGenres(genres);
        });

        // Chỉ observe phim để phục vụ việc search local
        movieViewModel.getAllMovies().observe(getViewLifecycleOwner(), movies -> {
            // Dữ liệu đã được load và sẵn sàng cho search
        });
    }

    private void loadAllCasts() {
        movieRepository.getAllCasts(new RepositoryCallback<List<Cast>>() {
            @Override
            public void onSuccess(List<Cast> casts) {
                if (isAdded() && casts != null) {
                    actorAdapter.updateData(casts);
                }
            }

            @Override
            public void onError(String error) {
                android.util.Log.e("SearchFragment", "Error loading casts: " + error);
            }
        });
    }

    private void performSearch(String query) {
        String finalQuery = query.toLowerCase().trim();
        movieViewModel.getAllMovies().observe(getViewLifecycleOwner(), allMovies -> {
            if (allMovies != null) {
                List<Movie> filtered = allMovies.stream()
                        .filter(movie ->
                                (movie.getTitle() != null && movie.getTitle().toLowerCase().contains(finalQuery))  ||
                                        (movie.getGenres() != null && movie.getGenres().stream().anyMatch(g -> g.getName().toLowerCase().contains(finalQuery))) ||
                                        (movie.getCasts() != null && movie.getCasts().stream().anyMatch(c -> c.getName().toLowerCase().contains(finalQuery)))
                        ).collect(Collectors.toList());

                if (filtered.isEmpty()) showEmptyState();
                else showSearchResults(filtered);
            }
        });
    }

    private void showInitialState() {
        binding.initialLayout.setVisibility(View.VISIBLE);
        binding.searchResultsLayout.setVisibility(View.GONE);
        binding.emptyLayout.setVisibility(View.GONE);
    }

    private void showSearchResults(List<Movie> movies) {
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