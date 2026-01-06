package com.example.filmspace_mobile.ui.movie;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import android.widget.Toast;

import androidx.annotation.OptIn;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.filmspace_mobile.R;
import com.example.filmspace_mobile.data.api.EpisodeApiService;
import com.example.filmspace_mobile.data.api.RetrofitClient;
import com.example.filmspace_mobile.data.local.UserSessionManager;
import com.example.filmspace_mobile.data.model.movie.Episode;
import com.example.filmspace_mobile.ui.adapters.VideoEpisodeAdapter;
import com.example.filmspace_mobile.ui.subscription.SubscriptionDescriptionActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@OptIn(markerClass = UnstableApi.class)
public class ExoVideoPlayerActivity extends AppCompatActivity {

    private static final String TAG = "ExoVideoPlayerActivity";
    
    public static final String EXTRA_MOVIE_ID = "movie_id";
    public static final String EXTRA_EPISODE_ID = "episode_id";
    public static final String EXTRA_MOVIE_TITLE = "movie_title";

    // UI Components
    private PlayerView playerView;
    private FrameLayout videoContainer;
    private LinearLayout episodesContainer;
    private ImageButton btnBack, btnRewind, btnForward, btnFullscreen;
    private RecyclerView rvEpisodes;
    private ProgressBar progressBar;

    // ExoPlayer
    private ExoPlayer exoPlayer;
    
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
    private boolean isFullscreen = false;
    private boolean controlsVisible = true;

    // Constants
    private static final int CONTROL_HIDE_DELAY = 3000;
    private static final long SEEK_TIME = 10_000; // 10 seconds

    // API Service
    private EpisodeApiService episodeApiService;
    
