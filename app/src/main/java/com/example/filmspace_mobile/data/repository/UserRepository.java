package com.example.filmspace_mobile.data.repository;

import com.example.filmspace_mobile.data.api.ApiService;
import com.example.filmspace_mobile.data.model.auth.UpdateUserRequest;
import com.example.filmspace_mobile.data.model.auth.UserResponse;

import java.io.File;

import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {
    private final ApiService apiService;

    @Inject
    public UserRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    /**
     * Get user by ID
     */
    public void getUserById(int userId, RepositoryCallback<UserResponse> callback) {
        apiService.getUserById(userId).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to load user data");
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    /**
     * Update user profile with avatar
     */
    public void updateUserWithAvatar(int userId, String username, String name, String email, 
                                     File avatarFile, RepositoryCallback<UserResponse> callback) {
        RequestBody usernameBody = RequestBody.create(MediaType.parse("text/plain"), username);
        RequestBody nameBody = RequestBody.create(MediaType.parse("text/plain"), name);
        RequestBody emailBody = RequestBody.create(MediaType.parse("text/plain"), email);
        
        RequestBody avatarBody = RequestBody.create(MediaType.parse("image/*"), avatarFile);
        MultipartBody.Part avatarPart = MultipartBody.Part.createFormData("avatar", avatarFile.getName(), avatarBody);

        apiService.updateUser(userId, usernameBody, nameBody, emailBody, avatarPart).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to update profile");
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    /**
     * Update user profile without avatar
     */
    public void updateUserWithoutAvatar(int userId, String username, String name, String email,
                                        RepositoryCallback<UserResponse> callback) {
        UpdateUserRequest request = new UpdateUserRequest(username, name, email);
        
        apiService.updateUserWithoutAvatar(userId, request).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to update profile");
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
}
