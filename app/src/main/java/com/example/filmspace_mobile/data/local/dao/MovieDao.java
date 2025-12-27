package com.example.filmspace_mobile.data.local.dao;

import androidx.paging.PagingSource;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.filmspace_mobile.data.local.entity.MovieEntity;

import java.util.List;

/**
 * DAO for movie database operations
 */
@Dao
public interface MovieDao {
    
    /**
     * Insert or replace movies in database
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovies(List<MovieEntity> movies);
    
    /**
     * Insert or replace a single movie
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovie(MovieEntity movie);
    
    /**
     * Get all movies (for non-paged queries)
     */
    @Query("SELECT * FROM movies ORDER BY cachedAt DESC")
    List<MovieEntity> getAllMovies();
    
    /**
     * Get movies with Paging 3 support
     */
    @Query("SELECT * FROM movies ORDER BY cachedAt DESC")
    PagingSource<Integer, MovieEntity> getMoviesPaged();
    
    /**
     * Get movie by ID
     */
    @Query("SELECT * FROM movies WHERE id = :movieId LIMIT 1")
    MovieEntity getMovieById(int movieId);
    
    /**
     * Get top rated movies
     */
    @Query("SELECT * FROM movies ORDER BY rating DESC LIMIT :limit")
    List<MovieEntity> getTopRatedMovies(int limit);
    
    /**
     * Clear all movies (for cache invalidation)
     */
    @Query("DELETE FROM movies")
    void clearAllMovies();
    
    /**
     * Delete old cached movies (older than specified timestamp)
     */
    @Query("DELETE FROM movies WHERE cachedAt < :timestamp")
    void deleteOldMovies(long timestamp);
    
    /**
     * Get count of cached movies
     */
    @Query("SELECT COUNT(*) FROM movies")
    int getMovieCount();
}
