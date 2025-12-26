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
import com.example.filmspace_mobile.data.model.movie.Review;
import com.example.filmspace_mobile.ui.adapters.ReviewAdapter;
import java.util.ArrayList;
import java.util.List;

public class MovieReviewFragment extends Fragment {

    private RecyclerView rvReviews;
    private ReviewAdapter reviewAdapter;

    public static MovieReviewFragment newInstance() {
        return new MovieReviewFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_movie_review, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupRecyclerView();
        loadReviews();
    }

    private void initViews(View view) {
        rvReviews = view.findViewById(R.id.rvReviews);
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvReviews.setLayoutManager(layoutManager);

        reviewAdapter = new ReviewAdapter(new ArrayList<>());
        rvReviews.setAdapter(reviewAdapter);
    }

    private void loadReviews() {
        // Get movie from parent activity
        MovieDetailActivity activity = (MovieDetailActivity) getActivity();
        if (activity == null) return;
        
        com.example.filmspace_mobile.data.model.movie.Movie movie = activity.getMovie();
        if (movie == null) return;
        
        // Load reviews from movie object
        List<Review> reviews = movie.getReviews();
        if (reviews != null && !reviews.isEmpty()) {
            reviewAdapter.updateData(reviews);
        } else {
            // No reviews available
            Toast.makeText(getContext(), "No reviews yet", Toast.LENGTH_SHORT).show();
        }
    }
}