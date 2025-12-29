package com.example.filmspace_mobile.ui.movie;

import android.content.Intent;
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
            // Handle episode click - navigate to video player
            Intent intent = new Intent(getContext(), VideoPlayerActivity.class);
            intent.putExtra(VideoPlayerActivity.EXTRA_MOVIE_ID, 1); // TODO: Get actual movie ID
            intent.putExtra(VideoPlayerActivity.EXTRA_EPISODE_ID, episode.getId());
            intent.putExtra(VideoPlayerActivity.EXTRA_MOVIE_TITLE, "Stranger Things"); // TODO: Get actual movie title
            startActivity(intent);
        });

        rvEpisodes.setAdapter(episodeAdapter);
    }

    private void loadEpisodes() {
        // TODO: Load from API
        // For now, load dummy data
        List<Episode> dummyEpisodes = new ArrayList<>();

        dummyEpisodes.add(new Episode(
                1, 1,
                "The Vanishing of Will Byers",
                "On his way home from a friend's house, young Will sees something terrifying. Nearby, a sinister secret lurks in the depths of a government lab.",
                "",
                49,
                "2016-07-15"
        ));

        dummyEpisodes.add(new Episode(
                2, 2,
                "The Weirdo on Maple Street",
                "Lucas, Mike, and Dustin try to talk to the girl found in the woods. Hopper questions an anxious Joyce about an unsettling phone call.",
                "",
                56,
                "2016-07-15"
        ));

        dummyEpisodes.add(new Episode(
                3, 3,
                "Holly, Jolly",
                "An increasingly concerned Nancy looks for Barb and finds out what Jonathan's been up to. Joyce is convinced Will is trying to talk to her.",
                "",
                52,
                "2016-07-15"
        ));

        dummyEpisodes.add(new Episode(
                4, 4,
                "The Body",
                "Refusing to believe Will is dead, Joyce tries to connect with her son. The boys give Eleven a makeover. Nancy and Jonathan form an unlikely alliance.",
                "",
                50,
                "2016-07-15"
        ));

        episodeAdapter.updateData(dummyEpisodes);
    }
}