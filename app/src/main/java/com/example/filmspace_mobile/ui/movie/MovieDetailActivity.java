package com.example.filmspace_mobile.ui.movie;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.example.filmspace_mobile.R;
import com.example.filmspace_mobile.data.model.movie.Cast;
import com.example.filmspace_mobile.data.model.movie.Movie;
import com.example.filmspace_mobile.ui.adapters.MovieDetailsPagerAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import java.util.ArrayList;
import java.util.List;

public class MovieDetailActivity extends AppCompatActivity {

    private ImageView moviePoster;
    private ImageButton btnBack;
    private TextView movieTitle;
    private MaterialButton btnPlay;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    private Movie movie;
    private MovieDetailsPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        initViews();
        loadMovieData();
        setupViewPager();
        setupClickListeners();
    }

    private void initViews() {
        moviePoster = findViewById(R.id.moviePoster);
        btnBack = findViewById(R.id.btnBack);
        movieTitle = findViewById(R.id.movieTitle);
        btnPlay = findViewById(R.id.btnPlay);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
    }

    private void loadMovieData() {
        // TODO: Get movie ID from intent and load from API
        // int movieId = getIntent().getIntExtra("MOVIE_ID", -1);

        // For now, create dummy movie data
        movie = new Movie();
        movie.setId(1);
        movie.setTitle("Stranger Things");
        movie.setOverview("The film opens with Phineas Taylor \"P.T.\" Barnum (Hugh Jackman) joining his circus troupe in a song (\"The Greatest Show\"), playing to an enthusiastic crowd as he and his performers put on a dazzling show.");
        movie.setReleaseDate("2021");
        movie.setSeasonCount(4);
        movie.setEpisodeCount(34);
        movie.setGenre("Sci-Fi");
        movie.setRating(8.7);

        // Add dummy cast
        List<Cast> castList = new ArrayList<>();
        castList.add(new Cast(1, "Peter England", "Character 1", ""));
        castList.add(new Cast(2, "Rosey Day", "Character 2", ""));
        castList.add(new Cast(3, "John Doe", "Character 3", ""));
        movie.setCastList(castList);

        displayMovieInfo();
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
            // Navigate to video player activity
            Intent intent = new Intent(this, VideoPlayerActivity.class);
            intent.putExtra(VideoPlayerActivity.EXTRA_MOVIE_ID, movie.getId());
            intent.putExtra(VideoPlayerActivity.EXTRA_MOVIE_TITLE, movie.getTitle());
            startActivity(intent);
        });
    }
}