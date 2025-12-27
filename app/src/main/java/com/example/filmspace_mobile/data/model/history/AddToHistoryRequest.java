package com.example.filmspace_mobile.data.model.history;

public class AddToHistoryRequest {
    private int movieId;

    public AddToHistoryRequest(int movieId) {
        this.movieId = movieId;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }
}
