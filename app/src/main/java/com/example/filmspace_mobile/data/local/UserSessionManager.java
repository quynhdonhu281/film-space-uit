package com.example.filmspace_mobile.data.local;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSessionManager {
    private static final String PREF_NAME = "filmspace_mobileSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_AVATAR_URL = "avatarUrl";
    private static final String KEY_NAME = "name";
    private static final String KEY_TOKEN = "token";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public UserSessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    /**
     * Save user session after successful login/register
     * Always clears old user data before storing new user (only 1 user stored at a time)
     */
    public void saveUserSession(int userId, String username, String email, 
                                 String avatarUrl, String name, String token) {
        // Clear any existing user data first
        editor.clear();
        
        // Save new user session
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_AVATAR_URL, avatarUrl);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    /**
     * Check if user is logged in
     */
    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Get user ID
     */
    public int getUserId() {
        return prefs.getInt(KEY_USER_ID, -1);
    }

    /**
     * Get username
     */
    public String getUsername() {
        return prefs.getString(KEY_USERNAME, null);
    }

    /**
     * Get email
     */
    public String getEmail() {
        return prefs.getString(KEY_EMAIL, null);
    }

    /**
     * Get avatar URL
     */
    public String getAvatarUrl() {
        return prefs.getString(KEY_AVATAR_URL, null);
    }

    /**
     * Get name
     */
    public String getName() {
        return prefs.getString(KEY_NAME, null);
    }

    /**
     * Get auth token
     */
    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    /**
     * Clear user session (logout)
     */
    public void clearSession() {
        editor.clear();
        editor.apply();
    }

    /**
     * Update avatar URL
     */
    public void updateAvatarUrl(String avatarUrl) {
        editor.putString(KEY_AVATAR_URL, avatarUrl);
        editor.apply();
    }

    /**
     * Update user name
     */
    public void updateName(String name) {
        editor.putString(KEY_NAME, name);
        editor.apply();
    }
}
