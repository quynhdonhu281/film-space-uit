package com.example.filmspace_mobile.data.model.auth;

public class VerifyOTPResponse {
    private int userId;
    private String username;
    private String email;
    private String avatarUrl;
    private String name;
    private String token;

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
}
