package com.example.filmspace_mobile.ui.movie;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.filmspace_mobile.R;
import com.example.filmspace_mobile.data.local.UserSessionManager;
import com.example.filmspace_mobile.data.model.movie.Episode;
import com.example.filmspace_mobile.data.model.movie.Movie;
// 1. Import Repository và Callback
import com.example.filmspace_mobile.data.repository.HistoryRepository;
import com.example.filmspace_mobile.data.repository.RepositoryCallback;
import com.example.filmspace_mobile.ui.adapters.EpisodeAdapter;
import com.example.filmspace_mobile.utils.PremiumUtils;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject; // Import Inject
import dagger.hilt.android.AndroidEntryPoint; // Import Hilt

@AndroidEntryPoint // [BẮT BUỘC] Để Inject được Repository
public class MovieEpisodeFragment extends Fragment {

    private static final String TAG = "MovieEpisodeFragment";
    private RecyclerView rvEpisodes;
    private TextView tvEmptyState;
    private EpisodeAdapter episodeAdapter;
    private Movie movie;
    private UserSessionManager sessionManager;

    // [BƯỚC 1] Inject HistoryRepository
    @Inject
    HistoryRepository historyRepository;

    public static MovieEpisodeFragment newInstance(Movie movie) {
        MovieEpisodeFragment fragment = new MovieEpisodeFragment();
        Bundle args = new Bundle();
        args.putSerializable("movie", movie);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            movie = (Movie) getArguments().getSerializable("movie");
        }
        sessionManager = new UserSessionManager(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_movie_episode, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupRecyclerView();
        loadEpisodes();
    }

    @Override
    public void onResume() {
        super.onResume();
        sessionManager = new UserSessionManager(requireContext());
        boolean isVip = sessionManager.isPremium();

        if (episodeAdapter != null) {
            episodeAdapter.setUserIsPremium(isVip);
            episodeAdapter.notifyDataSetChanged();
        }

        if (isVip && getActivity() instanceof MovieDetailActivity) {
            ((MovieDetailActivity) getActivity()).refreshMovieData();
        }
    }

    public void updateEpisodes(List<Episode> newEpisodes) {
        if (newEpisodes == null) return;
        if (episodeAdapter != null) {
            episodeAdapter.updateData(newEpisodes);
        }
        showEmptyState(newEpisodes.isEmpty());
    }

    private void initViews(View view) {
        rvEpisodes = view.findViewById(R.id.rvEpisodes);
        tvEmptyState = view.findViewById(R.id.tvEmptyState);
    }

    private void setupRecyclerView() {
        if (getContext() == null) return;
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvEpisodes.setLayoutManager(layoutManager);

        episodeAdapter = new EpisodeAdapter(new ArrayList<>(), episode -> {
            handleEpisodeClick(episode);
        }, sessionManager.isPremium());

        rvEpisodes.setAdapter(episodeAdapter);
    }

    private void handleEpisodeClick(Episode episode) {
        if (PremiumUtils.canUserAccessEpisode(episode, sessionManager)) {
            // Chuẩn bị Intent play video
            Intent intent = new Intent(getContext(), ExoVideoPlayerActivity.class);
            intent.putExtra(ExoVideoPlayerActivity.EXTRA_MOVIE_ID, movie.getId());
            intent.putExtra(ExoVideoPlayerActivity.EXTRA_EPISODE_ID, episode.getId());
            intent.putExtra(ExoVideoPlayerActivity.EXTRA_MOVIE_TITLE, movie.getTitle());

            // [BƯỚC 3] Gọi hàm lưu lịch sử TRƯỚC khi chuyển màn hình
            addToWatchHistory(movie.getId());

            PremiumUtils.logPremiumAccess(episode, sessionManager.isPremium(), true);
            startActivity(intent);
        } else {
            PremiumUtils.showPremiumDialog(getContext(), episode);
            PremiumUtils.logPremiumAccess(episode, sessionManager.isPremium(), false);
        }
    }

    // [BƯỚC 2] Hàm xử lý gọi API ngầm
    private void addToWatchHistory(int movieId) {
        // Chỉ gọi API nếu user đã đăng nhập
        if (!sessionManager.isLoggedIn()) {
            return;
        }

        Log.d(TAG, "addToWatchHistory: Adding movie " + movieId + " to history...");

        // Gọi Repository đã có sẵn
        historyRepository.addToHistory(movieId, new RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                // Thành công: Log lại để debug
                Log.d(TAG, "API Success: Added to history");
            }

            @Override
            public void onError(String errorMessage) {
                // Thất bại: Log lỗi (không cần show Toast làm phiền user)
                Log.e(TAG, "API Error: " + errorMessage);
            }
        });
    }

    private void loadEpisodes() {
        if (!isAdded() || movie == null) return;
        List<Episode> episodes = movie.getEpisodes();
        if (episodes != null && !episodes.isEmpty()) {
            episodeAdapter.updateData(episodes);
            showEmptyState(false);
        } else {
            showEmptyState(true);
        }
    }

    private void showEmptyState(boolean show) {
        if (tvEmptyState != null && rvEpisodes != null) {
            tvEmptyState.setVisibility(show ? View.VISIBLE : View.GONE);
            rvEpisodes.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}