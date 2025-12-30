package com.example.filmspace_mobile.data.local.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.filmspace_mobile.data.local.converter.GenreListConverter;
import com.example.filmspace_mobile.data.model.movie.Genre;

import java.util.List;

/**
 * Room entity for caching movies locally
 */
@Entity(tableName = "movies")
@TypeConverters(GenreListConverter.class)
public class MovieEntity {
    @PrimaryKey
    private int id;
    
    private String title;
    private String overview;
    private String posterUrl;
    private String backdropUrl;
    private double rating;
    private String releaseDate;
    private long cachedAt; // Timestamp for cache invalidation
    
    // Store genres as JSON string
    private List<Genre> genres;
    
    // Constructors
    public MovieEntity() {
    }

    @Ignore
    public MovieEntity(int id, String title, String overview, String posterUrl, 
                      String backdropUrl, double rating, String releaseDate, 
                      List<Genre> genres, long cachedAt) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.posterUrl = posterUrl;
        this.backdropUrl = backdropUrl;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.genres = genres;
        this.cachedAt = cachedAt;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getBackdropUrl() {
        return backdropUrl;
    }

    public void setBackdropUrl(String backdropUrl) {
        this.backdropUrl = backdropUrl;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public long getCachedAt() {
        return cachedAt;
    }

    public void setCachedAt(long cachedAt) {
        this.cachedAt = cachedAt;
    }
}
