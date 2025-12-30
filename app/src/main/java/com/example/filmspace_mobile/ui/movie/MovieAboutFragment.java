package com.example.filmspace_mobile.ui.movie;

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
import com.example.filmspace_mobile.data.model.movie.Cast;
import com.example.filmspace_mobile.data.model.movie.Movie;
import com.example.filmspace_mobile.ui.adapters.CastAdapter;
import java.util.ArrayList;
import java.util.List;

public class MovieAboutFragment extends Fragment {

    private TextView tvStoryLine;
    private TextView btnShowMore;
    private RecyclerView rvCast;
    private CastAdapter castAdapter;

    private Movie movie;
    private boolean isExpanded = false;

    public static MovieAboutFragment newInstance(Movie movie) {
        MovieAboutFragment fragment = new MovieAboutFragment();
        Bundle args = new Bundle();
        // Pass movie data through arguments if needed
        fragment.setArguments(args);
        return fragment;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_movie_about, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupStoryLine();
        setupCastRecyclerView();
        loadData();
    }

    private void initViews(View view) {
        tvStoryLine = view.findViewById(R.id.tvStoryLine);
        btnShowMore = view.findViewById(R.id.btnShowMore);
        rvCast = view.findViewById(R.id.rvCast);
    }

    private void setupStoryLine() {
        btnShowMore.setOnClickListener(v -> {
            if (isExpanded) {
                tvStoryLine.setMaxLines(4);
                btnShowMore.setText("More");
                isExpanded = false;
            } else {
                tvStoryLine.setMaxLines(Integer.MAX_VALUE);
                btnShowMore.setText("Less");
                isExpanded = true;
            }
        });
    }

    private void setupCastRecyclerView() {
        if (getContext() == null) return;
        
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                getContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        );
        rvCast.setLayoutManager(layoutManager);

        castAdapter = new CastAdapter(new ArrayList<>(), cast -> {
            // Handle cast click - navigate to actor details
            if (getContext() != null) {
                Toast.makeText(getContext(), "Clicked: " + cast.getName(), Toast.LENGTH_SHORT).show();
            }
        });

        rvCast.setAdapter(castAdapter);
    }

    private void loadData() {
        if (!isAdded() || getContext() == null || getActivity() == null || getActivity().isFinishing()) {
            return;
        }
        
        // Get movie from parent activity if not already set
        if (movie == null && getActivity() instanceof MovieDetailActivity) {
            movie = ((MovieDetailActivity) getActivity()).getMovie();
        }
        
        if (movie != null) {
            // Display story line/description from movie response
            String description = movie.getOverview();
            if (description != null && !description.isEmpty()) {
                tvStoryLine.setText(description);
            } else {
                tvStoryLine.setText("No description available");
            }

            // Load cast from movie response (includes castList from API)
            if (movie.getCastList() != null && !movie.getCastList().isEmpty()) {
                castAdapter.updateData(movie.getCastList());
            } else {
                // Show empty state
                if (getContext() != null) {
                    Toast.makeText(getContext(), "No cast information available", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            // Load dummy data for testing
            loadDummyData();
        }
    }

    private void loadDummyData() {
        String dummyStory = "The film opens with Phineas Taylor \"P.T.\" Barnum (Hugh Jackman) joining his circus troupe in a song (\"The Greatest Show\"), playing to an enthusiastic crowd as he and his performers put on a dazzling show. We cut to Barnum as a young boy (Ellis Rubin) in the 1800's, working with his tailor father Philo...";
        tvStoryLine.setText(dummyStory);

        loadDummyCast();
    }

    private void loadDummyCast() {
        List<Cast> dummyCast = new ArrayList<>();
        dummyCast.add(new Cast(1, "Peter England", "Character 1", ""));
        dummyCast.add(new Cast(2, "Rosey Day", "Character 2", ""));
        dummyCast.add(new Cast(3, "John Doe", "Character 3", ""));
        dummyCast.add(new Cast(4, "Jane Smith", "Character 4", ""));

        castAdapter.updateData(dummyCast);
    }
}