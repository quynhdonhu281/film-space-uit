package com.example.filmspace_mobile.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.filmspace_mobile.data.local.UserSessionManager;
import com.example.filmspace_mobile.data.model.auth.LoginResponse;
import com.example.filmspace_mobile.data.model.auth.RegisterResponse;
import com.example.filmspace_mobile.data.model.auth.ResetPasswordResponse;
import com.example.filmspace_mobile.data.model.auth.VerifyOTPResponse;
import com.example.filmspace_mobile.data.repository.AuthRepository;
import com.example.filmspace_mobile.data.repository.RepositoryCallback;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AuthViewModel extends ViewModel {
    private static final String TAG = "AuthViewModel";

    private final AuthRepository authRepository;

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

    @Inject
    public AuthViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
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
        return authRepository.getSessionManager();
    }

    // Login
    public void login(String email, String password) {
        loginLoadingLiveData.setValue(true);
        authRepository.login(email, password, new RepositoryCallback<LoginResponse>() {
            @Override
            public void onSuccess(LoginResponse data) {
                loginLoadingLiveData.setValue(false);
                loginResponseLiveData.setValue(data);
            }

            @Override
            public void onError(String error) {
                loginLoadingLiveData.setValue(false);
                loginErrorLiveData.setValue(error);
            }
        });
    }

    // Register
    public void register(String email, String password, String username, String fullname) {
        registerLoadingLiveData.setValue(true);
        authRepository.register(email, password, username, fullname, new RepositoryCallback<RegisterResponse>() {
            @Override
            public void onSuccess(RegisterResponse data) {
                registerLoadingLiveData.setValue(false);
                registerResponseLiveData.setValue(data);
            }

            @Override
            public void onError(String error) {
                registerLoadingLiveData.setValue(false);
                registerErrorLiveData.setValue(error);
            }
        });
    }

    // Verify OTP
    public void verifyOTP(String email, String otp) {
        verifyOTPLoadingLiveData.setValue(true);
        authRepository.verifyOTP(email, otp, new RepositoryCallback<VerifyOTPResponse>() {
            @Override
            public void onSuccess(VerifyOTPResponse data) {
                verifyOTPLoadingLiveData.setValue(false);
                verifyOTPResponseLiveData.setValue(data);
            }

            @Override
            public void onError(String error) {
                verifyOTPLoadingLiveData.setValue(false);
                verifyOTPErrorLiveData.setValue(error);
            }
        });
    }

    // Resend OTP
    public void resendOTP(String email) {
        resendOTPLoadingLiveData.setValue(true);
        authRepository.resendOTP(email, new RepositoryCallback<String>() {
            @Override
            public void onSuccess(String data) {
                resendOTPLoadingLiveData.setValue(false);
                resendOTPResponseLiveData.setValue(data);
            }

            @Override
            public void onError(String error) {
                resendOTPLoadingLiveData.setValue(false);
                resendOTPErrorLiveData.setValue(error);
            }
        });
    }

    // Forgot Password
    public void forgotPassword(String email) {
        forgotPasswordLoadingLiveData.setValue(true);
        authRepository.forgotPassword(email, new RepositoryCallback<String>() {
            @Override
            public void onSuccess(String data) {
                forgotPasswordLoadingLiveData.setValue(false);
                forgotPasswordResponseLiveData.setValue(data);
            }

            @Override
            public void onError(String error) {
                forgotPasswordLoadingLiveData.setValue(false);
                forgotPasswordErrorLiveData.setValue(error);
            }
        });
    }

    // Reset Password
    public void resetPassword(String email, String otp, String newPassword) {
        resetPasswordLoadingLiveData.setValue(true);
        authRepository.resetPassword(email, otp, newPassword, new RepositoryCallback<ResetPasswordResponse>() {
            @Override
            public void onSuccess(ResetPasswordResponse data) {
                resetPasswordLoadingLiveData.setValue(false);
                resetPasswordResponseLiveData.setValue(data);
            }

            @Override
            public void onError(String error) {
                resetPasswordLoadingLiveData.setValue(false);
                resetPasswordErrorLiveData.setValue(error);
            }
        });
    }
}
