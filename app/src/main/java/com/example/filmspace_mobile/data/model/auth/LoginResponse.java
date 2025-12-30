package com.example.filmspace_mobile.data.model.auth;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    private int userId;
    private String username;
    private String email;
    private String avatarUrl;
    private String name;
    private String token;
    @SerializedName("isPremium")
    private boolean isPremium;

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getName() {
        return name;
    }

    public String getToken() {
        return token;
    }

    public boolean isPremium() {
        return isPremium; // Default is false if not provided by backend
    }
}
