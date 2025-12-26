package com.example.filmspace_mobile.data.model.movie;

public class Episode {
    private int id;
    private int episodeNumber;
    private String title;
    private String overview;
    private String thumbnailUrl;
    private int duration; // in minutes
    private String airDate;

    // Constructor rỗng
    public Episode() {}

    // Constructor đầy đủ
    public Episode(int id, int episodeNumber, String title, String overview,
                   String thumbnailUrl, int duration, String airDate) {
        this.id = id;
        this.episodeNumber = episodeNumber;
        this.title = title;
        this.overview = overview;
        this.thumbnailUrl = thumbnailUrl;
        this.duration = duration;
        this.airDate = airDate;
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
}