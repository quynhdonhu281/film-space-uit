package com.example.filmspace_mobile.ui.auth.RegisterFragment;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.filmspace_mobile.data.api.ApiService;
import com.example.filmspace_mobile.data.api.RetrofitClient;
import com.example.filmspace_mobile.data.model.auth.RegisterRequest;
import com.example.filmspace_mobile.data.model.auth.RegisterResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterViewModel extends AndroidViewModel {
    private final ApiService apiService;

    private final MutableLiveData<RegisterResponse> registerResponseLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> registerErrorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> registerLoadingLiveData = new MutableLiveData<>();

    public RegisterViewModel(@NonNull Application application) {
        super(application);
        apiService = RetrofitClient.getApiService();
    }

    public LiveData<RegisterResponse> getRegisterResponse() { return registerResponseLiveData; }
    public LiveData<String> getRegisterError() { return registerErrorLiveData; }
    public LiveData<Boolean> getRegisterLoading() { return registerLoadingLiveData; }

    public void register(String email, String password, String username, String fullname) {
        registerLoadingLiveData.setValue(true);
        RegisterRequest request = new RegisterRequest(email, password, username, fullname);

        apiService.register(request).enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                registerLoadingLiveData.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    registerResponseLiveData.setValue(response.body());
                } else if (response.code() == 400) {
                    registerErrorLiveData.setValue("Registration failed: User already exists or invalid data");
                } else {
                    registerErrorLiveData.setValue("Registration failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                registerLoadingLiveData.setValue(false);
                registerErrorLiveData.setValue("Error: " + t.getMessage());
            }
        });
    }
}
