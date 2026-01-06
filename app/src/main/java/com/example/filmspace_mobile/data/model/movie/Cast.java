package com.example.filmspace_mobile.data.model.movie;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Cast implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String name;
    private String character;
    
    @SerializedName("avatarUrl")
    private String profileUrl;

    // Constructor rỗng
    public Cast() {}

    // Constructor đầy đủ
    public Cast(int id, String name, String character, String profileUrl) {
        this.id = id;
        this.name = name;
        this.character = character;
        this.profileUrl = profileUrl;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public String getProfileUrl() {
        return profileUrl;
    }
    public String getFullAvatarUrl() {
        if (profileUrl == null || profileUrl.isEmpty()) return null;
        // Thay link này bằng link ngrok hiện tại của bạn hoặc dùng biến từ Constant/BuildConfig
        String baseUrl = "https://d58vokudzsdux.cloudfront.net/";
        return  profileUrl;
    }
    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }
}