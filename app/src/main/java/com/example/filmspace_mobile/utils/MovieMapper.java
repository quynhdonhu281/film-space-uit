package com.example.filmspace_mobile.utils;

import com.example.filmspace_mobile.data.local.entity.MovieEntity;
import com.example.filmspace_mobile.data.model.movie.Movie;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapper for converting between Movie and MovieEntity
 */
public class MovieMapper {
    
    /**
     * Convert Movie model to MovieEntity for database storage
     */
    public static MovieEntity toEntity(Movie movie) {
        if (movie == null) {
            return null;
        }
        
        return new MovieEntity(
                movie.getId(),
                movie.getTitle(),
                movie.getOverview(),
                movie.getPosterUrl(),
                movie.getBackdropUrl(),
                movie.getRating(),
                movie.getReleaseDate(),
                movie.getGenres(),
                System.currentTimeMillis() // Set current time as cache timestamp
        );
    }
    
    /**
     * Convert MovieEntity from database to Movie model
     */
    public static Movie toModel(MovieEntity entity) {
        if (entity == null) {
            return null;
        }
        
        Movie movie = new Movie();
        movie.setId(entity.getId());
        movie.setTitle(entity.getTitle());
        movie.setOverview(entity.getOverview());
        movie.setPosterUrl(entity.getPosterUrl());
        movie.setBackdropUrl(entity.getBackdropUrl());
        movie.setRating(entity.getRating());
        movie.setReleaseDate(entity.getReleaseDate());
        movie.setGenres(entity.getGenres());
        
        return movie;
    }
    
    /**
     * Convert list of Movies to list of MovieEntities
     */
    public static List<MovieEntity> toEntityList(List<Movie> movies) {
        if (movies == null) {
            return null;
        }
        
        List<MovieEntity> entities = new ArrayList<>();
        for (Movie movie : movies) {
            entities.add(toEntity(movie));
        }
        return entities;
    }
    
    /**
     * Convert list of MovieEntities to list of Movies
     */
    public static List<Movie> toModelList(List<MovieEntity> entities) {
        if (entities == null) {
            return null;
        }
        
        List<Movie> movies = new ArrayList<>();
        for (MovieEntity entity : entities) {
            movies.add(toModel(entity));
        }
        return movies;
    }
}
