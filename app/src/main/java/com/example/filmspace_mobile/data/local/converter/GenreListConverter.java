package com.example.filmspace_mobile.data.local.converter;

import androidx.room.TypeConverter;

import com.example.filmspace_mobile.data.model.movie.Genre;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * TypeConverter for converting Genre list to/from JSON string
 */
public class GenreListConverter {
    private static final Gson gson = new Gson();
    
    @TypeConverter
    public static String fromGenreList(List<Genre> genres) {
        if (genres == null) {
            return null;
        }
        return gson.toJson(genres);
    }
    
    @TypeConverter
    public static List<Genre> toGenreList(String genresString) {
        if (genresString == null) {
            return null;
        }
        Type listType = new TypeToken<List<Genre>>() {}.getType();
        return gson.fromJson(genresString, listType);
    }
}
