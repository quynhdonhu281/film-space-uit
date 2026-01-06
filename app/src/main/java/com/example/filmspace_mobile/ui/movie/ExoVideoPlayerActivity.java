package com.example.filmspace_mobile.ui.movie;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.OptIn;
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

import com.example.filmspace_mobile.R;
import com.example.filmspace_mobile.data.api.EpisodeApiService;
import com.example.filmspace_mobile.data.api.RetrofitClient;
import com.example.filmspace_mobile.data.local.UserSessionManager;
import com.example.filmspace_mobile.data.model.movie.Episode;
import com.example.filmspace_mobile.utils.PremiumUtils;

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
    private ProgressBar loadingProgressBar;
    private ImageButton btnBack; // Nút Back mới thêm

    // ExoPlayer
    private ExoPlayer exoPlayer;

    // Data
    // Vẫn giữ list để hỗ trợ tính năng "Tự động chuyển tập tiếp theo" (Auto-play next)
    private List<Episode> episodeList = new ArrayList<>();
    private int currentEpisodePosition = 0;

    // Intent data
    private int movieId;
    private int episodeId;

    // State variables
    private boolean isFullscreen = false;

    // API & Session
    private EpisodeApiService episodeApiService;
    private UserSessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exo_video_player);

        // Giữ màn hình sáng
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        getIntentData();

        episodeApiService = RetrofitClient.getEpisodeApiService();
        sessionManager = new UserSessionManager(this);

        initViews();
        setupExoPlayer();

        // Load danh sách để lấy URL và hỗ trợ next bài
        loadEpisodesFromApi();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        movieId = intent.getIntExtra(EXTRA_MOVIE_ID, -1);
        episodeId = intent.getIntExtra(EXTRA_EPISODE_ID, -1);
    }

    private void initViews() {
        playerView = findViewById(R.id.player_view);
        loadingProgressBar = findViewById(R.id.progress_bar);
        btnBack = findViewById(R.id.btn_back);

        // XỬ LÝ NÚT BACK: Đóng Activity để quay về Fragment cũ
        btnBack.setOnClickListener(v -> finish());
    }

    private void setupExoPlayer() {
        // 1. Cấu hình Player
        exoPlayer = new ExoPlayer.Builder(this)
                .setSeekBackIncrementMs(10000)
                .setSeekForwardIncrementMs(10000)
                .build();

        playerView.setPlayer(exoPlayer);

        // 2. Cấu hình Controller
        playerView.setUseController(true);
        playerView.setShowNextButton(true); // Giữ nút Next để user bấm qua bài
        playerView.setShowPreviousButton(true);
        playerView.setControllerAutoShow(true);

        // 3. Xử lý Fullscreen
        playerView.setFullscreenButtonClickListener(isFullScreen -> {
            if (isFullScreen) {
                enterFullscreen();
            } else {
                exitFullscreen();
            }
        });

        // 4. Lắng nghe sự kiện
        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                // Tự động chuyển tập khi hết phim
                if (playbackState == Player.STATE_ENDED) {
                    playNextEpisode();
                }
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                Log.e(TAG, "ExoPlayer error: " + error.getMessage());
                Toast.makeText(ExoVideoPlayerActivity.this, "Error playing video", Toast.LENGTH_SHORT).show();
            }

            // Ẩn hiện nút Back cùng với Controller cho đẹp (Optional)
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                // Bạn có thể custom logic ẩn hiện nút back ở đây nếu muốn
            }
        });
    }

    private void loadEpisodesFromApi() {
        if (movieId == -1) return;

        loadingProgressBar.setVisibility(View.VISIBLE);

        Call<List<Episode>> call = episodeApiService.getMovieEpisodes(movieId);
        call.enqueue(new Callback<List<Episode>>() {
            @Override
            public void onResponse(Call<List<Episode>> call, Response<List<Episode>> response) {
                loadingProgressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    episodeList.clear();
                    episodeList.addAll(response.body());
                    // Không cần notify Adapter nữa vì đã xóa RecyclerView

                    findAndPlayEpisode();
                }
            }

            @Override
            public void onFailure(Call<List<Episode>> call, Throwable t) {
                loadingProgressBar.setVisibility(View.GONE);
                Toast.makeText(ExoVideoPlayerActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void findAndPlayEpisode() {
        if (episodeList.isEmpty()) return;

        Episode episodeToPlay = null;
        for (int i = 0; i < episodeList.size(); i++) {
            if (episodeList.get(i).getId() == episodeId) {
                episodeToPlay = episodeList.get(i);
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
        if (episode == null) return;

        // Check Premium
        if (!PremiumUtils.canUserAccessEpisode(episode, sessionManager)) {
            PremiumUtils.showPremiumDialog(this, episode);
            PremiumUtils.logPremiumAccess(episode, sessionManager.isPremium(), false);
            return;
        }
        PremiumUtils.logPremiumAccess(episode, sessionManager.isPremium(), true);

        // Play Video
        String videoUrl = episode.getVideoUrl();
        if (videoUrl != null && !videoUrl.isEmpty()) {
            MediaItem mediaItem = MediaItem.fromUri(videoUrl);
            exoPlayer.setMediaItem(mediaItem);
            exoPlayer.prepare();
            exoPlayer.play();
        }
    }

    private void playNextEpisode() {
        if (currentEpisodePosition < episodeList.size() - 1) {
            currentEpisodePosition++;
            Toast.makeText(this, "Playing next episode...", Toast.LENGTH_SHORT).show();
            playEpisode(episodeList.get(currentEpisodePosition));
        } else {
            Toast.makeText(this, "End of series", Toast.LENGTH_SHORT).show();
        }
    }

    // --- Logic Fullscreen (Đơn giản hơn vì không còn List Episodes) ---

    private void enterFullscreen() {
        isFullscreen = true;

        // 1. Ẩn UI hệ thống
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsController.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());

        // 2. Xoay ngang
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // 3. Tràn tai thỏ
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(params);
        }
    }

    private void exitFullscreen() {
        isFullscreen = false;

        // 1. Hiện UI hệ thống
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsController.show(WindowInsetsCompat.Type.systemBars());

        // 2. Xoay dọc
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // 3. Reset tai thỏ
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT;
            getWindow().setAttributes(params);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (exoPlayer != null) exoPlayer.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (exoPlayer != null) exoPlayer.release();
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