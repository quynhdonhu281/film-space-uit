package com.example.filmspace_mobile.data.repository;

import com.example.filmspace_mobile.data.api.ApiService;
import com.example.filmspace_mobile.data.model.history.AddToHistoryRequest;
import com.example.filmspace_mobile.data.model.history.WatchHistoryResponse;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class HistoryRepository {
    private final ApiService apiService;

    @Inject
    public HistoryRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    /**
     * Get watched movies history
     */
    public void getWatchHistory(int page, int pageSize, RepositoryCallback<WatchHistoryResponse> callback) {
        apiService.getWatchHistory(page, pageSize).enqueue(new Callback<WatchHistoryResponse>() {
            @Override
            public void onResponse(Call<WatchHistoryResponse> call, Response<WatchHistoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to load watch history");
                }
            }

            @Override
            public void onFailure(Call<WatchHistoryResponse> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    /**
     * Add movie to watch history
     */
    public void addToHistory(int movieId, RepositoryCallback<Void> callback) {
        AddToHistoryRequest request = new AddToHistoryRequest(movieId);
        
        apiService.addToHistory(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("Failed to add to history");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    /**
     * Delete movie from watch history
     */
    public void deleteFromHistory(int movieId, RepositoryCallback<Void> callback) {
        apiService.deleteFromHistory(movieId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("Failed to delete from history");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    /**
     * Clear all watch history
     */
    public void clearAllHistory(RepositoryCallback<Void> callback) {
        apiService.clearAllHistory().enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("Failed to clear history");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
}
