package com.example.filmspace_mobile.ui.auth.CreateNewPasswordFragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.filmspace_mobile.data.model.auth.ResetPasswordResponse;
import com.example.filmspace_mobile.data.repository.AuthRepository;
import com.example.filmspace_mobile.data.repository.RepositoryCallback;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class CreateNewPasswordViewModel extends ViewModel {
    private final AuthRepository authRepository;

    private final MutableLiveData<String> forgotPasswordResponseLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> forgotPasswordErrorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> forgotPasswordLoadingLiveData = new MutableLiveData<>();

    private final MutableLiveData<ResetPasswordResponse> resetPasswordResponseLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> resetPasswordErrorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> resetPasswordLoadingLiveData = new MutableLiveData<>();

    @Inject
    public CreateNewPasswordViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public LiveData<String> getForgotPasswordResponse() { return forgotPasswordResponseLiveData; }
    public LiveData<String> getForgotPasswordError() { return forgotPasswordErrorLiveData; }
    public LiveData<Boolean> getForgotPasswordLoading() { return forgotPasswordLoadingLiveData; }

    public LiveData<ResetPasswordResponse> getResetPasswordResponse() { return resetPasswordResponseLiveData; }
    public LiveData<String> getResetPasswordError() { return resetPasswordErrorLiveData; }
    public LiveData<Boolean> getResetPasswordLoading() { return resetPasswordLoadingLiveData; }

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
