package com.example.filmspace_mobile.data.model.movie;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Episode implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private int movieId;
    private int episodeNumber;
    private String title;
    
    @SerializedName("description")
    private String overview;
    
    private String thumbnailUrl;
    private int duration; // in minutes
    private String videoUrl;
    private boolean isPremium;
    
    @SerializedName("releaseDate")
    private String airDate;

    // Constructor rỗng
    public Episode() {}

    // Constructor đầy đủ
    public Episode(int id, int movieId, int episodeNumber, String title, String overview,
                   String thumbnailUrl, int duration, String videoUrl, String airDate, boolean isPremium) {
        this.id = id;
        this.movieId = movieId;
        this.episodeNumber = episodeNumber;
        this.title = title;
        this.overview = overview;
        this.thumbnailUrl = thumbnailUrl;
        this.duration = duration;
        this.videoUrl = videoUrl;
        this.airDate = airDate;
        this.isPremium = isPremium;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEpisodeNumber() {
        return episodeNumber;
    }

    public void setEpisodeNumber(int episodeNumber) {
        this.episodeNumber = episodeNumber;
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

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getAirDate() {
        return airDate;
    }

    public void setAirDate(String airDate) {
        this.airDate = airDate;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }
}