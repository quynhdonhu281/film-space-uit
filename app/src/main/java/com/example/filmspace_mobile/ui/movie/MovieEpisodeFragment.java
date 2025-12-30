package com.example.filmspace_mobile.ui.movie;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.filmspace_mobile.R;
import com.example.filmspace_mobile.data.local.UserSessionManager;
import com.example.filmspace_mobile.data.model.movie.Episode;
import com.example.filmspace_mobile.data.model.movie.Movie;
import com.example.filmspace_mobile.ui.adapters.EpisodeAdapter;
import java.util.ArrayList;
import java.util.List;

public class MovieEpisodeFragment extends Fragment {

    private RecyclerView rvEpisodes;
    private TextView tvEmptyState;
    private EpisodeAdapter episodeAdapter;
    private Movie movie;
    private UserSessionManager sessionManager;

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

    private void initViews(View view) {
        rvEpisodes = view.findViewById(R.id.rvEpisodes);
        tvEmptyState = view.findViewById(R.id.tvEmptyState);
    }

    private void setupRecyclerView() {
        if (getContext() == null) return;
        
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvEpisodes.setLayoutManager(layoutManager);

        // Initialize session manager to get user premium status
        if (sessionManager == null) {
            sessionManager = new UserSessionManager(getContext());
        }
        
        boolean userIsPremium = sessionManager.isPremium();

        episodeAdapter = new EpisodeAdapter(new ArrayList<>(), episode -> {
            // Handle episode click - navigate to video player
            Intent intent = new Intent(getContext(), VideoPlayerActivity.class);
            intent.putExtra(VideoPlayerActivity.EXTRA_MOVIE_ID, 1); // TODO: Get actual movie ID
            intent.putExtra(VideoPlayerActivity.EXTRA_EPISODE_ID, episode.getId());
            intent.putExtra(VideoPlayerActivity.EXTRA_MOVIE_TITLE, "Stranger Things"); // TODO: Get actual movie title
            startActivity(intent);
        }, userIsPremium);

        rvEpisodes.setAdapter(episodeAdapter);
    }

    private void loadEpisodes() {
        // Check if fragment is still attached and activity is not finishing
        if (!isAdded() || getContext() == null || getActivity() == null || getActivity().isFinishing()) {
            return;
        }
        
        // Get movie from arguments or parent activity
        if (movie == null) {
            if (getActivity() instanceof MovieDetailActivity) {
                MovieDetailActivity activity = (MovieDetailActivity) getActivity();
                movie = activity.getMovie();
            }
        }
        
        if (movie == null) {
            showEmptyState(true);
            Toast.makeText(getContext(), "Unable to load movie data", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Load episodes from movie object (from get movie by id response)
        List<Episode> episodes = movie.getEpisodes();
        if (episodes != null && !episodes.isEmpty()) {
            episodeAdapter.updateData(episodes);
            showEmptyState(false);
        } else {
            // No episodes available - show empty state
            showEmptyState(true);
            if (getContext() != null) {
                Toast.makeText(getContext(), "No episodes available", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showEmptyState(boolean show) {
        if (tvEmptyState != null && rvEpisodes != null) {
            tvEmptyState.setVisibility(show ? View.VISIBLE : View.GONE);
            rvEpisodes.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}