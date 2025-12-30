package com.example.filmspace_mobile.data.model.movie;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class EpisodeListResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<Episode> data;

    @SerializedName("totalEpisodes")
    private int totalEpisodes;

    @SerializedName("currentPage")
    private int currentPage;

    @SerializedName("totalPages")
    private int totalPages;

    public EpisodeListResponse() {
    }

    public EpisodeListResponse(boolean success, String message, List<Episode> data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.totalEpisodes = data != null ? data.size() : 0;
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Episode> getData() {
        return data;
    }

    public void setData(List<Episode> data) {
        this.data = data;
        this.totalEpisodes = data != null ? data.size() : 0;
    }

    public int getTotalEpisodes() {
        return totalEpisodes;
    }

    public void setTotalEpisodes(int totalEpisodes) {
        this.totalEpisodes = totalEpisodes;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    // Helper method to get episodes list
    public List<Episode> getEpisodes() {
        return data;
    }
}