package com.example.filmspace_mobile.ui.auth.RegisterFragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.filmspace_mobile.data.model.auth.RegisterResponse;
import com.example.filmspace_mobile.data.repository.AuthRepository;
import com.example.filmspace_mobile.data.repository.RepositoryCallback;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class RegisterViewModel extends ViewModel {
    private final AuthRepository authRepository;

    private final MutableLiveData<RegisterResponse> registerResponseLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> registerErrorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> registerLoadingLiveData = new MutableLiveData<>();

    @Inject
    public RegisterViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public LiveData<RegisterResponse> getRegisterResponse() { return registerResponseLiveData; }
    public LiveData<String> getRegisterError() { return registerErrorLiveData; }
    public LiveData<Boolean> getRegisterLoading() { return registerLoadingLiveData; }

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
}
