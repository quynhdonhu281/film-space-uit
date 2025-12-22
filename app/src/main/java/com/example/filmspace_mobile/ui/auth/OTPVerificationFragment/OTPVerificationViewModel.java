package com.example.filmspace_mobile.ui.auth.OTPVerificationFragment;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.filmspace_mobile.BuildConfig;
import com.example.filmspace_mobile.data.api.ApiService;
import com.example.filmspace_mobile.data.api.RetrofitClient;
import com.example.filmspace_mobile.data.local.UserSessionManager;
import com.example.filmspace_mobile.data.model.auth.ResendOTPRequest;
import com.example.filmspace_mobile.data.model.auth.ResendOTPResponse;
import com.example.filmspace_mobile.data.model.auth.VerifyOTPRequest;
import com.example.filmspace_mobile.data.model.auth.VerifyOTPResponse;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OTPVerificationViewModel extends AndroidViewModel {
    private static final String TAG = "OTPVerificationViewModel";
    private final ApiService apiService;
    private final UserSessionManager sessionManager;

    private final MutableLiveData<VerifyOTPResponse> verifyOTPResponseLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> verifyOTPErrorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> verifyOTPLoadingLiveData = new MutableLiveData<>();

    private final MutableLiveData<String> resendOTPResponseLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> resendOTPErrorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> resendOTPLoadingLiveData = new MutableLiveData<>();

    public OTPVerificationViewModel(@NonNull Application application) {
        super(application);
        apiService = RetrofitClient.getApiService();
        sessionManager = new UserSessionManager(application.getApplicationContext());
    }

    public LiveData<VerifyOTPResponse> getVerifyOTPResponse() { return verifyOTPResponseLiveData; }
    public LiveData<String> getVerifyOTPError() { return verifyOTPErrorLiveData; }
    public LiveData<Boolean> getVerifyOTPLoading() { return verifyOTPLoadingLiveData; }

    public LiveData<String> getResendOTPResponse() { return resendOTPResponseLiveData; }
    public LiveData<String> getResendOTPError() { return resendOTPErrorLiveData; }
    public LiveData<Boolean> getResendOTPLoading() { return resendOTPLoadingLiveData; }

    public UserSessionManager getSessionManager() { return sessionManager; }

    public void verifyOTP(String email, String otp) {
        verifyOTPLoadingLiveData.setValue(true);
        VerifyOTPRequest request = new VerifyOTPRequest(email, otp);

        apiService.verifyOtp(request).enqueue(new Callback<VerifyOTPResponse>() {
            @Override
            public void onResponse(Call<VerifyOTPResponse> call, Response<VerifyOTPResponse> response) {
                verifyOTPLoadingLiveData.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    VerifyOTPResponse verifyResponse = response.body();
                    sessionManager.saveUserSession(
                            verifyResponse.getUserId(),
                            verifyResponse.getUsername(),
                            verifyResponse.getEmail(),
                            verifyResponse.getAvatarUrl(),
                            verifyResponse.getName(),
                            verifyResponse.getToken()
                    );
                    verifyOTPResponseLiveData.setValue(verifyResponse);
                } else {
                    String errorMessage = "OTP verification failed";
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            if (BuildConfig.DEBUG) {
                                Log.e(TAG, "Error body: " + errorBody);
                            }
                            errorMessage = "Invalid or expired OTP. Please try again.";
                        }
                    } catch (IOException e) {
                        if (BuildConfig.DEBUG) {
                            Log.e(TAG, "Error reading error body", e);
                        }
                    }
                    verifyOTPErrorLiveData.setValue(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<VerifyOTPResponse> call, Throwable t) {
                verifyOTPLoadingLiveData.setValue(false);
                verifyOTPErrorLiveData.setValue("Error: " + t.getMessage());
            }
        });
    }

    public void resendOTP(String email) {
        resendOTPLoadingLiveData.setValue(true);
        ResendOTPRequest request = new ResendOTPRequest(email);

        apiService.resendOtp(request).enqueue(new Callback<ResendOTPResponse>() {
            @Override
            public void onResponse(Call<ResendOTPResponse> call, Response<ResendOTPResponse> response) {
                resendOTPLoadingLiveData.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    String message = response.body().getMessage();
                    resendOTPResponseLiveData.setValue(message != null ? message : "OTP resent successfully");
                } else {
                    resendOTPErrorLiveData.setValue("Failed to resend OTP");
                }
            }

            @Override
            public void onFailure(Call<ResendOTPResponse> call, Throwable t) {
                resendOTPLoadingLiveData.setValue(false);
                resendOTPErrorLiveData.setValue("Error: " + t.getMessage());
            }
        });
    }
}
