package com.example.filmspace_mobile.ui.main.HomeFragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.filmspace_mobile.ui.adapters.GenreAdapter;
import com.example.filmspace_mobile.ui.adapters.MovieAdapter;
import com.example.filmspace_mobile.ui.adapters.MovieHorizontalAdapter;
import com.example.filmspace_mobile.ui.adapters.MovieSliderAdapter;
import com.example.filmspace_mobile.R;
import com.example.filmspace_mobile.databinding.FragmentHomeBinding;
import com.example.filmspace_mobile.ui.movie.MovieDetailActivity;
import com.example.filmspace_mobile.ui.movie.MoviesByGenreActivity;
import com.example.filmspace_mobile.viewmodel.GenreViewModel;
import com.example.filmspace_mobile.viewmodel.MovieViewModel;
import com.google.android.material.snackbar.Snackbar;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends Fragment {
    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    
    private MovieSliderAdapter sliderAdapter;
    private MovieAdapter recommendedMoviesAdapter;
    private MovieHorizontalAdapter topRatingMoviesAdapter;
    private GenreAdapter genreAdapter;
    private MovieAdapter upcomingMoviesAdapter;

    // Auto-scroll for ViewPager
    private Handler autoScrollHandler;
    private Runnable autoScrollRunnable;
    private static final long AUTO_SCROLL_DELAY = 3000; // 3 seconds
    private boolean isViewCreated = false;

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
        isViewCreated = true;

        // Initialize ViewModel - Only need HomeViewModel now!
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        setupViewPager();
        setupRecyclerViews();
        setupObservers();
        setupScrollToTopButton();
        setupAutoScroll();
        setupSwipeRefresh();

        // Fetch data from your backend
        loadData();
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

        // Pause auto-scroll when user touches ViewPager
        binding.viewPager.setOnTouchListener((v, event) -> {
            stopAutoScroll();
            v.performClick();
            binding.viewPager.postDelayed(() -> startAutoScroll(), 5000);
            return false;
        });
    }

    private void setupRecyclerViews() {
        // Recommended Movies (horizontal scroll)
        binding.recommendedMoviesRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        recommendedMoviesAdapter = new MovieAdapter(movie -> openMovieDetail(movie.getId()));
        binding.recommendedMoviesRecyclerView.setAdapter(recommendedMoviesAdapter);

        // Top Rating Movies (vertical scroll with horizontal items)
        binding.topRatingMoviesRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false)
        );
        topRatingMoviesAdapter = new MovieHorizontalAdapter(movie -> openMovieDetail(movie.getId()));
        binding.topRatingMoviesRecyclerView.setAdapter(topRatingMoviesAdapter);
    }

    private void setupObservers() {
        // Observe HomeViewModel data for UI updates
        homeViewModel.getSliderMovies().observe(getViewLifecycleOwner(), movies -> {
            if (movies != null && !movies.isEmpty()) {
                sliderAdapter.setMovies(movies);
                // Set to middle copy (second set of the tripled data)
                binding.viewPager.post(() -> binding.viewPager.setCurrentItem(movies.size(), false));
                binding.viewPager.setVisibility(View.VISIBLE);
            } else {
                binding.viewPager.setVisibility(View.GONE);
            }
        });

        // Recommended Movies - show all movies for now
        homeViewModel.getAllMovies().observe(getViewLifecycleOwner(), movies -> {
            if (movies != null && !movies.isEmpty()) {
                // Don't set the adapter here anymore, use the recommended API instead
            }
        });

        // Recommended Movies from API
        homeViewModel.getRecommendedMovies().observe(getViewLifecycleOwner(), movies -> {
            if (movies != null && !movies.isEmpty()) {
                recommendedMoviesAdapter.setMovies(movies);
                binding.recommendedMoviesRecyclerView.setVisibility(View.VISIBLE);
            } else {
                binding.recommendedMoviesRecyclerView.setVisibility(View.GONE);
            }
        });

        // Top Rating Movies - show top 10 movies sorted by rating
        homeViewModel.getTopRatingMovies().observe(getViewLifecycleOwner(), movies -> {
            if (movies != null && !movies.isEmpty()) {
                topRatingMoviesAdapter.setMovies(movies);
                binding.topRatingMoviesRecyclerView.setVisibility(View.VISIBLE);
            } else {
                binding.topRatingMoviesRecyclerView.setVisibility(View.GONE);
            }
        });

        // Observe loading states from HomeViewModel
        homeViewModel.getMoviesLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                // Show/hide shimmer and RecyclerViews
                binding.shimmerBestMovies.getRoot().setVisibility(isLoading ? View.VISIBLE : View.GONE);
                binding.recommendedMoviesProgressBar.setVisibility(View.GONE);
                binding.topRatingMoviesProgressBar.setVisibility(View.GONE);
                binding.recommendedMoviesRecyclerView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
                binding.topRatingMoviesRecyclerView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
                
                // Start/stop shimmer animation
                if (isLoading) {
                    binding.shimmerBestMovies.shimmerMovies1.startShimmer();
                    binding.shimmerBestMovies.shimmerMovies2.startShimmer();
                } else {
                    binding.shimmerBestMovies.shimmerMovies1.stopShimmer();
                    binding.shimmerBestMovies.shimmerMovies2.stopShimmer();
                }
            }
        });

        // Observe error states from HomeViewModel
        homeViewModel.getMoviesError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && getView() != null) {
                Snackbar.make(getView(), error, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry, v -> reloadData())
                    .show();
                binding.shimmerBestMovies.getRoot().setVisibility(View.GONE);
                binding.shimmerBestMovies.shimmerMovies1.stopShimmer();
                binding.shimmerBestMovies.shimmerMovies2.stopShimmer();
                binding.recommendedMoviesProgressBar.setVisibility(View.GONE);
                binding.topRatingMoviesProgressBar.setVisibility(View.GONE);
                binding.recommendedMoviesRecyclerView.setVisibility(View.VISIBLE);
                binding.topRatingMoviesRecyclerView.setVisibility(View.VISIBLE);
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

    private void openMoviesByGenre(int genreId) {
        Intent intent = new Intent(getContext(), MoviesByGenreActivity.class);
        intent.putExtra("genreId", genreId);
        startActivity(intent);
        // Add slide animation
        if (getActivity() != null) {
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    private void setupScrollToTopButton() {
        NestedScrollView scrollView = binding.scrollView;
        
        scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            // Show FAB when scrolled down 300px
            if (scrollY > 300) {
                binding.fabScrollToTop.setVisibility(View.VISIBLE);
                binding.fabScrollToTop.setAlpha(0f);
                binding.fabScrollToTop.animate()
                    .alpha(1f)
                    .setDuration(200)
                    .start();
            } else {
                binding.fabScrollToTop.animate()
                    .alpha(0f)
                    .setDuration(200)
                    .withEndAction(() -> binding.fabScrollToTop.setVisibility(View.GONE))
                    .start();
            }
        });

        binding.fabScrollToTop.setOnClickListener(v -> {
            scrollView.smoothScrollTo(0, 0);
        });
    }

    private void setupAutoScroll() {
        // Use modern Handler constructor
        autoScrollHandler = new Handler(Looper.getMainLooper());
        autoScrollRunnable = new Runnable() {
            @Override
            public void run() {
                // Check if view is still created and visible
                if (isViewCreated && binding != null && sliderAdapter.getItemCount() > 0 
                    && isResumed() && getUserVisibleHint()) {
                    int currentItem = binding.viewPager.getCurrentItem();
                    int nextItem = currentItem + 1;
                    binding.viewPager.setCurrentItem(nextItem, true);
                    // Re-schedule only if still active
                    if (isViewCreated && isResumed()) {
                        autoScrollHandler.postDelayed(this, AUTO_SCROLL_DELAY);
                    }
                }
            }
        };
    }

    private void startAutoScroll() {
        if (autoScrollHandler != null && autoScrollRunnable != null && isViewCreated) {
            // Remove any pending callbacks first
            autoScrollHandler.removeCallbacks(autoScrollRunnable);
            autoScrollHandler.postDelayed(autoScrollRunnable, AUTO_SCROLL_DELAY);
        }
    }

    private void stopAutoScroll() {
        if (autoScrollHandler != null && autoScrollRunnable != null) {
            autoScrollHandler.removeCallbacks(autoScrollRunnable);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isViewCreated) {
            startAutoScroll();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopAutoScroll();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isViewCreated = false;
        stopAutoScroll();
        // Clean up handler to prevent memory leaks
        if (autoScrollHandler != null) {
            autoScrollHandler.removeCallbacksAndMessages(null);
        }
        binding = null;
    }

    private void setupSwipeRefresh() {
        binding.swipeRefreshLayout.setColorSchemeResources(
            R.color.color_main,
            R.color.white,
            R.color.color_main
        );
        
        binding.swipeRefreshLayout.setOnRefreshListener(this::refreshData);
    }

    private void loadData() {
        homeViewModel.loadMovies();
        homeViewModel.loadGenres();
        homeViewModel.loadRecommendedMovies();
    }

    private void refreshData() {
        // Show refresh indicator
        if (binding != null) {
            binding.swipeRefreshLayout.setRefreshing(true);
        }
        
        // Reload data
        homeViewModel.loadMovies();
        homeViewModel.loadGenres();
        homeViewModel.loadRecommendedMovies();
        
        // Hide refresh indicator after a short delay using lifecycle-aware approach
        if (getView() != null && isAdded()) {
            getView().postDelayed(() -> {
                // Double-check view is still valid
                if (isViewCreated && binding != null && isAdded()) {
                    binding.swipeRefreshLayout.setRefreshing(false);
                }
            }, 1000);
        }
    }
    
    /**
     * Public method to reload data (called from MainActivity on retry)
     */
    public void reloadData() {
        loadData();
    }
}