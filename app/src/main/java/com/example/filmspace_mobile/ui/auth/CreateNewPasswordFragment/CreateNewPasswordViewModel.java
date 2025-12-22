package com.example.filmspace_mobile.ui.auth.CreateNewPasswordFragment;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.filmspace_mobile.data.api.ApiService;
import com.example.filmspace_mobile.data.api.RetrofitClient;
import com.example.filmspace_mobile.data.model.auth.ForgotPasswordRequest;
import com.example.filmspace_mobile.data.model.auth.ForgotPasswordResponse;
import com.example.filmspace_mobile.data.model.auth.ResetPasswordRequest;
import com.example.filmspace_mobile.data.model.auth.ResetPasswordResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateNewPasswordViewModel extends AndroidViewModel {
    private final ApiService apiService;

    private final MutableLiveData<String> forgotPasswordResponseLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> forgotPasswordErrorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> forgotPasswordLoadingLiveData = new MutableLiveData<>();

    private final MutableLiveData<ResetPasswordResponse> resetPasswordResponseLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> resetPasswordErrorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> resetPasswordLoadingLiveData = new MutableLiveData<>();

    public CreateNewPasswordViewModel(@NonNull Application application) {
        super(application);
        apiService = RetrofitClient.getApiService();
    }

    public LiveData<String> getForgotPasswordResponse() { return forgotPasswordResponseLiveData; }
    public LiveData<String> getForgotPasswordError() { return forgotPasswordErrorLiveData; }
    public LiveData<Boolean> getForgotPasswordLoading() { return forgotPasswordLoadingLiveData; }

    public LiveData<ResetPasswordResponse> getResetPasswordResponse() { return resetPasswordResponseLiveData; }
    public LiveData<String> getResetPasswordError() { return resetPasswordErrorLiveData; }
    public LiveData<Boolean> getResetPasswordLoading() { return resetPasswordLoadingLiveData; }

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

    public void resetPassword(String email, String otp, String newPassword) {
        resetPasswordLoadingLiveData.setValue(true);
        ResetPasswordRequest request = new ResetPasswordRequest(email, otp, newPassword);

        apiService.resetPassword(request).enqueue(new Callback<ResetPasswordResponse>() {
            @Override
            public void onResponse(Call<ResetPasswordResponse> call, Response<ResetPasswordResponse> response) {
                resetPasswordLoadingLiveData.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    resetPasswordResponseLiveData.setValue(response.body());
                } else {
                    resetPasswordErrorLiveData.setValue("Password reset failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResetPasswordResponse> call, Throwable t) {
                resetPasswordLoadingLiveData.setValue(false);
                resetPasswordErrorLiveData.setValue("Error: " + t.getMessage());
            }
        });
    }
}
