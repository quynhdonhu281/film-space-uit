package com.example.filmspace_mobile.ui.movie;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.example.filmspace_mobile.R;
import com.example.filmspace_mobile.data.model.movie.Movie;
import com.example.filmspace_mobile.data.repository.MovieRepository;
import com.example.filmspace_mobile.data.repository.RepositoryCallback;
import com.example.filmspace_mobile.ui.adapters.MovieDetailsPagerAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MovieDetailActivity extends AppCompatActivity {

    private ImageView moviePoster;
    private ImageButton btnBack;
    private TextView movieTitle;
    private MaterialButton btnPlay;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ProgressBar progressBar;

    private Movie movie;
    private MovieDetailsPagerAdapter pagerAdapter;
    
    @Inject
    MovieRepository movieRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        initViews();
        setupClickListeners();
        loadMovieData();
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
        int movieId = getIntent().getIntExtra("movieId", -1);
        
        if (movieId == -1) {
            Toast.makeText(this, "Invalid movie ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Show loading
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        viewPager.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);

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
                
                Toast.makeText(MovieDetailActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void displayMovieInfo() {
        movieTitle.setText(movie.getTitle());

        // TODO: Load poster image with Glide/Picasso
        // Glide.with(this)
        //     .load(movie.getBackdropUrl())
        //     .placeholder(R.drawable.ic_movie_placeholder)
        //     .into(moviePoster);
    }

    private void setupViewPager() {
        pagerAdapter = new MovieDetailsPagerAdapter(this, movie);
        viewPager.setAdapter(pagerAdapter);

        // Connect TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
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
        }).attach();

        // Set default tab to About (position 0)
        viewPager.setCurrentItem(0);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnPlay.setOnClickListener(v -> {
            // TODO: Navigate to video player activity
            Toast.makeText(this, "Opening video player...", Toast.LENGTH_SHORT).show();
            // Intent intent = new Intent(this, VideoPlayerActivity.class);
            // intent.putExtra("MOVIE_ID", movie.getId());
            // startActivity(intent);
        });
    }

    public Movie getMovie() {
        return movie;
    }
}