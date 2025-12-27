package com.example.filmspace_mobile.data.model.history;

import com.example.filmspace_mobile.data.model.movie.Movie;

public class WatchHistoryItem {
    private int id;
    private String watchedAt;
    private Movie movie;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWatchedAt() {
        return watchedAt;
    }

    public void setWatchedAt(String watchedAt) {
        this.watchedAt = watchedAt;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }
}
