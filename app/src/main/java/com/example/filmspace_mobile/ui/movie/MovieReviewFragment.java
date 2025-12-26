package com.example.filmspace_mobile.ui.movie;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        // TODO: Load from API
        // For now, load dummy data
        List<Review> dummyReviews = new ArrayList<>();

        dummyReviews.add(new Review(
                1,
                "Tarun Kumar",
                "",
                4.5f,
                "Amazing! The room is good than the picture. Thanks for amazing experience!",
                "2 days ago"
        ));

        dummyReviews.add(new Review(
                2,
                "Abhishek Kumar",
                "",
                5.0f,
                "The service is on point, and I really like the facilities. Good job!",
                "3 days ago"
        ));

        dummyReviews.add(new Review(
                3,
                "Mohit Yadav",
                "",
                5.0f,
                "The service is on point, and I really like the facilities. Good job!",
                "5 days ago"
        ));

        dummyReviews.add(new Review(
                4,
                "Payal Yadav",
                "",
                5.0f,
                "Excellent experience! The staff was very helpful and accommodating. Highly recommend!",
                "1 week ago"
        ));

        reviewAdapter.updateData(dummyReviews);
    }
}