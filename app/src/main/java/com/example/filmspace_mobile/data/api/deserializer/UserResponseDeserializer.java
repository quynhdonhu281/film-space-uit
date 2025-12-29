package com.example.filmspace_mobile.data.api.deserializer;

import com.example.filmspace_mobile.data.api.CloudFrontUrlHelper;
import com.example.filmspace_mobile.data.model.auth.UserResponse;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class UserResponseDeserializer implements JsonDeserializer<UserResponse> {
    
    @Override
    public UserResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) 
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        
        // Create UserResponse using reflection to set private fields
        UserResponse response = new UserResponse();
        
        try {
            // Get all fields and make them accessible
            java.lang.reflect.Field idField = UserResponse.class.getDeclaredField("id");
            idField.setAccessible(true);
            if (jsonObject.has("id")) {
                idField.setInt(response, jsonObject.get("id").getAsInt());
            }
            
            java.lang.reflect.Field usernameField = UserResponse.class.getDeclaredField("username");
            usernameField.setAccessible(true);
            if (jsonObject.has("username")) {
                usernameField.set(response, jsonObject.get("username").getAsString());
            }
            
            java.lang.reflect.Field nameField = UserResponse.class.getDeclaredField("name");
            nameField.setAccessible(true);
            if (jsonObject.has("name")) {
                nameField.set(response, jsonObject.get("name").getAsString());
            }
            
            java.lang.reflect.Field emailField = UserResponse.class.getDeclaredField("email");
            emailField.setAccessible(true);
            if (jsonObject.has("email")) {
                emailField.set(response, jsonObject.get("email").getAsString());
            }
            
            java.lang.reflect.Field roleField = UserResponse.class.getDeclaredField("role");
            roleField.setAccessible(true);
            if (jsonObject.has("role")) {
                roleField.set(response, jsonObject.get("role").getAsString());
            }
            
            java.lang.reflect.Field createdAtField = UserResponse.class.getDeclaredField("createdAt");
            createdAtField.setAccessible(true);
            if (jsonObject.has("createdAt")) {
                createdAtField.set(response, jsonObject.get("createdAt").getAsString());
            }
            
            java.lang.reflect.Field reviewCountField = UserResponse.class.getDeclaredField("reviewCount");
            reviewCountField.setAccessible(true);
            if (jsonObject.has("reviewCount")) {
                reviewCountField.setInt(response, jsonObject.get("reviewCount").getAsInt());
            }
            
            java.lang.reflect.Field watchlistCountField = UserResponse.class.getDeclaredField("watchlistCount");
            watchlistCountField.setAccessible(true);
            if (jsonObject.has("watchlistCount")) {
                watchlistCountField.setInt(response, jsonObject.get("watchlistCount").getAsInt());
            }
            
            // Handle avatarUrl with CloudFront prefix
            java.lang.reflect.Field avatarUrlField = UserResponse.class.getDeclaredField("avatarUrl");
            avatarUrlField.setAccessible(true);
            if (jsonObject.has("avatarUrl") && !jsonObject.get("avatarUrl").isJsonNull()) {
                String avatarUrl = jsonObject.get("avatarUrl").getAsString();
                avatarUrlField.set(response, CloudFrontUrlHelper.prependCloudFrontUrl(avatarUrl));
            }
            
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new JsonParseException("Error deserializing UserResponse", e);
        }
        
        return response;
    }
}
