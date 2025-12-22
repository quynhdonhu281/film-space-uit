package com.example.filmspace_mobile.data.model.auth;

public class RegisterRequest {
    private String email;
    private String password;
    private String username;
    private String fullname;

    public RegisterRequest(String email, String password, String username, String fullname) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
}
