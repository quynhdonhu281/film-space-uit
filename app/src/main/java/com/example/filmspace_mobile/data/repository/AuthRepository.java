package com.example.filmspace_mobile.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.filmspace_mobile.BuildConfig;
import com.example.filmspace_mobile.data.api.ApiService;
import com.example.filmspace_mobile.data.local.UserSessionManager;
import com.example.filmspace_mobile.data.model.auth.*;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class AuthRepository {
    private static final String TAG = "AuthRepository";
    
    private final ApiService apiService;
    private final UserSessionManager sessionManager;

    @Inject
    public AuthRepository(ApiService apiService, UserSessionManager sessionManager) {
        this.apiService = apiService;
        this.sessionManager = sessionManager;
    }

    public UserSessionManager getSessionManager() {
        return sessionManager;
    }

    // Login
    public void login(String email, String password,
                     RepositoryCallback<LoginResponse> callback) {
        LoginRequest request = new LoginRequest(email, password);
        
        apiService.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
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
                    callback.onSuccess(loginResponse);
                } else {
                    callback.onError(getErrorMessage(response.code(), "sign in"));
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                callback.onError(getNetworkErrorMessage(t));
            }
        });
    }

    // Register
    public void register(String email, String password, String username, String fullname,
                        RepositoryCallback<RegisterResponse> callback) {
        RegisterRequest request = new RegisterRequest(email, password, username, fullname);
        
        apiService.register(request).enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String error = "Registration failed. Please try again.";
                    if (response.code() == 409) {
                        error = "Email or username already exists";
                    }
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            if (errorBody.contains("message")) {
                                Gson gson = new Gson();
                                ErrorResponse errorResponse = gson.fromJson(errorBody, ErrorResponse.class);
                                if (errorResponse != null && errorResponse.message != null) {
                                    error = errorResponse.message;
                                }
                            }
                        }
                    } catch (IOException ignored) {}
                    callback.onError(error);
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                callback.onError(getNetworkErrorMessage(t));
            }
        });
    }

    // Verify OTP
    public void verifyOTP(String email, String otp,
                         RepositoryCallback<VerifyOTPResponse> callback) {
        VerifyOTPRequest request = new VerifyOTPRequest(email, otp);
        
        apiService.verifyOtp(request).enqueue(new Callback<VerifyOTPResponse>() {
            @Override
            public void onResponse(Call<VerifyOTPResponse> call, Response<VerifyOTPResponse> response) {
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
                    callback.onSuccess(verifyResponse);
                } else {
                    callback.onError("Invalid or expired OTP. Please try again.");
                }
            }

            @Override
            public void onFailure(Call<VerifyOTPResponse> call, Throwable t) {
                callback.onError(getNetworkErrorMessage(t));
            }
        });
    }

    // Resend OTP
    public void resendOTP(String email, RepositoryCallback<String> callback) {
        ResendOTPRequest request = new ResendOTPRequest(email);
        
        apiService.resendOtp(request).enqueue(new Callback<ResendOTPResponse>() {
            @Override
            public void onResponse(Call<ResendOTPResponse> call, Response<ResendOTPResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String message = response.body().getMessage();
                    callback.onSuccess(message != null ? message : "OTP resent successfully");
                } else {
                    callback.onError("Failed to resend OTP. Please try again.");
                }
            }

            @Override
            public void onFailure(Call<ResendOTPResponse> call, Throwable t) {
                callback.onError(getNetworkErrorMessage(t));
            }
        });
    }

    // Forgot Password
    public void forgotPassword(String email, RepositoryCallback<String> callback) {
        ForgotPasswordRequest request = new ForgotPasswordRequest(email);
        
        apiService.forgotPassword(request).enqueue(new Callback<ForgotPasswordResponse>() {
            @Override
            public void onResponse(Call<ForgotPasswordResponse> call, Response<ForgotPasswordResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String message = response.body().getMessage();
                    callback.onSuccess(message != null ? message : "OTP sent successfully");
                } else {
                    String error = response.code() == 404 
                        ? "Email not found. Please check your email." 
                        : "Failed to send OTP. Please try again.";
                    callback.onError(error);
                }
            }

            @Override
            public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
                callback.onError(getNetworkErrorMessage(t));
            }
        });
    }

    // Reset Password
    public void resetPassword(String email, String otp, String newPassword,
                             RepositoryCallback<ResetPasswordResponse> callback) {
        ResetPasswordRequest request = new ResetPasswordRequest(email, otp, newPassword);
        
        apiService.resetPassword(request).enqueue(new Callback<ResetPasswordResponse>() {
            @Override
            public void onResponse(Call<ResetPasswordResponse> call, Response<ResetPasswordResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String error = (response.code() == 401 || response.code() == 400)
                        ? "Invalid or expired OTP. Please try again." 
                        : "Failed to reset password. Please try again.";
                    callback.onError(error);
                }
            }

            @Override
            public void onFailure(Call<ResetPasswordResponse> call, Throwable t) {
                callback.onError(getNetworkErrorMessage(t));
            }
        });
    }

    // Logout
    public void logout(RepositoryCallback<Void> callback) {
        apiService.logout().enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                // Clear session regardless of server response
                sessionManager.clearSession();
                
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    // Still call success since local session is cleared
                    callback.onSuccess(null);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Clear session even on network failure
                sessionManager.clearSession();
                callback.onSuccess(null);
            }
        });
    }

    // Helper methods for error messages
    private String getErrorMessage(int code, String action) {
        switch (code) {
            case 401:
                return "Invalid email or password";
            case 403:
                return "Account is disabled. Contact support.";
            case 404:
                return "Service unavailable. Try again later.";
            case 500:
            case 502:
            case 503:
                return "Server error. Please try again later.";
            default:
                return "Unable to " + action + ". Please try again.";
        }
    }

    private String getNetworkErrorMessage(Throwable t) {
        if (t instanceof UnknownHostException || t instanceof IOException) {
            return "No internet connection. Please check your network.";
        } else if (t instanceof SocketTimeoutException) {
            return "Request timed out. Please try again.";
        } else {
            return "Something went wrong. Please try again later.";
        }
    }

    // Helper class for error response parsing
    private static class ErrorResponse {
        String message;
        String error;
    }
}
