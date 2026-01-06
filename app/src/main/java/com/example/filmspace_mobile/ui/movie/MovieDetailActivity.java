package com.example.filmspace_mobile.ui.movie;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
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

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MovieDetailActivity extends AppCompatActivity {

    private static final String KEY_MOVIE_ID = "movieId";
    private static final String TAG = "MovieDetailActivity";

    private ImageView moviePoster;
    private ImageButton btnBack;
    private TextView movieTitle;
    private TextView movieReleaseYear;
    private MaterialButton btnPlay;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ProgressBar progressBar;

    private Movie movie;
    private int currentMovieId = -1;
    private MovieDetailsPagerAdapter pagerAdapter;
    private TabLayoutMediator tabLayoutMediator;

    // [MỚI] Biến cờ để tránh load 2 lần khi mới mở màn hình
    private boolean isFirstLoad = true;

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

        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_MOVIE_ID)) {
            currentMovieId = savedInstanceState.getInt(KEY_MOVIE_ID, -1);
        }

        loadMovieData();
    }

    // [MỚI - QUAN TRỌNG]
    // Hàm này sẽ chạy mỗi khi màn hình hiện lên (bao gồm cả khi quay lại từ Payment)
    @Override
    protected void onResume() {
        super.onResume();

        // Nếu là lần đầu tiên mở màn hình (vừa chạy onCreate xong) thì bỏ qua
        // để tránh việc load API 2 lần liên tiếp gây chậm app
        if (isFirstLoad) {
            isFirstLoad = false;
            return;
        }

        // Nếu quay lại từ màn hình khác (VD: PaymentActivity) và đã có ID phim
        // Thì gọi API update lại dữ liệu (để check xem đã lên VIP chưa)
        if (currentMovieId != -1) {
            Log.d(TAG, "onResume: Quay lại màn hình -> Tự động refresh data");
            refreshMovieData();
        }
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
        movieReleaseYear = findViewById(R.id.movieReleaseYear);
        btnPlay = findViewById(R.id.btnPlay);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        progressBar = findViewById(R.id.progressBar);
    }

    private void loadMovieData() {
        int movieId = currentMovieId;

        if (movieId == -1) {
            movieId = getIntent().getIntExtra(KEY_MOVIE_ID, -1);
        }

        if (movieId == -1) {
            View rootView = findViewById(android.R.id.content);
            if (rootView != null) {
                Snackbar.make(rootView, R.string.invalid_movie_id, Snackbar.LENGTH_SHORT).show();
            }
            finish();
            return;
        }

        currentMovieId = movieId;

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

    /**
     * Hàm fetch lại data "ngầm" (không hiện loading che màn hình)
     * Được gọi tự động trong onResume
     */
    public void refreshMovieData() {
        if (currentMovieId == -1) return;

        Log.d(TAG, "refreshMovieData: Fetching updated movie data...");

        movieRepository.getMovieById(currentMovieId, new RepositoryCallback<Movie>() {
            @Override
            public void onSuccess(Movie data) {
                // Kiểm tra nếu Activity đã bị hủy thì thôi không update UI
                if (isFinishing() || isDestroyed()) return;

                movie = data; // Cập nhật biến global
                Log.d(TAG, "refreshMovieData: Success. Updating Fragments.");

                // 1. Cập nhật thông tin cơ bản
                displayMovieInfo();

                // 2. Cập nhật dữ liệu cho các Fragment con (quan trọng nhất là danh sách tập phim)
                for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                    if (fragment instanceof MovieEpisodeFragment) {
                        // Gọi hàm updateEpisodes bên Fragment để list phim tự nhận link mới
                        ((MovieEpisodeFragment) fragment).updateEpisodes(movie.getEpisodes());
                    }
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Failed to refresh movie data: " + errorMessage);
            }
        });
    }

    private void displayMovieInfo() {
        if (movie == null) return;

        movieTitle.setText(movie.getTitle());
        movieReleaseYear.setText(String.valueOf(movie.getReleaseDate()));

        String posterUrl = movie.getPosterUrl();
        if (posterUrl != null && !posterUrl.trim().isEmpty()) {
            Glide.with(this)
                    .load(posterUrl)
                    .placeholder(R.drawable.movie_poster_placeholder)
                    .error(R.drawable.movie_poster_placeholder)
                    .into(moviePoster);
        } else {
            moviePoster.setImageResource(R.drawable.movie_poster_placeholder);
        }
    }

    private void setupViewPager() {
        if (movie == null) return;

        if (tabLayoutMediator != null) {
            tabLayoutMediator.detach();
        }

        pagerAdapter = new MovieDetailsPagerAdapter(this, movie);
        viewPager.setAdapter(pagerAdapter);

        viewPager.setOffscreenPageLimit(2);
        viewPager.setUserInputEnabled(true);

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

        // Mặc định về tab About
        viewPager.setCurrentItem(0, false);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnPlay.setOnClickListener(v -> {
            if (movie == null) return;
            Intent intent = new Intent(this, VideoPlayerActivity.class);
            intent.putExtra(VideoPlayerActivity.EXTRA_MOVIE_ID, movie.getId());
            intent.putExtra(VideoPlayerActivity.EXTRA_MOVIE_TITLE, movie.getTitle());
            startActivity(intent);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tabLayoutMediator != null) {
            tabLayoutMediator.detach();
            tabLayoutMediator = null;
        }
    }

    public Movie getMovie() {
        return movie;
    }
}