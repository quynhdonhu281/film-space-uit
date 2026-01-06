package com.example.filmspace_mobile.ui.main.HomeFragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;

import com.example.filmspace_mobile.R;
import com.example.filmspace_mobile.data.repository.MovieRepository;
import com.example.filmspace_mobile.databinding.FragmentHomeBinding;
import com.example.filmspace_mobile.ui.adapters.MovieAdapter;
import com.example.filmspace_mobile.ui.adapters.MovieHorizontalAdapter;
import com.example.filmspace_mobile.ui.adapters.MovieSliderAdapter;
import com.example.filmspace_mobile.ui.movie.MovieDetailActivity;
import com.google.android.material.snackbar.Snackbar;

import javax.inject.Inject; // [QUAN TRỌNG] Dùng javax thay vì jakarta cho Android Hilt

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends Fragment {
    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    private MovieSliderAdapter sliderAdapter;

    // Inject Repository để truyền vào Adapter
    @Inject
    MovieRepository movieRepository;

    private MovieAdapter recommendedMoviesAdapter;
    private MovieHorizontalAdapter topRatingMoviesAdapter;

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

        // Initialize ViewModel
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        setupViewPager();
        setupRecyclerViews();
        setupObservers();
        setupScrollToTopButton();
        setupAutoScroll();
        setupSwipeRefresh();

        // Defer heavy data loading until UI is ready
        binding.getRoot().post(this::loadData);
    }

    private void setupViewPager() {
        sliderAdapter = new MovieSliderAdapter(binding.viewPager, movie -> {
            openMovieDetail(movie.getId());
        });
        binding.viewPager.setAdapter(sliderAdapter);
        binding.viewPager.setClipToPadding(false);
        binding.viewPager.setClipChildren(false);
        binding.viewPager.setOffscreenPageLimit(1);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.15f);
        });
        binding.viewPager.setPageTransformer(compositePageTransformer);

        binding.viewPager.post(() -> {
            if (sliderAdapter.getItemCount() > 0) {
                int middlePosition = sliderAdapter.getItemCount() / 2;
                binding.viewPager.setCurrentItem(middlePosition, false);
            }
        });

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

        // [ĐÚNG] Truyền movieRepository vào Constructor
        recommendedMoviesAdapter = new MovieAdapter(
                movie -> openMovieDetail(movie.getId()),
                movieRepository
        );
        binding.recommendedMoviesRecyclerView.setAdapter(recommendedMoviesAdapter);

        // Top Rating Movies (vertical scroll)
        binding.topRatingMoviesRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false)
        );
        topRatingMoviesAdapter = new MovieHorizontalAdapter(movie -> openMovieDetail(movie.getId()), movieRepository);
        binding.topRatingMoviesRecyclerView.setAdapter(topRatingMoviesAdapter);
    }

    private void setupObservers() {
        // Slider Movies
        homeViewModel.getSliderMovies().observe(getViewLifecycleOwner(), movies -> {
            if (movies != null && !movies.isEmpty()) {
                sliderAdapter.setMovies(movies);
                binding.viewPager.post(() -> binding.viewPager.setCurrentItem(movies.size(), false));
                binding.viewPager.setVisibility(View.VISIBLE);
            } else {
                binding.viewPager.setVisibility(View.GONE);
            }
        });

        // Recommended Movies
        homeViewModel.getRecommendedMovies().observe(getViewLifecycleOwner(), movies -> {
            if (movies != null && !movies.isEmpty()) {
                // [SỬA LẠI] Chỉ cần truyền movies, repository đã có trong constructor rồi
                recommendedMoviesAdapter.setMovies(movies);
                binding.recommendedMoviesRecyclerView.setVisibility(View.VISIBLE);
            } else {
                binding.recommendedMoviesRecyclerView.setVisibility(View.GONE);
            }
        });

        // Top Rating Movies
        homeViewModel.getTopRatingMovies().observe(getViewLifecycleOwner(), movies -> {
            if (movies != null && !movies.isEmpty()) {
                topRatingMoviesAdapter.setMovies(movies);
                binding.topRatingMoviesRecyclerView.setVisibility(View.VISIBLE);
            } else {
                binding.topRatingMoviesRecyclerView.setVisibility(View.GONE);
            }
        });

        // Loading State
        homeViewModel.getMoviesLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                binding.shimmerBestMovies.getRoot().setVisibility(isLoading ? View.VISIBLE : View.GONE);
                binding.recommendedMoviesProgressBar.setVisibility(View.GONE);
                binding.recommendedMoviesRecyclerView.setVisibility(isLoading ? View.GONE : View.VISIBLE);

                binding.shimmerTopRatingMovies.getRoot().setVisibility(isLoading ? View.VISIBLE : View.GONE);
                binding.topRatingMoviesProgressBar.setVisibility(View.GONE);
                binding.topRatingMoviesRecyclerView.setVisibility(isLoading ? View.GONE : View.VISIBLE);

                binding.shimmerViewPager.getRoot().setVisibility(isLoading ? View.VISIBLE : View.GONE);
                binding.viewPager.setVisibility(isLoading ? View.GONE : View.VISIBLE);

                if (isLoading) {
                    binding.shimmerBestMovies.shimmerMovies1.startShimmer();
                    binding.shimmerBestMovies.shimmerMovies2.startShimmer();
                    binding.shimmerTopRatingMovies.shimmerMovies1.startShimmer();
                    binding.shimmerTopRatingMovies.shimmerMovies2.startShimmer();
                } else {
                    binding.shimmerBestMovies.shimmerMovies1.stopShimmer();
                    binding.shimmerBestMovies.shimmerMovies2.stopShimmer();
                    binding.shimmerTopRatingMovies.shimmerMovies1.stopShimmer();
                    binding.shimmerTopRatingMovies.shimmerMovies2.stopShimmer();
                }
            }
        });

        // Error State
        homeViewModel.getMoviesError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && getView() != null) {
                Snackbar.make(getView(), error, Snackbar.LENGTH_LONG)
                        .setAction(R.string.retry, v -> reloadData())
                        .show();

                binding.shimmerBestMovies.getRoot().setVisibility(View.GONE);
                binding.shimmerTopRatingMovies.getRoot().setVisibility(View.GONE);

                binding.recommendedMoviesRecyclerView.setVisibility(View.VISIBLE);
                binding.topRatingMoviesRecyclerView.setVisibility(View.VISIBLE);
                binding.viewPager.setVisibility(View.VISIBLE);
            }
        });
    }

    private void openMovieDetail(int movieId) {
        Intent intent = new Intent(getContext(), MovieDetailActivity.class);
        intent.putExtra("movieId", movieId);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    private void setupScrollToTopButton() {
        NestedScrollView scrollView = binding.scrollView;
        scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY > 300) {
                binding.fabScrollToTop.setVisibility(View.VISIBLE);
                binding.fabScrollToTop.setAlpha(0f);
                binding.fabScrollToTop.animate().alpha(1f).setDuration(200).start();
            } else {
                binding.fabScrollToTop.animate().alpha(0f).setDuration(200)
                        .withEndAction(() -> binding.fabScrollToTop.setVisibility(View.GONE)).start();
            }
        });
        binding.fabScrollToTop.setOnClickListener(v -> scrollView.smoothScrollTo(0, 0));
    }

    private void setupAutoScroll() {
        autoScrollHandler = new Handler(Looper.getMainLooper());
        autoScrollRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isViewCreated || binding == null || sliderAdapter.getItemCount() == 0) return;

                if (isResumed() && getUserVisibleHint()) {
                    try {
                        int currentItem = binding.viewPager.getCurrentItem();
                        binding.viewPager.setCurrentItem(currentItem + 1, true);
                        if (isViewCreated && isResumed() && autoScrollHandler != null) {
                            autoScrollHandler.postDelayed(this, AUTO_SCROLL_DELAY);
                        }
                    } catch (Exception e) {
                        android.util.Log.e("HomeFragment", "Auto-scroll error", e);
                    }
                }
            }
        };
    }

    private void startAutoScroll() {
        if (autoScrollHandler != null && autoScrollRunnable != null && isViewCreated && sliderAdapter.getItemCount() > 0) {
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
        if (isViewCreated) startAutoScroll();
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
        if (autoScrollHandler != null) {
            autoScrollHandler.removeCallbacksAndMessages(null);
        }
        binding = null;
    }

    private void setupSwipeRefresh() {
        binding.swipeRefreshLayout.setColorSchemeResources(R.color.color_main, R.color.white, R.color.color_main);
        binding.swipeRefreshLayout.setOnRefreshListener(this::refreshData);
    }

    private void loadData() {
        homeViewModel.loadMovies();
        homeViewModel.loadRecommendedMovies();
    }

    private void refreshData() {
        if (binding != null) binding.swipeRefreshLayout.setRefreshing(true);
        homeViewModel.loadMovies();
        homeViewModel.loadRecommendedMovies();
        if (getView() != null && isAdded()) {
            getView().postDelayed(() -> {
                if (isViewCreated && binding != null && isAdded()) {
                    binding.swipeRefreshLayout.setRefreshing(false);
                }
            }, 1000);
        }
    }

    public void reloadData() {
        loadData();
    }
}