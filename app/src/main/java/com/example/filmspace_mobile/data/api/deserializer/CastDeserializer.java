package com.example.filmspace_mobile.data.api.deserializer;

import com.example.filmspace_mobile.data.api.CloudFrontUrlHelper;
import com.example.filmspace_mobile.data.model.movie.Cast;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class CastDeserializer implements JsonDeserializer<Cast> {
    
    @Override
    public Cast deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) 
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        Cast cast = new Cast();
        
        if (jsonObject.has("id")) {
            cast.setId(jsonObject.get("id").getAsInt());
        }
        if (jsonObject.has("name")) {
            cast.setName(jsonObject.get("name").getAsString());
        }
        if (jsonObject.has("character")) {
            cast.setCharacter(jsonObject.get("character").getAsString());
        }
        
        // Handle avatarUrl field with CloudFront prefix
        if (jsonObject.has("avatarUrl") && !jsonObject.get("avatarUrl").isJsonNull()) {
            String profileUrl = jsonObject.get("avatarUrl").getAsString();
            cast.setProfileUrl(CloudFrontUrlHelper.prependCloudFrontUrl(profileUrl));
        }
        
        return cast;
    }
}
