package com.example.filmspace_mobile.data.repository;

/**
 * Generic callback interface for repository operations
 * @param <T> The type of data returned on success
 */
public interface RepositoryCallback<T> {
    /**
     * Called when the operation succeeds
     * @param data The result data
     */
    void onSuccess(T data);

    /**
     * Called when the operation fails
     * @param error User-friendly error message
     */
    void onError(String error);
}
