package com.example.filmspace_mobile.ui.movie;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.filmspace_mobile.R;
import com.example.filmspace_mobile.data.local.UserSessionManager;
import com.example.filmspace_mobile.data.model.movie.Review;
import com.example.filmspace_mobile.data.repository.RepositoryCallback;
import com.example.filmspace_mobile.data.repository.ReviewRepository;
import com.example.filmspace_mobile.ui.adapters.ReviewAdapter;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MovieReviewFragment extends Fragment {

    private RecyclerView rvReviews;
    private ReviewAdapter reviewAdapter;
    private LinearLayout submitReviewSection;
    private TextView loginPrompt;
    private RatingBar ratingBar;
    private EditText reviewCommentInput;
    private Button btnSubmitReview;

    @Inject
    UserSessionManager sessionManager;

    @Inject
    ReviewRepository reviewRepository;

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
        setupSubmitReview();
        loadReviews();
    }

    private void initViews(View view) {
        rvReviews = view.findViewById(R.id.rvReviews);
        submitReviewSection = view.findViewById(R.id.submitReviewSection);
        loginPrompt = view.findViewById(R.id.loginPrompt);
        ratingBar = view.findViewById(R.id.ratingBar);
        reviewCommentInput = view.findViewById(R.id.reviewCommentInput);
        btnSubmitReview = view.findViewById(R.id.btnSubmitReview);
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvReviews.setLayoutManager(layoutManager);

        reviewAdapter = new ReviewAdapter(new ArrayList<>());
        rvReviews.setAdapter(reviewAdapter);
    }

    private void setupSubmitReview() {
        // Check if user is logged in
        if (sessionManager.isLoggedIn()) {
            submitReviewSection.setVisibility(View.VISIBLE);
            loginPrompt.setVisibility(View.GONE);

            btnSubmitReview.setOnClickListener(v -> submitReview());
        } else {
            submitReviewSection.setVisibility(View.GONE);
            loginPrompt.setVisibility(View.VISIBLE);
        }
    }

    private void submitReview() {
        MovieDetailActivity activity = (MovieDetailActivity) getActivity();
        if (activity == null) return;

        com.example.filmspace_mobile.data.model.movie.Movie movie = activity.getMovie();
        if (movie == null) return;

        String comment = reviewCommentInput.getText().toString().trim();
        int rating = (int) ratingBar.getRating();

        if (comment.isEmpty()) {
            reviewCommentInput.setError("Please write a comment");
            reviewCommentInput.requestFocus();
            return;
        }

        if (rating == 0) {
            if (getView() != null) {
                Snackbar.make(getView(), R.string.please_select_rating, Snackbar.LENGTH_SHORT).show();
            }
            return;
        }

        // Disable button while submitting
        btnSubmitReview.setEnabled(false);
        btnSubmitReview.setText("Submitting...");

        int userId = sessionManager.getUserId();
        reviewRepository.createReview(movie.getId(), userId, rating, comment, new RepositoryCallback<Review>() {
            @Override
            public void onSuccess(Review review) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (getView() != null) {
                            Snackbar.make(getView(), R.string.review_submitted, Snackbar.LENGTH_SHORT).show();
                        }
                        reviewCommentInput.setText("");
                        ratingBar.setRating(5);
                        btnSubmitReview.setEnabled(true);
                        btnSubmitReview.setText("Submit Review");
                        
                        // Reload reviews
                        loadReviews();
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (getView() != null) {
                            Snackbar.make(getView(), error, Snackbar.LENGTH_LONG).show();
                        }
                        btnSubmitReview.setEnabled(true);
                        btnSubmitReview.setText("Submit Review");
                    });
                }
            }
        });
    }

    private void loadReviews() {
        // Add lifecycle safety checks
        if (!isAdded() || getActivity() == null || getActivity().isFinishing()) {
            return;
        }
        
        // Get movie from parent activity
        MovieDetailActivity activity = (MovieDetailActivity) getActivity();
        if (activity == null) return;
        
        com.example.filmspace_mobile.data.model.movie.Movie movie = activity.getMovie();
        if (movie == null) return;
        
        // Load reviews from movie object
        List<Review> reviews = movie.getReviews();
        if (reviews != null && !reviews.isEmpty()) {
            reviewAdapter.updateData(reviews);
        }
        // Don't show "no reviews" Snackbar during initial load to avoid crash
    }
}