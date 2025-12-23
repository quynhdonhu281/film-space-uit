package com.example.filmspace_mobile.ui.auth.OTPVerificationFragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.filmspace_mobile.data.local.UserSessionManager;
import com.example.filmspace_mobile.data.model.auth.VerifyOTPResponse;
import com.example.filmspace_mobile.data.repository.AuthRepository;
import com.example.filmspace_mobile.data.repository.RepositoryCallback;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class OTPVerificationViewModel extends ViewModel {
    private static final String TAG = "OTPVerificationViewModel";
    private final AuthRepository authRepository;
    private final UserSessionManager sessionManager;

    private final MutableLiveData<VerifyOTPResponse> verifyOTPResponseLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> verifyOTPErrorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> verifyOTPLoadingLiveData = new MutableLiveData<>();

    private final MutableLiveData<String> resendOTPResponseLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> resendOTPErrorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> resendOTPLoadingLiveData = new MutableLiveData<>();

    @Inject
    public OTPVerificationViewModel(AuthRepository authRepository, UserSessionManager sessionManager) {
        this.authRepository = authRepository;
        this.sessionManager = sessionManager;
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
}
