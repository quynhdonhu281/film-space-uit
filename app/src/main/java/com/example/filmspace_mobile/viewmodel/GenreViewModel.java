package com.example.filmspace_mobile.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.filmspace_mobile.data.model.movie.Genre;
import com.example.filmspace_mobile.data.repository.GenreRepository;
import com.example.filmspace_mobile.data.repository.RepositoryCallback;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class GenreViewModel extends ViewModel {
    private static final String TAG = "GenreViewModel";

    private final GenreRepository genreRepository;

    private final MutableLiveData<List<Genre>> genresLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> genresErrorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> genresLoadingLiveData = new MutableLiveData<>();

    @Inject
    public GenreViewModel(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    // Getters for LiveData
    public LiveData<List<Genre>> getGenres() { return genresLiveData; }
    public LiveData<String> getGenresError() { return genresErrorLiveData; }
    public LiveData<Boolean> getGenresLoading() { return genresLoadingLiveData; }

    /**
     * Đồng bộ tên hàm với SearchFragment
     */
    public void fetchAllGenres() {
        fetchGenres();
    }

    /**
     * Fetch all genres từ Repository
     */
    public void fetchGenres() {
        // Kiểm tra nếu đã có dữ liệu rồi thì không cần load lại (tối ưu performance)
        if (genresLiveData.getValue() != null && !genresLiveData.getValue().isEmpty()) {
            return;
        }

        genresLoadingLiveData.setValue(true);
        genreRepository.getAllGenres(new RepositoryCallback<List<Genre>>() {
            @Override
            public void onSuccess(List<Genre> genres) {
                genresLoadingLiveData.postValue(false); // Dùng postValue cho an toàn từ background thread
                genresLiveData.postValue(genres);
            }

            @Override
            public void onError(String errorMessage) {
                genresLoadingLiveData.postValue(false);
                genresErrorLiveData.postValue(errorMessage);
            }
        });
    }
}