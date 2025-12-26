package com.example.filmspace_mobile.data.model.movie;

public class Cast {
    private int id;
    private String name;
    private String character;
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

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }
}