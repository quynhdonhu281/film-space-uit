package com.example.filmspace_mobile.ui.movie;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.filmspace_mobile.R;
import com.example.filmspace_mobile.data.model.movie.Episode;
import com.example.filmspace_mobile.ui.adapters.EpisodeAdapter;
import java.util.ArrayList;
import java.util.List;

public class MovieEpisodeFragment extends Fragment {

    private RecyclerView rvEpisodes;
    private EpisodeAdapter episodeAdapter;

    public static MovieEpisodeFragment newInstance() {
        return new MovieEpisodeFragment();
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
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvEpisodes.setLayoutManager(layoutManager);

        episodeAdapter = new EpisodeAdapter(new ArrayList<>(), episode -> {
            // Handle episode click - play video
            Toast.makeText(getContext(),
                    "Play: " + episode.getTitle(),
                    Toast.LENGTH_SHORT).show();
            // TODO: Navigate to video player
        });

        rvEpisodes.setAdapter(episodeAdapter);
    }

    private void loadEpisodes() {
        // Get movie from parent activity
        MovieDetailActivity activity = (MovieDetailActivity) getActivity();
        if (activity == null) return;
        
        com.example.filmspace_mobile.data.model.movie.Movie movie = activity.getMovie();
        if (movie == null) return;
        
        // Load episodes from movie object
        List<Episode> episodes = movie.getEpisodes();
        if (episodes != null && !episodes.isEmpty()) {
            episodeAdapter.updateData(episodes);
        } else {
            // No episodes available
            Toast.makeText(getContext(), "No episodes available", Toast.LENGTH_SHORT).show();
        }
    }
}