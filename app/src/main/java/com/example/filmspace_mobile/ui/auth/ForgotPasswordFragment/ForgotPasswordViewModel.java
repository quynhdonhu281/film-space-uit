package com.example.filmspace_mobile.ui.auth.ForgotPasswordFragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.filmspace_mobile.data.repository.AuthRepository;
import com.example.filmspace_mobile.data.repository.RepositoryCallback;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ForgotPasswordViewModel extends ViewModel {
    private final AuthRepository authRepository;

    private final MutableLiveData<String> forgotPasswordResponseLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> forgotPasswordErrorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> forgotPasswordLoadingLiveData = new MutableLiveData<>();

    @Inject
    public ForgotPasswordViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public LiveData<String> getForgotPasswordResponse() { return forgotPasswordResponseLiveData; }
    public LiveData<String> getForgotPasswordError() { return forgotPasswordErrorLiveData; }
    public LiveData<Boolean> getForgotPasswordLoading() { return forgotPasswordLoadingLiveData; }

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
}
