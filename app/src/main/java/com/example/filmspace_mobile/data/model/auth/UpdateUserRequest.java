package com.example.filmspace_mobile.data.model.auth;

public class UpdateUserRequest {
    private String name;

    public UpdateUserRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
