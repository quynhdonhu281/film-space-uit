package com.example.filmspace_mobile.data.api.deserializer;

import android.util.Log;

import com.example.filmspace_mobile.data.api.CloudFrontUrlHelper;
import com.example.filmspace_mobile.data.model.auth.LoginResponse;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class LoginResponseDeserializer implements JsonDeserializer<LoginResponse> {
    
    private static final String TAG = "LoginResponseDeserializer";
    
    @Override
    public LoginResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) 
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        
        // Create LoginResponse using reflection to set private fields
        LoginResponse response = new LoginResponse();
        
        try {
            // Get all fields and make them accessible
            java.lang.reflect.Field userIdField = LoginResponse.class.getDeclaredField("userId");
            userIdField.setAccessible(true);
            if (jsonObject.has("userId")) {
                userIdField.setInt(response, jsonObject.get("userId").getAsInt());
            }
            
            java.lang.reflect.Field usernameField = LoginResponse.class.getDeclaredField("username");
            usernameField.setAccessible(true);
            if (jsonObject.has("username")) {
                usernameField.set(response, jsonObject.get("username").getAsString());
            }
            
            java.lang.reflect.Field emailField = LoginResponse.class.getDeclaredField("email");
            emailField.setAccessible(true);
            if (jsonObject.has("email")) {
                emailField.set(response, jsonObject.get("email").getAsString());
            }
            
            java.lang.reflect.Field nameField = LoginResponse.class.getDeclaredField("name");
            nameField.setAccessible(true);
            if (jsonObject.has("name") && !jsonObject.get("name").isJsonNull()) {
                nameField.set(response, jsonObject.get("name").getAsString());
            }
            
            java.lang.reflect.Field tokenField = LoginResponse.class.getDeclaredField("token");
            tokenField.setAccessible(true);
            if (jsonObject.has("token")) {
                tokenField.set(response, jsonObject.get("token").getAsString());
            }
            
            // Handle avatarUrl with CloudFront prefix
            java.lang.reflect.Field avatarUrlField = LoginResponse.class.getDeclaredField("avatarUrl");
            avatarUrlField.setAccessible(true);
            if (jsonObject.has("avatarUrl") && !jsonObject.get("avatarUrl").isJsonNull()) {
                String avatarUrl = jsonObject.get("avatarUrl").getAsString();
                avatarUrlField.set(response, CloudFrontUrlHelper.prependCloudFrontUrl(avatarUrl));
            }
            
            java.lang.reflect.Field isPremiumField = LoginResponse.class.getDeclaredField("isPremium");
            isPremiumField.setAccessible(true);
            if (jsonObject.has("isPremium")) {
                isPremiumField.set(response, jsonObject.get("isPremium").getAsBoolean());
            }
            
            Log.d(TAG, "LoginResponse deserialized successfully: userId=" + response.getUserId() + ", isPremium=" + response.isPremium());
            
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Log.e(TAG, "Error deserializing LoginResponse", e);
            throw new JsonParseException("Error deserializing LoginResponse", e);
        }
        
        return response;
    }
}
