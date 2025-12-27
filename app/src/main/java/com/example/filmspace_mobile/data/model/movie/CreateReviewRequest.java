package com.example.filmspace_mobile.data.model.movie;

public class CreateReviewRequest {
    private int movieId;
    private int userId;
    private int rating;
    private String comment;

    public CreateReviewRequest(int movieId, int userId, int rating, String comment) {
        this.movieId = movieId;
        this.userId = userId;
        this.rating = rating;
        this.comment = comment;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
