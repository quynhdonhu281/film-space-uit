package com.example.filmspace_mobile.ui.movie;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.filmspace_mobile.R;
import com.example.filmspace_mobile.data.api.EpisodeApiService;
import com.example.filmspace_mobile.data.api.RetrofitClient;
import com.example.filmspace_mobile.data.model.movie.Episode;
import com.example.filmspace_mobile.ui.adapters.VideoEpisodeAdapter;
import com.example.filmspace_mobile.ui.subscription.SubscriptionDescriptionActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoPlayerActivity extends AppCompatActivity {

    private static final String TAG = "VideoPlayerActivity";


    public static final String EXTRA_MOVIE_ID = "movie_id";
    public static final String EXTRA_EPISODE_ID = "episode_id";
    public static final String EXTRA_MOVIE_TITLE = "movie_title";

    // UI Components
    private VideoView videoView;
    private FrameLayout videoContainer;
    private RelativeLayout videoControlsOverlay;
    private LinearLayout episodesContainer;

    private ImageButton btnBack, btnPlayPause, btnRewind, btnForward, btnFullscreen;
    private SeekBar seekBar;
    private TextView tvCurrentTime, tvTotalTime, tvVideoTitle;
    private RecyclerView rvEpisodes;
    private ProgressBar progressBar;

    // Adapter and data
    private VideoEpisodeAdapter episodeAdapter;
    private List<Episode> episodeList = new ArrayList<>();
    private int currentEpisodePosition = 0;

    // Intent data
    private int movieId;
    private int episodeId;
    private String movieTitle;

    // Handler for UI updates
    private final Handler handler = new Handler(Looper.getMainLooper());

    // State variables
    private boolean isPlaying = false;
    private boolean isFullscreen = false;
    private boolean controlsVisible = true;

    // Constants
    private static final int CONTROL_HIDE_DELAY = 3000;
    private static final int SEEK_TIME = 10_000;

    // API Service
    private EpisodeApiService episodeApiService;

    // ==============================
    // Activity Lifecycle
    // ==============================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        // Keep screen on while playing
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Get intent data
        getIntentData();

        // Initialize API
        episodeApiService = RetrofitClient.getEpisodeApiService();

        // Initialize views
        initViews();

        // Setup components
        setupRecyclerView();
        setupVideoPlayer();
        setupControls();

        // Load episodes from API
        loadEpisodesFromApi();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoView != null && videoView.isPlaying()) {
            videoView.pause();
            isPlaying = false;
            if (btnPlayPause != null) {
                btnPlayPause.setImageResource(R.drawable.ic_play);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        if (videoView != null) {
            videoView.stopPlayback();
        }
    }

    @Override
    public void onBackPressed() {
        if (isFullscreen) {
            // Exit fullscreen first
            exitFullscreen();
        } else {
            // Exit activity
            super.onBackPressed();
        }
    }

    // ==============================
    // Get Intent Data
    // ==============================

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent == null) {
            Log.e(TAG, "ERROR: Intent is null!");
            Toast.makeText(this, "Error: Invalid intent", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        movieId = intent.getIntExtra(EXTRA_MOVIE_ID, 0);
        episodeId = intent.getIntExtra(EXTRA_EPISODE_ID, 0);
        movieTitle = intent.getStringExtra(EXTRA_MOVIE_TITLE);

        Log.d(TAG, "=== INTENT DATA DEBUG ===");
        Log.d(TAG, "Movie ID: " + movieId);
        Log.d(TAG, "Episode ID: " + episodeId);
        Log.d(TAG, "Movie Title: " + movieTitle);
        Log.d(TAG, "========================");

        if (movieId == 0) {
            Log.e(TAG, "ERROR: Movie ID is 0! VideoPlayerActivity was started without proper movie ID");
            Toast.makeText(this, "Error: Invalid movie ID", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    }

    // ==============================
    // Initialize Views
    // ==============================

    private void initViews() {
        // Video components
        videoView = findViewById(R.id.videoView);
        videoContainer = findViewById(R.id.videoContainer);
        videoControlsOverlay = findViewById(R.id.videoControlsOverlay);
        episodesContainer = findViewById(R.id.episodesContainer);

        // Control buttons
        btnBack = findViewById(R.id.btnBack);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnRewind = findViewById(R.id.btnRewind);
        btnForward = findViewById(R.id.btnForward);
        btnFullscreen = findViewById(R.id.btnFullscreen);

        // SeekBar and time
        seekBar = findViewById(R.id.seekBar);
        tvCurrentTime = findViewById(R.id.tvCurrentTime);
        tvTotalTime = findViewById(R.id.tvTotalTime);
        tvVideoTitle = findViewById(R.id.tvVideoTitle);

        // Episodes list
        rvEpisodes = findViewById(R.id.rvEpisodes);
        progressBar = findViewById(R.id.progressBar);

        // Set movie title
        if (movieTitle != null && !movieTitle.isEmpty()) {
            tvVideoTitle.setText(movieTitle);
        }
    }

    // ==============================
    // Load Episodes from API
    // ==============================

    private void loadEpisodesFromApi() {
        showLoading(true);

        Log.d(TAG, "Loading episodes for movie ID: " + movieId);

        Call<List<Episode>> call = episodeApiService.getMovieEpisodes(movieId);

        call.enqueue(new Callback<List<Episode>>() {
            @Override
            public void onResponse(Call<List<Episode>> call, Response<List<Episode>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    episodeList = response.body();

                    Log.d(TAG, "Loaded " + episodeList.size() + " episodes");

                    if (episodeList.isEmpty()) {
                        Log.w(TAG, "No episodes found for movie ID: " + movieId);
                        Toast.makeText(VideoPlayerActivity.this,
                                "No episodes available",
                                Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }

                    // Update adapter
                    episodeAdapter.updateEpisodes(episodeList);

                    // Find episode to play
                    if (episodeId != 0) {
                        // Play specific episode
                        loadSpecificEpisode(episodeId);
                    } else {
                        // Play first episode
                        currentEpisodePosition = 0;
                        playEpisode(episodeList.get(0));
                        episodeAdapter.setCurrentPlayingPosition(0);
                    }
                } else {
                    Log.e(TAG, "Failed to load episodes: " + response.code());
                    Toast.makeText(VideoPlayerActivity.this,
                            "Failed to load episodes",
                            Toast.LENGTH_SHORT).show();

                    // Load fallback data for testing
                    loadFallbackEpisodes();
                }
            }

            @Override
            public void onFailure(Call<List<Episode>> call, Throwable t) {
                showLoading(false);
                Log.e(TAG, "API error", t);
                Toast.makeText(VideoPlayerActivity.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();

                // Load fallback episodes for testing
                loadFallbackEpisodes();
            }
        });
    }

    private void loadSpecificEpisode(int episodeId) {
        if (episodeList == null || episodeList.isEmpty()) {
            Toast.makeText(this, "No episodes available", Toast.LENGTH_SHORT).show();
            return;
        }

        for (int i = 0; i < episodeList.size(); i++) {
            if (episodeList.get(i).getId() == episodeId) {
                currentEpisodePosition = i;
                playEpisode(episodeList.get(i));
                if (episodeAdapter != null) {
                    episodeAdapter.setCurrentPlayingPosition(i);
                }
                return;
            }
        }

        // If episode not found, play first one
        Log.w(TAG, "Episode " + episodeId + " not found, playing first episode");
        currentEpisodePosition = 0;
        playEpisode(episodeList.get(0));
        if (episodeAdapter != null) {
            episodeAdapter.setCurrentPlayingPosition(0);
        }
    }

    private void loadFallbackEpisodes() {
        // Fallback data for testing when API is not available
        episodeList.clear();

        episodeList.add(new Episode(1, 1,
                "Episode 1", "", "", 49, "2016-07-15"));

        episodeList.add(new Episode(2, 2,
                "Episode 2", "", "", 56, "2016-07-15"));

        episodeList.add(new Episode(3, 3,
                "Episode 3", "", "", 52, "2016-07-15"));

        if (episodeAdapter != null) {
            episodeAdapter.updateEpisodes(episodeList);
        }

        if (!episodeList.isEmpty()) {
            currentEpisodePosition = 0;
            playEpisode(episodeList.get(0));
            if (episodeAdapter != null) {
                episodeAdapter.setCurrentPlayingPosition(0);
            }
        }
    }

    // ==============================
    // Setup RecyclerView
    // ==============================

    private void setupRecyclerView() {
        rvEpisodes.setLayoutManager(new LinearLayoutManager(this));

        episodeAdapter = new VideoEpisodeAdapter(episodeList, (episode, position) -> {
            // Check if episode is premium and user is not premium
            if (episode.isPremium() && !isPremiumUser()) {
                // Navigate to subscription screen
                Intent intent = new Intent(VideoPlayerActivity.this,
                        SubscriptionDescriptionActivity.class);
                startActivity(intent);
                return;
            }

            currentEpisodePosition = position;
            playEpisode(episode);
            episodeAdapter.setCurrentPlayingPosition(position);
        });

        rvEpisodes.setAdapter(episodeAdapter);
    }

    private boolean isPremiumUser() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        return prefs.getBoolean("is_premium", false);
    }

    // ==============================
    // Video Player Setup
    // ==============================

    private void setupVideoPlayer() {
        if (videoView == null) {
            Log.e(TAG, "VideoView is null in setupVideoPlayer()");
            return;
        }

        videoView.setOnPreparedListener(mp -> {
            if (seekBar != null && tvTotalTime != null) {
                seekBar.setMax(videoView.getDuration());
                tvTotalTime.setText(formatTime(videoView.getDuration()));
                updateVideoProgress();
            }
            Log.d(TAG, "Video prepared, duration: " + videoView.getDuration());
        });

        videoView.setOnCompletionListener(mp -> {
            Log.d(TAG, "Video completed");
            playNextEpisode();
        });

        videoView.setOnClickListener(v -> toggleControls());

        videoView.setOnErrorListener((mp, what, extra) -> {
            Log.e(TAG, "Video error: what=" + what + ", extra=" + extra);
            Toast.makeText(this, "Error playing video", Toast.LENGTH_SHORT).show();
            return true;
        });
    }

    private void playEpisode(Episode episode) {
        if (episode == null) {
            Toast.makeText(this, "Episode data is null", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update title
        String title = movieTitle != null ? movieTitle : "Episode";
        if (tvVideoTitle != null) {
            tvVideoTitle.setText(title + " - Episode " + episode.getEpisodeNumber());
        }

        // Get video URL from API response
        String videoUrl = episode.getVideoUrl();

        if (videoUrl == null || videoUrl.isEmpty()) {
            Toast.makeText(this, "Video URL not available", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Video URL is null or empty for episode: " + episode.getId());
            return;
        }

        Log.d(TAG, "Playing video: " + videoUrl);

        // Play video
        try {
            videoView.setVideoURI(Uri.parse(videoUrl));
            videoView.start();

            isPlaying = true;
            if (btnPlayPause != null) {
                btnPlayPause.setImageResource(R.drawable.ic_pause);
            }
            hideControlsWithDelay();
        } catch (Exception e) {
            Log.e(TAG, "Error playing video", e);
            Toast.makeText(this, "Error playing video: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // ==============================
    // Setup Controls
    // ==============================

    private void setupControls() {
        // Back button
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (isFullscreen) {
                    exitFullscreen();
                } else {
                    finish();
                }
            });
        }

        // Play/Pause button
        if (btnPlayPause != null) {
            btnPlayPause.setOnClickListener(v -> togglePlayPause());
        }

        // Rewind button
        if (btnRewind != null) {
            btnRewind.setOnClickListener(v -> {
                int newPosition = videoView.getCurrentPosition() - SEEK_TIME;
                videoView.seekTo(Math.max(0, newPosition));
                showControls();
                hideControlsWithDelay();
            });
        }

        // Forward button
        if (btnForward != null) {
            btnForward.setOnClickListener(v -> {
                int newPosition = videoView.getCurrentPosition() + SEEK_TIME;
                videoView.seekTo(Math.min(videoView.getDuration(), newPosition));
                showControls();
                hideControlsWithDelay();
            });
        }

        // Fullscreen button
        if (btnFullscreen != null) {
            btnFullscreen.setOnClickListener(v -> toggleFullscreen());
        }

        // SeekBar
        if (seekBar != null) {
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser && videoView != null) {
                        videoView.seekTo(progress);
                        if (tvCurrentTime != null) {
                            tvCurrentTime.setText(formatTime(progress));
                        }
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    handler.removeCallbacks(hideControlsRunnable);
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    hideControlsWithDelay();
                }
            });
        }
    }

    private void togglePlayPause() {
        if (videoView == null) return;

        if (isPlaying) {
            videoView.pause();
            btnPlayPause.setImageResource(R.drawable.ic_play);
            isPlaying = false;
            handler.removeCallbacks(hideControlsRunnable);
        } else {
            videoView.start();
            btnPlayPause.setImageResource(R.drawable.ic_pause);
            isPlaying = true;
            hideControlsWithDelay();
        }
    }

    // ==============================
    // Fullscreen Management
    // ==============================

    private void toggleFullscreen() {
        if (isFullscreen) {
            exitFullscreen();
        } else {
            enterFullscreen();
        }
    }

    private void enterFullscreen() {
        Log.d(TAG, "Entering fullscreen mode");

        // 1. Change to landscape orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // 2. Hide system UI (status bar, navigation bar)
        hideSystemUI();

        // 3. Change video container size to MATCH_PARENT
        if (videoContainer != null) {
            RelativeLayout.LayoutParams params =
                    (RelativeLayout.LayoutParams) videoContainer.getLayoutParams();
            params.height = RelativeLayout.LayoutParams.MATCH_PARENT;
            videoContainer.setLayoutParams(params);
        }

        // 4. Hide episodes container completely
        if (episodesContainer != null) {
            episodesContainer.setVisibility(View.GONE);
        }

        // 5. Change fullscreen icon
        if (btnFullscreen != null) {
            btnFullscreen.setImageResource(R.drawable.ic_fullscreen_exit);
        }

        isFullscreen = true;

        Log.d(TAG, "Fullscreen mode activated");
    }

    private void exitFullscreen() {
        Log.d(TAG, "Exiting fullscreen mode");

        // 1. Return to portrait orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // 2. Show system UI
        showSystemUI();

        // 3. Reset video container size to 250dp
        if (videoContainer != null) {
            RelativeLayout.LayoutParams params =
                    (RelativeLayout.LayoutParams) videoContainer.getLayoutParams();
            params.height = (int) (250 * getResources().getDisplayMetrics().density);
            videoContainer.setLayoutParams(params);
        }

        // 4. Show episodes container
        if (episodesContainer != null) {
            episodesContainer.setVisibility(View.VISIBLE);
        }

        // 5. Change fullscreen icon
        if (btnFullscreen != null) {
            btnFullscreen.setImageResource(R.drawable.ic_fullscreen);
        }

        isFullscreen = false;

        Log.d(TAG, "Fullscreen mode deactivated");
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
        );

        // Hide status bar and navigation bar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

        // Show status bar and navigation bar
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    // ==============================
    // Controls Visibility
    // ==============================

    private void toggleControls() {
        if (controlsVisible) {
            hideControls();
        } else {
            showControls();
            if (isPlaying) {
                hideControlsWithDelay();
            }
        }
    }

    private void showControls() {
        if (videoControlsOverlay != null) {
            videoControlsOverlay.setVisibility(View.VISIBLE);
            controlsVisible = true;
        }
    }

    private void hideControls() {
        if (videoControlsOverlay != null) {
            videoControlsOverlay.setVisibility(View.GONE);
            controlsVisible = false;
        }
    }

    private void hideControlsWithDelay() {
        handler.removeCallbacks(hideControlsRunnable);
        handler.postDelayed(hideControlsRunnable, CONTROL_HIDE_DELAY);
    }

    private final Runnable hideControlsRunnable = () -> {
        if (isPlaying) {
            hideControls();
        }
    };

    // ==============================
    // Video Progress Update
    // ==============================

    private void updateVideoProgress() {
        if (videoView != null && videoView.isPlaying()) {
            int position = videoView.getCurrentPosition();
            if (seekBar != null) {
                seekBar.setProgress(position);
            }
            if (tvCurrentTime != null) {
                tvCurrentTime.setText(formatTime(position));
            }
        }

        // Only continue if activity is not destroyed
        if (!isFinishing() && !isDestroyed()) {
            handler.postDelayed(this::updateVideoProgress, 1000);
        }
    }

    private String formatTime(int ms) {
        int seconds = (ms / 1000) % 60;
        int minutes = (ms / (1000 * 60)) % 60;
        int hours = ms / (1000 * 60 * 60);

        return hours > 0
                ? String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds)
                : String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    // ==============================
    // Episode Navigation
    // ==============================

    private void playNextEpisode() {
        if (episodeList == null || episodeList.isEmpty()) {
            Toast.makeText(this, "No episodes available", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentEpisodePosition < episodeList.size() - 1) {
            Episode nextEpisode = episodeList.get(currentEpisodePosition + 1);

            // Check if next episode is premium
            if (nextEpisode.isPremium() && !isPremiumUser()) {
                Toast.makeText(this, "This episode requires premium subscription",
                        Toast.LENGTH_LONG).show();

                // Show subscription dialog
                Intent intent = new Intent(this, SubscriptionDescriptionActivity.class);
                startActivity(intent);
                return;
            }

            currentEpisodePosition++;
            playEpisode(nextEpisode);
            if (episodeAdapter != null) {
                episodeAdapter.setCurrentPlayingPosition(currentEpisodePosition);
            }
        } else {
            Toast.makeText(this, "No more episodes", Toast.LENGTH_SHORT).show();
        }
    }

    // ==============================
    // Loading Indicator
    // ==============================

    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
}