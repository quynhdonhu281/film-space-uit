package com.example.filmspace_mobile.data.model.movie; // Hoặc package tương ứng của bạn

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class MovieViewResponse implements Serializable {

    @SerializedName("movieId")
    private int movieId;

    @SerializedName("movieTitle")
    private String movieTitle;

    @SerializedName("totalViews")
    private long totalViews; // Đây là cái bạn cần lấy

    // Bạn có thể map thêm recentViews nếu cần, nhưng hiện tại chỉ cần totalViews
    // private List<RecentView> recentViews;

    public long getTotalViews() {
        return totalViews;
    }
}