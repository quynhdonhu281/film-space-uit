package com.example.filmspace_mobile.ui.auth.SignInFragment;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.filmspace_mobile.data.api.ApiService;
import com.example.filmspace_mobile.data.api.RetrofitClient;
import com.example.filmspace_mobile.data.local.UserSessionManager;
import com.example.filmspace_mobile.data.model.auth.LoginRequest;
import com.example.filmspace_mobile.data.model.auth.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInViewModel extends AndroidViewModel {
    private final ApiService apiService;
    private final UserSessionManager sessionManager;

    private final MutableLiveData<LoginResponse> loginResponseLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> loginErrorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loginLoadingLiveData = new MutableLiveData<>();

    public SignInViewModel(@NonNull Application application) {
        super(application);
        apiService = RetrofitClient.getApiService();
        sessionManager = new UserSessionManager(application.getApplicationContext());
    }

    public LiveData<LoginResponse> getLoginResponse() { return loginResponseLiveData; }
    public LiveData<String> getLoginError() { return loginErrorLiveData; }
    public LiveData<Boolean> getLoginLoading() { return loginLoadingLiveData; }
    public UserSessionManager getSessionManager() { return sessionManager; }

    public void login(String email, String password) {
        loginLoadingLiveData.setValue(true);
        LoginRequest request = new LoginRequest(email, password);

        apiService.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                loginLoadingLiveData.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
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
}
