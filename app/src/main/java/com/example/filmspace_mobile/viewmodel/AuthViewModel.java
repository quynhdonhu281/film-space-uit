package com.example.filmspace_mobile.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.filmspace_mobile.data.api.ApiService;
import com.example.filmspace_mobile.data.api.RetrofitClient;
import com.example.filmspace_mobile.data.local.UserSessionManager;
import com.example.filmspace_mobile.data.model.auth.ForgotPasswordRequest;
import com.example.filmspace_mobile.data.model.auth.ForgotPasswordResponse;
import com.example.filmspace_mobile.data.model.auth.LoginRequest;
import com.example.filmspace_mobile.data.model.auth.LoginResponse;
import com.example.filmspace_mobile.data.model.auth.RegisterRequest;
import com.example.filmspace_mobile.data.model.auth.RegisterResponse;
import com.example.filmspace_mobile.data.model.auth.ResendOTPRequest;
import com.example.filmspace_mobile.data.model.auth.ResendOTPResponse;
import com.example.filmspace_mobile.data.model.auth.ResetPasswordRequest;
import com.example.filmspace_mobile.data.model.auth.ResetPasswordResponse;
import com.example.filmspace_mobile.data.model.auth.VerifyOTPRequest;
import com.example.filmspace_mobile.data.model.auth.VerifyOTPResponse;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthViewModel extends AndroidViewModel {
    private static final String TAG = "AuthViewModel";

    private final ApiService apiService;
    private final UserSessionManager sessionManager;

    // Login
    private final MutableLiveData<LoginResponse> loginResponseLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> loginErrorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loginLoadingLiveData = new MutableLiveData<>();

    // Register
    private final MutableLiveData<RegisterResponse> registerResponseLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> registerErrorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> registerLoadingLiveData = new MutableLiveData<>();

    // Verify OTP
    private final MutableLiveData<VerifyOTPResponse> verifyOTPResponseLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> verifyOTPErrorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> verifyOTPLoadingLiveData = new MutableLiveData<>();

    // Resend OTP
    private final MutableLiveData<String> resendOTPResponseLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> resendOTPErrorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> resendOTPLoadingLiveData = new MutableLiveData<>();

    // Forgot Password
    private final MutableLiveData<String> forgotPasswordResponseLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> forgotPasswordErrorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> forgotPasswordLoadingLiveData = new MutableLiveData<>();

    // Reset Password
    private final MutableLiveData<ResetPasswordResponse> resetPasswordResponseLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> resetPasswordErrorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> resetPasswordLoadingLiveData = new MutableLiveData<>();

    public AuthViewModel(@NonNull Application application) {
        super(application);
        apiService = RetrofitClient.getApiService();
        sessionManager = new UserSessionManager(application.getApplicationContext());
    }

    // Getters for LiveData
    public LiveData<LoginResponse> getLoginResponse() { return loginResponseLiveData; }
    public LiveData<String> getLoginError() { return loginErrorLiveData; }
    public LiveData<Boolean> getLoginLoading() { return loginLoadingLiveData; }

    public LiveData<RegisterResponse> getRegisterResponse() { return registerResponseLiveData; }
    public LiveData<String> getRegisterError() { return registerErrorLiveData; }
    public LiveData<Boolean> getRegisterLoading() { return registerLoadingLiveData; }

    public LiveData<VerifyOTPResponse> getVerifyOTPResponse() { return verifyOTPResponseLiveData; }
    public LiveData<String> getVerifyOTPError() { return verifyOTPErrorLiveData; }
    public LiveData<Boolean> getVerifyOTPLoading() { return verifyOTPLoadingLiveData; }

    public LiveData<String> getResendOTPResponse() { return resendOTPResponseLiveData; }
    public LiveData<String> getResendOTPError() { return resendOTPErrorLiveData; }
    public LiveData<Boolean> getResendOTPLoading() { return resendOTPLoadingLiveData; }

    public LiveData<String> getForgotPasswordResponse() { return forgotPasswordResponseLiveData; }
    public LiveData<String> getForgotPasswordError() { return forgotPasswordErrorLiveData; }
    public LiveData<Boolean> getForgotPasswordLoading() { return forgotPasswordLoadingLiveData; }

    public LiveData<ResetPasswordResponse> getResetPasswordResponse() { return resetPasswordResponseLiveData; }
    public LiveData<String> getResetPasswordError() { return resetPasswordErrorLiveData; }
    public LiveData<Boolean> getResetPasswordLoading() { return resetPasswordLoadingLiveData; }

    // Session Manager
    public UserSessionManager getSessionManager() {
        return sessionManager;
    }

    // Login
    public void login(String email, String password) {
        loginLoadingLiveData.setValue(true);
        LoginRequest request = new LoginRequest(email, password);

        apiService.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                loginLoadingLiveData.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    // Save session
                    sessionManager.saveUserSession(
                            loginResponse.getUserId(),
                            loginResponse.getUsername(),
                            loginResponse.getEmail(),
                            loginResponse.getAvatarUrl(),
                            loginResponse.getName(),
                            loginResponse.getToken()
                    );
                    loginResponseLiveData.setValue(loginResponse);
                } else {
                    loginErrorLiveData.setValue("Login failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                loginLoadingLiveData.setValue(false);
                loginErrorLiveData.setValue("Error: " + t.getMessage());
            }
        });
    }

    // Register
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

    // Verify OTP
    public void verifyOTP(String email, String otp) {
        verifyOTPLoadingLiveData.setValue(true);
        VerifyOTPRequest request = new VerifyOTPRequest(email, otp);

        apiService.verifyOtp(request).enqueue(new Callback<VerifyOTPResponse>() {
            @Override
            public void onResponse(Call<VerifyOTPResponse> call, Response<VerifyOTPResponse> response) {
                verifyOTPLoadingLiveData.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    VerifyOTPResponse verifyResponse = response.body();
                    // Save session
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
                            Log.e(TAG, "Error body: " + errorBody);
                            errorMessage = "OTP verification failed: " + errorBody;
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading error body", e);
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

    // Resend OTP
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

    // Forgot Password
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

    // Reset Password
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
