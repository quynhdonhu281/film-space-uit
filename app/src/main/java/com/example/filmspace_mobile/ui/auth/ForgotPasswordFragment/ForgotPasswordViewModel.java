package com.example.filmspace_mobile.ui.auth.ForgotPasswordFragment;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.filmspace_mobile.data.api.ApiService;
import com.example.filmspace_mobile.data.api.RetrofitClient;
import com.example.filmspace_mobile.data.model.auth.ForgotPasswordRequest;
import com.example.filmspace_mobile.data.model.auth.ForgotPasswordResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordViewModel extends AndroidViewModel {
    private final ApiService apiService;

    private final MutableLiveData<String> forgotPasswordResponseLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> forgotPasswordErrorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> forgotPasswordLoadingLiveData = new MutableLiveData<>();

    public ForgotPasswordViewModel(@NonNull Application application) {
        super(application);
        apiService = RetrofitClient.getApiService();
    }

    public LiveData<String> getForgotPasswordResponse() { return forgotPasswordResponseLiveData; }
    public LiveData<String> getForgotPasswordError() { return forgotPasswordErrorLiveData; }
    public LiveData<Boolean> getForgotPasswordLoading() { return forgotPasswordLoadingLiveData; }

    public void forgotPassword(String email) {
        forgotPasswordLoadingLiveData.setValue(true);
        ForgotPasswordRequest request = new ForgotPasswordRequest(email);

        apiService.forgotPassword(request).enqueue(new Callback<ForgotPasswordResponse>() {
            @Override
            public void onResponse(Call<ForgotPasswordResponse> call, Response<ForgotPasswordResponse> response) {
                forgotPasswordLoadingLiveData.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    String message = response.body().getMessage();
                    forgotPasswordResponseLiveData.setValue(message != null ? message : "OTP sent successfully");
                } else {
                    forgotPasswordErrorLiveData.setValue("Failed to send OTP");
                }
            }

            @Override
            public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
                forgotPasswordLoadingLiveData.setValue(false);
                forgotPasswordErrorLiveData.setValue("Error: " + t.getMessage());
            }
        });
    }
}
