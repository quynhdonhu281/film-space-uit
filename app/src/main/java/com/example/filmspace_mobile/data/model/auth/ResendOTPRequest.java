package com.example.filmspace_mobile.data.model.auth;

public class ResendOTPRequest {
    private String email;

    public ResendOTPRequest(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
