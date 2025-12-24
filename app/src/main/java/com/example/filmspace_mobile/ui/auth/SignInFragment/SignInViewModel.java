package com.example.filmspace_mobile.ui.auth.SignInFragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.filmspace_mobile.data.local.UserSessionManager;
import com.example.filmspace_mobile.data.model.auth.LoginResponse;
import com.example.filmspace_mobile.data.repository.AuthRepository;
import com.example.filmspace_mobile.data.repository.RepositoryCallback;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class SignInViewModel extends ViewModel {
    private final AuthRepository authRepository;
    private final UserSessionManager sessionManager;

    private final MutableLiveData<LoginResponse> loginResponseLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> loginErrorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loginLoadingLiveData = new MutableLiveData<>();

    @Inject
    public SignInViewModel(AuthRepository authRepository, UserSessionManager sessionManager) {
        this.authRepository = authRepository;
        this.sessionManager = sessionManager;
    }

    public LiveData<LoginResponse> getLoginResponse() { return loginResponseLiveData; }
    public LiveData<String> getLoginError() { return loginErrorLiveData; }
    public LiveData<Boolean> getLoginLoading() { return loginLoadingLiveData; }
    public UserSessionManager getSessionManager() { return sessionManager; }

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
}
