package com.example.filmspace_mobile.data.api.deserializer;

import com.example.filmspace_mobile.data.api.CloudFrontUrlHelper;
import com.example.filmspace_mobile.data.model.movie.Movie;
import com.example.filmspace_mobile.data.model.movie.Cast;
import com.example.filmspace_mobile.data.model.movie.Episode;
import com.example.filmspace_mobile.data.model.movie.Genre;
import com.example.filmspace_mobile.data.model.movie.Review;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class MovieDeserializer implements JsonDeserializer<Movie> {
    
    @Override
    public Movie deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) 
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        Movie movie = new Movie();
        
        // Deserialize simple fields
        if (jsonObject.has("id")) {
            movie.setId(jsonObject.get("id").getAsInt());
        }
        if (jsonObject.has("title")) {
            movie.setTitle(jsonObject.get("title").getAsString());
        }
        
        // Deserialize URL fields with CloudFront prefix
        if (jsonObject.has("posterUrl") && !jsonObject.get("posterUrl").isJsonNull()) {
            String posterUrl = jsonObject.get("posterUrl").getAsString();
            movie.setPosterUrl(CloudFrontUrlHelper.prependCloudFrontUrl(posterUrl));
        }
        // if (jsonObject.has("backdropUrl") && !jsonObject.get("backdropUrl").isJsonNull()) {
        //     String backdropUrl = jsonObject.get("backdropUrl").getAsString();
        //     movie.setBackdropUrl(CloudFrontUrlHelper.prependCloudFrontUrl(backdropUrl));
        // }
        
        // Deserialize other fields
        if (jsonObject.has("description")) {
            if (!jsonObject.get("description").isJsonNull()) {
                movie.setOverview(jsonObject.get("description").getAsString());
            }
        }
        if (jsonObject.has("director")) {
            if (!jsonObject.get("director").isJsonNull()) {
                movie.setDirector(jsonObject.get("director").getAsString());
            }
        }
        if (jsonObject.has("releaseYear")) {
            movie.setReleaseDate(jsonObject.get("releaseYear").getAsInt());
        }
        // if (jsonObject.has("seasonCount")) {
        //     movie.setSeasonCount(jsonObject.get("seasonCount").getAsInt());
        // }
        // if (jsonObject.has("episodeCount")) {
        //     movie.setEpisodeCount(jsonObject.get("episodeCount").getAsInt());
        // }
        if (jsonObject.has("reviewCount")) {
            movie.setReviewCount(jsonObject.get("reviewCount").getAsInt());
        }
        // if (jsonObject.has("genre")) {
        //     movie.setGenre(jsonObject.get("genre").getAsString());
        // }
        if (jsonObject.has("rating")) {
            movie.setRating(jsonObject.get("rating").getAsDouble());
        }
        
        // Deserialize nested objects
        if (jsonObject.has("genres")) {
            Type genreListType = new TypeToken<List<Genre>>(){}.getType();
            List<Genre> genres = context.deserialize(jsonObject.get("genres"), genreListType);
            movie.setGenres(genres);
        }
        if (jsonObject.has("casts")) {
            Type castListType = new TypeToken<List<Cast>>(){}.getType();
            List<Cast> casts = context.deserialize(jsonObject.get("casts"), castListType);
            movie.setCastList(casts);
        }
        if (jsonObject.has("episodes")) {
            Type episodeListType = new TypeToken<List<Episode>>(){}.getType();
            List<Episode> episodes = context.deserialize(jsonObject.get("episodes"), episodeListType);
            movie.setEpisodes(episodes);
        }
        if (jsonObject.has("reviews")) {
            Type reviewListType = new TypeToken<List<Review>>(){}.getType();
            List<Review> reviews = context.deserialize(jsonObject.get("reviews"), reviewListType);
            movie.setReviews(reviews);
        }
        
        return movie;
    }
}
