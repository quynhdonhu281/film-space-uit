package com.example.filmspace_mobile.utils;

import android.text.TextUtils;
import android.util.Patterns;
import com.google.android.material.textfield.TextInputEditText;
import java.util.regex.Pattern;

/**
 * Centralized validation helper to avoid duplicating validation logic across fragments.
 * Provides reusable validation methods for common input fields.
 */
public class ValidationHelper {
    
    // Password validation pattern: at least 8 chars, uppercase, lowercase, and special character
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).{8,}$"
    );
    
    // Fullname pattern: only English letters and spaces
    private static final Pattern FULLNAME_PATTERN = Pattern.compile("^[a-zA-Z ]+$");
    
    // Username pattern: no spaces allowed
    private static final Pattern USERNAME_NO_SPACE_PATTERN = Pattern.compile("^\\S+$");
    
    /**
     * Validates email field.
     * @param emailInput The email input field
     * @return true if valid, false otherwise
     */
    public static boolean validateEmail(TextInputEditText emailInput) {
        String email = emailInput.getText().toString().trim();
        
        if (TextUtils.isEmpty(email)) {
            emailInput.setError("Email is required");
            emailInput.requestFocus();
            return false;
        }
        
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Invalid email format");
            emailInput.requestFocus();
            return false;
        }
        
        emailInput.setError(null);
        return true;
    }
    
    /**
     * Validates password field with minimum requirements.
     * @param passwordInput The password input field
     * @return true if valid, false otherwise
     */
    public static boolean validatePassword(TextInputEditText passwordInput) {
        String password = passwordInput.getText().toString().trim();
        
        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("Password is required");
            passwordInput.requestFocus();
            return false;
        }
        
        if (password.length() < 8) {
            passwordInput.setError("Password must be at least 8 characters");
            passwordInput.requestFocus();
            return false;
        }
        
        passwordInput.setError(null);
        return true;
    }
    
    /**
     * Validates password field with strong requirements (uppercase, lowercase, special char).
     * @param passwordInput The password input field
     * @return true if valid, false otherwise
     */
    public static boolean validateStrongPassword(TextInputEditText passwordInput) {
        String password = passwordInput.getText().toString().trim();
        
        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("Password is required");
            passwordInput.requestFocus();
            return false;
        }
        
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            passwordInput.setError("Password must be 8+ chars with uppercase, lowercase, and special character");
            passwordInput.requestFocus();
            return false;
        }
        
        passwordInput.setError(null);
        return true;
    }
    
    /**
     * Validates that two password fields match.
     * @param passwordInput The password input field
     * @param confirmPasswordInput The confirm password input field
     * @return true if passwords match, false otherwise
     */
    public static boolean validatePasswordMatch(TextInputEditText passwordInput, 
                                                TextInputEditText confirmPasswordInput) {
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();
        
        if (!password.equals(confirmPassword)) {
            confirmPasswordInput.setError("Passwords do not match");
            confirmPasswordInput.requestFocus();
            return false;
        }
        
        confirmPasswordInput.setError(null);
        return true;
    }
    
    /**
     * Validates fullname field (only English letters and spaces).
     * @param fullnameInput The fullname input field
     * @return true if valid, false otherwise
     */
    public static boolean validateFullname(TextInputEditText fullnameInput) {
        String fullname = fullnameInput.getText().toString().trim();
        
        if (TextUtils.isEmpty(fullname)) {
            fullnameInput.setError("Fullname is required");
            fullnameInput.requestFocus();
            return false;
        }
        
        if (!FULLNAME_PATTERN.matcher(fullname).matches()) {
            fullnameInput.setError("Fullname must contain only English letters");
            fullnameInput.requestFocus();
            return false;
        }
        
        fullnameInput.setError(null);
        return true;
    }
    
    /**
     * Validates username field (no spaces, minimum 3 characters).
     * @param usernameInput The username input field
     * @return true if valid, false otherwise
     */
    public static boolean validateUsername(TextInputEditText usernameInput) {
        String username = usernameInput.getText().toString().trim();
        
        if (TextUtils.isEmpty(username)) {
            usernameInput.setError("Username is required");
            usernameInput.requestFocus();
            return false;
        }
        
        if (!USERNAME_NO_SPACE_PATTERN.matcher(username).matches()) {
            usernameInput.setError("Username cannot contain spaces");
            usernameInput.requestFocus();
            return false;
        }
        
        if (username.length() < 3) {
            usernameInput.setError("Username must be at least 3 characters");
            usernameInput.requestFocus();
            return false;
        }
        
        usernameInput.setError(null);
        return true;
    }
    
    /**
     * Validates that a required text field is not empty.
     * @param input The input field to validate
     * @param fieldName The name of the field for error message
     * @return true if not empty, false otherwise
     */
    public static boolean validateRequired(TextInputEditText input, String fieldName) {
        String value = input.getText().toString().trim();
        
        if (TextUtils.isEmpty(value)) {
            input.setError(fieldName + " is required");
            input.requestFocus();
            return false;
        }
        
        input.setError(null);
        return true;
    }
    
    /**
     * Clears error from input field.
     * @param input The input field
     */
    public static void clearError(TextInputEditText input) {
        if (input != null) {
            input.setError(null);
        }
    }
}
