package com.example.filmspace_mobile.data.repository;

import com.example.filmspace_mobile.data.api.ApiService;
import com.example.filmspace_mobile.data.model.movie.CreateReviewRequest;
import com.example.filmspace_mobile.data.model.movie.Review;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class ReviewRepository {
    private final ApiService apiService;

    @Inject
    public ReviewRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    /**
     * Submit a review for a movie
     */
    public void createReview(int movieId, int userId, int rating, String comment, 
                            RepositoryCallback<Review> callback) {
        CreateReviewRequest request = new CreateReviewRequest(movieId, userId, rating, comment);
        
        apiService.createReview(request).enqueue(new Callback<Review>() {
            @Override
            public void onResponse(Call<Review> call, Response<Review> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to submit review");
                }
            }

            @Override
            public void onFailure(Call<Review> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
}
