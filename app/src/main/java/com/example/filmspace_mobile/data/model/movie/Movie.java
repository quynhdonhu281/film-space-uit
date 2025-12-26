package com.example.filmspace_mobile.data.model.movie;

import java.util.List;

public class Movie {
    private int id;
    private String title;
    private String posterUrl;
    private String backdropUrl;
    private String overview;
    private String releaseDate;
    private int seasonCount;
    private int episodeCount;
    private String genre;  // Giữ lại cho backward compatibility
    private List<Genre> genres;  // ĐỔI: từ List<String> thành List<Genre>
    private double rating;
    private List<Cast> castList;

    // Constructor rỗng
    public Movie() {}

    // Constructor đầy đủ
    public Movie(int id, String title, String posterUrl, String backdropUrl,
                 String overview, String releaseDate, int seasonCount,
                 int episodeCount, String genre, double rating) {
        this.id = id;
        this.title = title;
        this.posterUrl = posterUrl;
        this.backdropUrl = backdropUrl;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.seasonCount = seasonCount;
        this.episodeCount = episodeCount;
        this.genre = genre;
        this.rating = rating;
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

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int getSeasonCount() {
        return seasonCount;
    }

    public void setSeasonCount(int seasonCount) {
        this.seasonCount = seasonCount;
    }

    public int getEpisodeCount() {
        return episodeCount;
    }

    public void setEpisodeCount(int episodeCount) {
        this.episodeCount = episodeCount;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    // Getter/Setter cho genres (List<Genre>)
    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public List<Cast> getCastList() {
        return castList;
    }

    public void setCastList(List<Cast> castList) {
        this.castList = castList;
    }
}