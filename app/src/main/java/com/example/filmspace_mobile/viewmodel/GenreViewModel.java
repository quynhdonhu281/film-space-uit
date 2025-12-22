package com.example.filmspace_mobile.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.filmspace_mobile.data.api.ApiService;
import com.example.filmspace_mobile.data.api.RetrofitClient;
import com.example.filmspace_mobile.data.model.movie.Genre;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GenreViewModel extends AndroidViewModel {
    private final ApiService apiService;

    // Genres
    private final MutableLiveData<List<Genre>> genresLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> genresErrorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> genresLoadingLiveData = new MutableLiveData<>();

    public GenreViewModel(@NonNull Application application) {
        super(application);
        apiService = RetrofitClient.getApiService();
    }

    // Getters for LiveData
    public LiveData<List<Genre>> getGenres() { return genresLiveData; }
    public LiveData<String> getGenresError() { return genresErrorLiveData; }
    public LiveData<Boolean> getGenresLoading() { return genresLoadingLiveData; }

    // Fetch all genres
    public void fetchGenres() {
        genresLoadingLiveData.setValue(true);
        apiService.getAllGenres().enqueue(new Callback<List<Genre>>() {
            @Override
            public void onResponse(Call<List<Genre>> call, Response<List<Genre>> response) {
                genresLoadingLiveData.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    genresLiveData.setValue(response.body());
                } else {
                    genresErrorLiveData.setValue("Failed to load genres");
                }
            }

            @Override
            public void onFailure(Call<List<Genre>> call, Throwable t) {
                genresLoadingLiveData.setValue(false);
                genresErrorLiveData.setValue("Error: " + t.getMessage());
            }
        });
    }
}
