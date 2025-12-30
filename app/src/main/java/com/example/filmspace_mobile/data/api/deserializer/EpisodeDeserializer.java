package com.example.filmspace_mobile.data.api.deserializer;

import com.example.filmspace_mobile.data.api.CloudFrontUrlHelper;
import com.example.filmspace_mobile.data.model.movie.Episode;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class EpisodeDeserializer implements JsonDeserializer<Episode> {
    
    @Override
    public Episode deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) 
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        Episode episode = new Episode();
        
        if (jsonObject.has("id")) {
            episode.setId(jsonObject.get("id").getAsInt());
        }
        if (jsonObject.has("movieId")) {
            episode.setMovieId(jsonObject.get("movieId").getAsInt());
        }
        if (jsonObject.has("episodeNumber")) {
            episode.setEpisodeNumber(jsonObject.get("episodeNumber").getAsInt());
        }
        if (jsonObject.has("title")) {
            episode.setTitle(jsonObject.get("title").getAsString());
        }
        if (jsonObject.has("description")) {
            episode.setOverview(jsonObject.get("description").getAsString());
        }
        if (jsonObject.has("duration")) {
            episode.setDuration(jsonObject.get("duration").getAsInt());
        }
        if (jsonObject.has("releaseDate")) {
            episode.setAirDate(jsonObject.get("releaseDate").getAsString());
        }
        
        // Handle URL fields with CloudFront prefix
        if (jsonObject.has("thumbnailUrl") && !jsonObject.get("thumbnailUrl").isJsonNull()) {
            String thumbnailUrl = jsonObject.get("thumbnailUrl").getAsString();
            episode.setThumbnailUrl(CloudFrontUrlHelper.prependCloudFrontUrl(thumbnailUrl));
        }
        if (jsonObject.has("videoUrl") && !jsonObject.get("videoUrl").isJsonNull()) {
            String videoUrl = jsonObject.get("videoUrl").getAsString();
            episode.setVideoUrl(CloudFrontUrlHelper.prependCloudFrontUrl(videoUrl));
        }
        
        return episode;
    }
}
