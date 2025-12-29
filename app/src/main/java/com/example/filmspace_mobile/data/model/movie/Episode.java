package com.example.filmspace_mobile.data.model.movie;

import com.google.gson.annotations.SerializedName;

public class Episode {

    @SerializedName("id")
    private int id;

    @SerializedName("movieId")
    private int movieId;

    @SerializedName("episodeNumber")
    private int episodeNumber;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("duration")
    private int duration;

    @SerializedName("videoUrl")
    private String videoUrl;

    @SerializedName("releaseDate")
    private String releaseDate;

    @SerializedName("isPremium")
    private boolean isPremium;

    public Episode(int id, int episodeNumber, String title, String description,
                   String videoUrl, int duration, String releaseDate) {
        this.id = id;
        this.episodeNumber = episodeNumber;
        this.title = title;
        this.description = description;
        this.videoUrl = videoUrl;
        this.duration = duration;
        this.releaseDate = releaseDate;
        this.isPremium = false;
    }

    public Episode() {
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }

    public String getFormattedDuration() {
        int hours = duration / 60;
        int minutes = duration % 60;

        if (hours > 0) {
            return String.format("%dh %dm", hours, minutes);
        } else {
            return String.format("%dm", minutes);
        }
    }
    public String getOverview() {
        return description;
    }

}