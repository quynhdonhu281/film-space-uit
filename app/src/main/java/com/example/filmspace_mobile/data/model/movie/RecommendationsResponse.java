package com.example.filmspace_mobile.data.model.movie;

import java.util.List;

public class RecommendationsResponse {
    private List<Movie> data;

    public List<Movie> getData() {
        return data;
    }

    public void setData(List<Movie> data) {
        this.data = data;
    }
}
