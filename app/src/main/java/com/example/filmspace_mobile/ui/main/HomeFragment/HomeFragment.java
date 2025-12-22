package com.example.filmspace_mobile.ui.main.HomeFragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.filmspace_mobile.Adapter.GenreAdapter;
import com.example.filmspace_mobile.Adapter.MovieAdapter;
import com.example.filmspace_mobile.Adapter.MovieSliderAdapter;
import com.example.filmspace_mobile.databinding.FragmentHomeBinding;
import com.example.filmspace_mobile.ui.movie.MovieDetailActivity;
import com.example.filmspace_mobile.ui.movie.MoviesByGenreActivity;
import com.example.filmspace_mobile.viewmodel.GenreViewModel;
import com.example.filmspace_mobile.viewmodel.MovieViewModel;

public class HomeFragment extends Fragment {
    private MovieViewModel movieViewModel;
    private GenreViewModel genreViewModel;
    private FragmentHomeBinding binding;
    
    private MovieSliderAdapter sliderAdapter;
    private MovieAdapter bestMoviesAdapter;
    private GenreAdapter genreAdapter;
    private MovieAdapter upcomingMoviesAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModels
        movieViewModel = new ViewModelProvider(this).get(MovieViewModel.class);
        genreViewModel = new ViewModelProvider(this).get(GenreViewModel.class);

        setupViewPager();
        setupRecyclerViews();
        setupObservers();

        // Fetch data from your backend
        movieViewModel.fetchAllMovies();
        genreViewModel.fetchGenres();
    }

    private void setupViewPager() {
        sliderAdapter = new MovieSliderAdapter(binding.viewPager, movie -> {
            openMovieDetail(movie.getId());
        });
        binding.viewPager.setAdapter(sliderAdapter);
        binding.viewPager.setClipToPadding(false);
        binding.viewPager.setClipChildren(false);
        binding.viewPager.setOffscreenPageLimit(3);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.15f);
        });
        binding.viewPager.setPageTransformer(compositePageTransformer);

        // Set to middle to enable infinite scroll
        binding.viewPager.post(() -> {
            if (sliderAdapter.getItemCount() > 0) {
                binding.viewPager.setCurrentItem(Integer.MAX_VALUE / 2, false);
            }
        });
    }

    private void setupRecyclerViews() {
        // Best Movies
        binding.bestMoviesRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        bestMoviesAdapter = new MovieAdapter(movie -> openMovieDetail(movie.getId()));
        binding.bestMoviesRecyclerView.setAdapter(bestMoviesAdapter);

        // Categories/Genres
        binding.categoryRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        genreAdapter = new GenreAdapter(genre -> openMoviesByGenre(genre.getId()));
        binding.categoryRecyclerView.setAdapter(genreAdapter);

        // Upcoming Movies
        binding.upcomingMoviesRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        upcomingMoviesAdapter = new MovieAdapter(movie -> openMovieDetail(movie.getId()));
        binding.upcomingMoviesRecyclerView.setAdapter(upcomingMoviesAdapter);
    }

    private void setupObservers() {
        // Observe all movies
        movieViewModel.getAllMovies().observe(getViewLifecycleOwner(), movies -> {
            if (movies != null && !movies.isEmpty()) {
                // Update slider
                sliderAdapter.setMovies(movies);
                binding.viewPager.post(() -> binding.viewPager.setCurrentItem(Integer.MAX_VALUE / 2, false));

                // Update best movies
                bestMoviesAdapter.setMovies(movies);
                binding.bestMoviesProgressBar.setVisibility(View.GONE);

                // Update upcoming movies
                upcomingMoviesAdapter.setMovies(movies);
                binding.upcomingMoviesProgressBar.setVisibility(View.GONE);
            }
        });

        // Observe movies error
        movieViewModel.getMoviesError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                binding.bestMoviesProgressBar.setVisibility(View.GONE);
                binding.upcomingMoviesProgressBar.setVisibility(View.GONE);
            }
        });

        // Observe movies loading
        movieViewModel.getMoviesLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                binding.bestMoviesProgressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                binding.upcomingMoviesProgressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });

        // Observe genres
        genreViewModel.getGenres().observe(getViewLifecycleOwner(), genres -> {
            if (genres != null && !genres.isEmpty()) {
                genreAdapter.setGenres(genres);
                binding.categoryProgressBar.setVisibility(View.GONE);
            }
        });

        // Observe genres error
        genreViewModel.getGenresError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                binding.categoryProgressBar.setVisibility(View.GONE);
            }
        });

        // Observe genres loading
        genreViewModel.getGenresLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                binding.categoryProgressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void openMovieDetail(int movieId) {
        Intent intent = new Intent(getContext(), MovieDetailActivity.class);
        intent.putExtra("movieId", movieId);
        startActivity(intent);
    }

    private void openMoviesByGenre(int genreId) {
        Intent intent = new Intent(getContext(), MoviesByGenreActivity.class);
        intent.putExtra("genreId", genreId);
        startActivity(intent);
    }
}