    // User Session Manager
    private UserSessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exo_video_player);

        // Keep screen on while playing
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Get intent data
        getIntentData();

        // Initialize API
        episodeApiService = RetrofitClient.getEpisodeApiService();
        
        // Initialize session manager
        sessionManager = new UserSessionManager(this);

        // Initialize views
        initViews();

        // Setup components
        setupRecyclerView();
        setupExoPlayer();
        setupControls();

        // Load episodes from API
        loadEpisodesFromApi();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        movieId = intent.getIntExtra(EXTRA_MOVIE_ID, -1);
        episodeId = intent.getIntExtra(EXTRA_EPISODE_ID, -1);
        movieTitle = intent.getStringExtra(EXTRA_MOVIE_TITLE);

        Log.d(TAG, "Intent data - MovieId: " + movieId + ", EpisodeId: " + episodeId + ", Title: " + movieTitle);
    }

    private void initViews() {
        playerView = findViewById(R.id.player_view);
        videoContainer = findViewById(R.id.video_container);
        episodesContainer = findViewById(R.id.episodes_container);
        btnBack = findViewById(R.id.btn_back);
        btnRewind = findViewById(R.id.btn_rewind);
        btnForward = findViewById(R.id.btn_forward);
        btnFullscreen = findViewById(R.id.btn_fullscreen);
        rvEpisodes = findViewById(R.id.rv_episodes);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void setupExoPlayer() {
        exoPlayer = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(exoPlayer);
        
        // Hide default controls, we'll use custom ones
        playerView.setUseController(false);
        
        // Set background color to prevent black bars
        playerView.setBackgroundColor(getResources().getColor(android.R.color.black));
        
        // Player event listener
        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == Player.STATE_BUFFERING) {
                    progressBar.setVisibility(View.VISIBLE);
                } else {
                    progressBar.setVisibility(View.GONE);
                }
                
                if (playbackState == Player.STATE_ENDED) {
                    playNextEpisode();
                }
                
                // Show controls when video is ready
                if (playbackState == Player.STATE_READY) {
                    showControls();
                }
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                Log.e(TAG, "ExoPlayer error: " + error.getMessage());
                Toast.makeText(ExoVideoPlayerActivity.this, "Error playing video", Toast.LENGTH_SHORT).show();
            }
        });

        // Click listener to toggle controls
        playerView.setOnClickListener(v -> toggleControls());
    }

    private void setupControls() {
        btnBack.setOnClickListener(v -> finish());
        
        btnRewind.setOnClickListener(v -> {
            long currentPosition = exoPlayer.getCurrentPosition();
            long newPosition = Math.max(0, currentPosition - SEEK_TIME);
            exoPlayer.seekTo(newPosition);
            showControls();
        });
        
        btnForward.setOnClickListener(v -> {
            long currentPosition = exoPlayer.getCurrentPosition();
            long duration = exoPlayer.getDuration();
            long newPosition = Math.min(duration, currentPosition + SEEK_TIME);
            exoPlayer.seekTo(newPosition);
            showControls();
        });
        
        btnFullscreen.setOnClickListener(v -> toggleFullscreen());
    }

    private void setupRecyclerView() {
        episodeAdapter = new VideoEpisodeAdapter(episodeList, this::onEpisodeSelected);
        rvEpisodes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvEpisodes.setAdapter(episodeAdapter);
    }

    private void loadEpisodesFromApi() {
        if (movieId == -1) {
            Toast.makeText(this, "Invalid movie ID", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        Call<List<Episode>> call = episodeApiService.getMovieEpisodes(movieId);
        call.enqueue(new Callback<List<Episode>>() {
            @Override
            public void onResponse(Call<List<Episode>> call, Response<List<Episode>> response) {
                progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null) {
                    episodeList.clear();
                    episodeList.addAll(response.body());
                    episodeAdapter.notifyDataSetChanged();

                    // Find and play the requested episode
                    findAndPlayEpisode();
                } else {
                    Toast.makeText(ExoVideoPlayerActivity.this, "Failed to load episodes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Episode>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Failed to load episodes", t);
                Toast.makeText(ExoVideoPlayerActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void findAndPlayEpisode() {
        if (episodeList.isEmpty()) {
            Toast.makeText(this, "No episodes available", Toast.LENGTH_SHORT).show();
            return;
        }

        // Find episode by ID or play first episode
        Episode episodeToPlay = null;
        for (int i = 0; i < episodeList.size(); i++) {
            Episode episode = episodeList.get(i);
            if (episode.getId() == episodeId) {
                episodeToPlay = episode;
                currentEpisodePosition = i;
                break;
            }
        }

        if (episodeToPlay == null) {
            episodeToPlay = episodeList.get(0);
            currentEpisodePosition = 0;
        }

        playEpisode(episodeToPlay);
    }

    private void playEpisode(Episode episode) {
        if (episode == null) {
            Toast.makeText(this, "Episode data is null", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if episode is premium and user has access
        if (episode.isPremium() && !hasUserPremiumAccess()) {
            showPremiumDialog(episode);
            return;
        }

        String videoUrl = episode.getVideoUrl();
        if (videoUrl == null || videoUrl.isEmpty()) {
            Toast.makeText(this, "Video URL not available", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Playing video: " + videoUrl);

        // Create media item and play
        MediaItem mediaItem = MediaItem.fromUri(videoUrl);
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare();
        exoPlayer.play();

        // Update episode adapter selection
        episodeAdapter.setCurrentPlayingPosition(currentEpisodePosition);
    }

    private void onEpisodeSelected(Episode episode, int position) {
        currentEpisodePosition = position;
        playEpisode(episode);
    }

    private void playNextEpisode() {
        if (currentEpisodePosition < episodeList.size() - 1) {
            currentEpisodePosition++;
            Episode nextEpisode = episodeList.get(currentEpisodePosition);
            playEpisode(nextEpisode);
        } else {
            Toast.makeText(this, "No more episodes", Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleControls() {
        if (controlsVisible) {
            hideControls();
        } else {
            showControls();
        }
    }

    private void showControls() {
        btnBack.setVisibility(View.VISIBLE);
        btnRewind.setVisibility(View.VISIBLE);
        btnForward.setVisibility(View.VISIBLE);
        btnFullscreen.setVisibility(View.VISIBLE);
        episodesContainer.setVisibility(View.VISIBLE);
        controlsVisible = true;

        // Auto-hide controls after delay
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(this::hideControls, CONTROL_HIDE_DELAY);
    }

    private void hideControls() {
        btnBack.setVisibility(View.GONE);
        btnRewind.setVisibility(View.GONE);
        btnForward.setVisibility(View.GONE);
        btnFullscreen.setVisibility(View.GONE);
        episodesContainer.setVisibility(View.GONE);
        controlsVisible = false;
    }

    private void toggleFullscreen() {
        if (isFullscreen) {
            exitFullscreen();
        } else {
            enterFullscreen();
        }
    }

    private void enterFullscreen() {
        isFullscreen = true;
        
        // Hide system UI
        WindowInsetsControllerCompat windowInsetsController = 
            WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsController.setSystemBarsBehavior(
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
        
        // Set landscape orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        
        // Update fullscreen button icon
        btnFullscreen.setImageResource(R.drawable.ic_fullscreen_exit);
        
        // Hide episodes container in fullscreen
        episodesContainer.setVisibility(View.GONE);
    }

    private void exitFullscreen() {
        isFullscreen = false;
        
        // Show system UI
        WindowInsetsControllerCompat windowInsetsController = 
            WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsController.show(WindowInsetsCompat.Type.systemBars());
        
        // Set portrait orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        // Update fullscreen button icon
        btnFullscreen.setImageResource(R.drawable.ic_fullscreen);
        
        // Show episodes container
        if (controlsVisible) {
            episodesContainer.setVisibility(View.VISIBLE);
        }
    }

    private boolean hasUserPremiumAccess() {
        // Check user premium status from session manager
        return sessionManager.isPremium();
    }

    private void showPremiumDialog(Episode episode) {
        new AlertDialog.Builder(this)
                .setTitle("Premium Episode")
                .setMessage("This episode requires a Premium subscription. Would you like to upgrade to Premium to watch this content?")
                .setIcon(R.drawable.ic_premium) // You may need to add this icon
                .setPositiveButton("Upgrade to Premium", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Navigate to payment/subscription screen
                        navigateToPayment();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(true)
                .show();
    }

    private void navigateToPayment() {
        try {
            // Try to navigate to SubscriptionDescriptionActivity
            Intent intent = new Intent(this, SubscriptionDescriptionActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to payment screen", e);
            Toast.makeText(this, "Unable to open payment screen", Toast.LENGTH_SHORT).show();
        }
    }

    @Deprecated
    private void showPremiumDialog() {
        // Deprecated method - use showPremiumDialog(Episode episode) instead
        Intent intent = new Intent(this, SubscriptionDescriptionActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (exoPlayer != null) {
            exoPlayer.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        if (exoPlayer != null) {
            exoPlayer.release();
        }
    }

    @Override
    public void onBackPressed() {
        if (isFullscreen) {
            exitFullscreen();
        } else {
            super.onBackPressed();
        }
    }
}