package com.example.filmspace_mobile.data.model.auth;

public class UserResponse {
    private int id;
    private String username;
    private String name;
    private String email;
    private String avatarUrl;
    private String role;
    private String createdAt;
    private int reviewCount;
    // private int watchlistCount;
    private boolean isPremium;

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getRole() {
        return role;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    // public int getWatchlistCount() {
    //     return watchlistCount;
    // }
    
    public boolean isPremium() {
        return isPremium;
    }
}
