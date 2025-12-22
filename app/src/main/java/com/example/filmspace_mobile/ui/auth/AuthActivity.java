package com.example.filmspace_mobile.ui.auth;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.filmspace_mobile.R;
import com.example.filmspace_mobile.data.api.ApiService;
import com.example.filmspace_mobile.data.api.RetrofitClient;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
    }

    public static class AuthViewModel extends ViewModel {
        private final ApiService apiService;

        // Login
        private final MutableLiveData<LoginResponse> loginResponseLiveData = new MutableLiveData<>();
        private final MutableLiveData<String> loginErrorLiveData = new MutableLiveData<>();
        private final MutableLiveData<Boolean> loginLoadingLiveData = new MutableLiveData<>();

        // Register
        private final MutableLiveData<RegisterResponse> registerResponseLiveData = new MutableLiveData<>();
        private final MutableLiveData<String> registerErrorLiveData = new MutableLiveData<>();
        private final MutableLiveData<Boolean> registerLoadingLiveData = new MutableLiveData<>();

        // OTP Verification
        private final MutableLiveData<VerifyOTPResponse> verifyOTPResponseLiveData = new MutableLiveData<>();
        private final MutableLiveData<String> verifyOTPErrorLiveData = new MutableLiveData<>();
        private final MutableLiveData<Boolean> verifyOTPLoadingLiveData = new MutableLiveData<>();

        // Resend OTP
        private final MutableLiveData<ResendOTPResponse> resendOTPResponseLiveData = new MutableLiveData<>();
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

        public AuthViewModel() {
            apiService = RetrofitClient.getApiService();
        }

        // ========== LOGIN ==========
        public void login(String email, String password) {
            loginLoadingLiveData.setValue(true);
            LoginRequest request = new LoginRequest(email, password);

            apiService.login(request).enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    loginLoadingLiveData.setValue(false);
                    if (response.isSuccessful() && response.body() != null) {
                        loginResponseLiveData.setValue(response.body());
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

        public LiveData<LoginResponse> getLoginResponse() {
            return loginResponseLiveData;
        }

        public LiveData<String> getLoginError() {
            return loginErrorLiveData;
        }

        public LiveData<Boolean> getLoginLoading() {
            return loginLoadingLiveData;
        }

        // ========== REGISTER ==========
        public void register(String email, String password, String username, String fullname) {
            registerLoadingLiveData.setValue(true);
            RegisterRequest request = new RegisterRequest(email, password, username, fullname);

            apiService.register(request).enqueue(new Callback<RegisterResponse>() {
                @Override
                public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                    registerLoadingLiveData.setValue(false);
                    if (response.isSuccessful() && response.body() != null) {
                        registerResponseLiveData.setValue(response.body());
                    } else if (!response.isSuccessful() && response.code() == 400) {
                        // Backend returns 400 even for successful registration with OTP sent
                        try {
                            String errorBody = response.errorBody().string();
                            android.util.Log.d("AuthViewModel", "Registration 400 response: " + errorBody);
                            // Parse error body as RegisterResponse
                            if (errorBody.contains("success") || errorBody.contains("OTP")) {
                                RegisterResponse fakeResponse = new RegisterResponse();
                                registerResponseLiveData.setValue(fakeResponse);
                            } else {
                                registerErrorLiveData.setValue("Registration failed: " + errorBody);
                            }
                        } catch (Exception e) {
                            registerErrorLiveData.setValue("Registration failed: " + response.message());
                        }
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

        public LiveData<RegisterResponse> getRegisterResponse() {
            return registerResponseLiveData;
        }

        public LiveData<String> getRegisterError() {
            return registerErrorLiveData;
        }

        public LiveData<Boolean> getRegisterLoading() {
            return registerLoadingLiveData;
        }

        // ========== OTP VERIFICATION ==========
        public void verifyOTP(String email, String otp) {
            verifyOTPLoadingLiveData.setValue(true);
            VerifyOTPRequest request = new VerifyOTPRequest(email, otp);

            apiService.verifyOtp(request).enqueue(new Callback<VerifyOTPResponse>() {
                @Override
                public void onResponse(Call<VerifyOTPResponse> call, Response<VerifyOTPResponse> response) {
                    verifyOTPLoadingLiveData.setValue(false);
                    if (response.isSuccessful() && response.body() != null) {
                        verifyOTPResponseLiveData.setValue(response.body());
                    } else {
                        // Log error details for debugging
                        try {
                            String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                            android.util.Log.e("AuthViewModel", "Verify OTP failed - Code: " + response.code() + ", Message: " + response.message() + ", Body: " + errorBody);
                            verifyOTPErrorLiveData.setValue("OTP verification failed: " + errorBody);
                        } catch (Exception e) {
                            android.util.Log.e("AuthViewModel", "Error reading error body", e);
                            verifyOTPErrorLiveData.setValue("OTP verification failed: " + response.message());
                        }
                    }
                }

                @Override
                public void onFailure(Call<VerifyOTPResponse> call, Throwable t) {
                    verifyOTPLoadingLiveData.setValue(false);
                    android.util.Log.e("AuthViewModel", "Verify OTP network error", t);
                    verifyOTPErrorLiveData.setValue("Error: " + t.getMessage());
                }
            });
        }

        public LiveData<VerifyOTPResponse> getVerifyOTPResponse() {
            return verifyOTPResponseLiveData;
        }

        public LiveData<String> getVerifyOTPError() {
            return verifyOTPErrorLiveData;
        }

        public LiveData<Boolean> getVerifyOTPLoading() {
            return verifyOTPLoadingLiveData;
        }

        // ========== RESEND OTP ==========
        public void resendOTP(String email) {
            resendOTPLoadingLiveData.setValue(true);
            ResendOTPRequest request = new ResendOTPRequest(email);

            apiService.resendOtp(request).enqueue(new Callback<ResendOTPResponse>() {
                @Override
                public void onResponse(Call<ResendOTPResponse> call, Response<ResendOTPResponse> response) {
                    resendOTPLoadingLiveData.setValue(false);
                    if (response.isSuccessful() && response.body() != null) {
                        resendOTPResponseLiveData.setValue(response.body());
                    } else {
                        resendOTPErrorLiveData.setValue("Resend OTP failed: " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<ResendOTPResponse> call, Throwable t) {
                    resendOTPLoadingLiveData.setValue(false);
                    resendOTPErrorLiveData.setValue("Error: " + t.getMessage());
                }
            });
        }

        public LiveData<ResendOTPResponse> getResendOTPResponse() {
            return resendOTPResponseLiveData;
        }

        public LiveData<String> getResendOTPError() {
            return resendOTPErrorLiveData;
        }

        public LiveData<Boolean> getResendOTPLoading() {
            return resendOTPLoadingLiveData;
        }

        // ========== FORGOT PASSWORD ==========
        public void forgotPassword(String email) {
            forgotPasswordLoadingLiveData.setValue(true);
            ForgotPasswordRequest request = new ForgotPasswordRequest(email);

            apiService.forgotPassword(request).enqueue(new Callback<ForgotPasswordResponse>() {
                @Override
                public void onResponse(Call<ForgotPasswordResponse> call, Response<ForgotPasswordResponse> response) {
                    forgotPasswordLoadingLiveData.setValue(false);
                    if (response.isSuccessful() && response.body() != null) {
                        forgotPasswordResponseLiveData.setValue("OTP sent to your email");
                    } else {
                        forgotPasswordErrorLiveData.setValue("Forgot password request failed: " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
                    forgotPasswordLoadingLiveData.setValue(false);
                    forgotPasswordErrorLiveData.setValue("Error: " + t.getMessage());
                }
            });
        }

        public LiveData<String> getForgotPasswordResponse() {
            return forgotPasswordResponseLiveData;
        }

        public LiveData<String> getForgotPasswordError() {
            return forgotPasswordErrorLiveData;
        }

        public LiveData<Boolean> getForgotPasswordLoading() {
            return forgotPasswordLoadingLiveData;
        }

        // ========== RESET PASSWORD ==========
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

        public LiveData<ResetPasswordResponse> getResetPasswordResponse() {
            return resetPasswordResponseLiveData;
        }

        public LiveData<String> getResetPasswordError() {
            return resetPasswordErrorLiveData;
        }

        public LiveData<Boolean> getResetPasswordLoading() {
            return resetPasswordLoadingLiveData;
        }
    }
}