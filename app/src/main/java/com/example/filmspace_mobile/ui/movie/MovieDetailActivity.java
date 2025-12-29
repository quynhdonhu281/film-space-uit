package com.example.filmspace_mobile.ui.movie;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.example.filmspace_mobile.R;
import com.example.filmspace_mobile.data.local.UserSessionManager;
import com.example.filmspace_mobile.data.model.movie.Movie;
import com.example.filmspace_mobile.data.repository.HistoryRepository;
import com.example.filmspace_mobile.data.repository.MovieRepository;
import com.example.filmspace_mobile.data.repository.RepositoryCallback;
import com.example.filmspace_mobile.ui.adapters.MovieDetailsPagerAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


import com.bumptech.glide.Glide;
import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MovieDetailActivity extends AppCompatActivity {

    private static final String KEY_MOVIE_ID = "movieId";

    private ImageView moviePoster;
    private ImageButton btnBack;
    private TextView movieTitle;
    private MaterialButton btnPlay;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ProgressBar progressBar;

    private Movie movie;
    private int currentMovieId = -1;
    private MovieDetailsPagerAdapter pagerAdapter;
    private TabLayoutMediator tabLayoutMediator;
    
    @Inject
    MovieRepository movieRepository;

    @Inject
    HistoryRepository historyRepository;

    @Inject
    UserSessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        initViews();        
        setupClickListeners();
        
        // Check if we have saved movie data
        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_MOVIE_ID)) {
            currentMovieId = savedInstanceState.getInt(KEY_MOVIE_ID, -1);
        }
        
        loadMovieData();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (currentMovieId != -1) {
            outState.putInt(KEY_MOVIE_ID, currentMovieId);
        }
    }

    private void initViews() {
        moviePoster = findViewById(R.id.moviePoster);
        btnBack = findViewById(R.id.btnBack);
        movieTitle = findViewById(R.id.movieTitle);
        btnPlay = findViewById(R.id.btnPlay);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        progressBar = findViewById(R.id.progressBar);
    }

    private void loadMovieData() {
        // Try to get movieId from saved state first, then from intent
        int movieId = currentMovieId;

        if (movieId == -1) {
            movieId = getIntent().getIntExtra("movieId", -1);
        }
        
        if (movieId == -1) {
            View rootView = findViewById(android.R.id.content);
            if (rootView != null) {
                Snackbar.make(rootView, R.string.invalid_movie_id, Snackbar.LENGTH_SHORT).show();
            }
            finish();
            return;
        }
        
        // Store the movieId
        currentMovieId = movieId;

        // Show loading
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        viewPager.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);

        // Add to watch history if user is logged in
        if (sessionManager.isLoggedIn()) {
            addToWatchHistory(movieId);
        }

        // Fetch movie details from API
        movieRepository.getMovieById(movieId, new RepositoryCallback<Movie>() {
            @Override
            public void onSuccess(Movie data) {
                movie = data;
                
                // Hide loading
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                viewPager.setVisibility(View.VISIBLE);
                tabLayout.setVisibility(View.VISIBLE);
                
                displayMovieInfo();
                setupViewPager();
            }

            @Override
            public void onError(String errorMessage) {
                // Hide loading
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                
                View rootView = findViewById(android.R.id.content);
                if (rootView != null) {
                    Snackbar.make(rootView, errorMessage, Snackbar.LENGTH_LONG).show();
                }
                Toast.makeText(MovieDetailActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void addToWatchHistory(int movieId) {
        historyRepository.addToHistory(movieId, new RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                // Silently added to history
            }

            @Override
            public void onError(String error) {
                // Silently fail - don't disturb user experience
            }
        });
    }

    private void displayMovieInfo() {
        movieTitle.setText(movie.getTitle());

        // Load poster image with Glide, handle empty/null URL
        String posterUrl = movie.getPosterUrl();
        if (posterUrl != null && !posterUrl.trim().isEmpty()) {
            Glide.with(this)
                .load(posterUrl)
                .placeholder(R.drawable.movie_poster_placeholder)
                .error(R.drawable.movie_poster_placeholder)
                .into(moviePoster);
        } else {
            // Show placeholder if no poster URL
            moviePoster.setImageResource(R.drawable.movie_poster_placeholder);
        }
    }

    private void setupViewPager() {
        if (movie == null) {
            return;
        }
        
        // Detach previous mediator if exists
        if (tabLayoutMediator != null) {
            tabLayoutMediator.detach();
        }
        
        pagerAdapter = new MovieDetailsPagerAdapter(this, movie);
        viewPager.setAdapter(pagerAdapter);
        
        // Keep all 3 pages in memory to prevent fragment recreation
        viewPager.setOffscreenPageLimit(2);
        
        // Set user input enabled to prevent swipe issues
        viewPager.setUserInputEnabled(true);

        // Connect TabLayout with ViewPager2 and store the mediator
        tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("About");
                    break;
                case 1:
                    tab.setText("Episodes");
                    break;
                case 2:
                    tab.setText("Review");
                    break;
            }
        });
        tabLayoutMediator.attach();

        // Set default tab to About (position 0)
        viewPager.setCurrentItem(0, false);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnPlay.setOnClickListener(v -> {
            // TODO: Navigate to video player activity
            View rootView = findViewById(android.R.id.content);
            if (rootView != null) {
                Snackbar.make(rootView, R.string.opening_video_player, Snackbar.LENGTH_SHORT).show();
            }
            // Intent intent = new Intent(this, VideoPlayerActivity.class);
            // intent.putExtra("MOVIE_ID", movie.getId());
            // startActivity(intent);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Detach TabLayoutMediator to prevent memory leaks
        if (tabLayoutMediator != null) {
            tabLayoutMediator.detach();
            tabLayoutMediator = null;
        }
    }

    public Movie getMovie() {
        return movie;
    }
}