package com.example.filmspace_mobile.data.model.history;

import com.example.filmspace_mobile.data.model.movie.Movie;

import java.util.ArrayList;
import java.util.List;

public class WatchHistoryResponse {
    private List<WatchHistoryItem> data;
    private Pagination pagination;

    public List<WatchHistoryItem> getData() {
        return data;
    }

    public void setData(List<WatchHistoryItem> data) {
        this.data = data;
    }

    /**
     * Extract movies from watch history items
     * @return List of movies from history
     */
    public List<Movie> getMovies() {
        List<Movie> movies = new ArrayList<>();
        if (data != null) {
            for (WatchHistoryItem item : data) {
                if (item.getMovie() != null) {
                    movies.add(item.getMovie());
                }
            }
        }
        return movies;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public static class Pagination {
        private int currentPage;
        private int pageSize;
        private int totalItems;
        private int totalPages;

        public int getCurrentPage() {
            return currentPage;
        }

        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public int getTotalItems() {
            return totalItems;
        }

        public void setTotalItems(int totalItems) {
            this.totalItems = totalItems;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }
    }
}
