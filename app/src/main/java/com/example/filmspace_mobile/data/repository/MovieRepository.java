package com.example.filmspace_mobile.data.repository;

import com.example.filmspace_mobile.BuildConfig;
import com.example.filmspace_mobile.data.api.ApiService;
import com.example.filmspace_mobile.data.model.movie.Cast;
import com.example.filmspace_mobile.data.model.movie.Movie;
import com.example.filmspace_mobile.data.model.movie.MovieViewResponse; // [MỚI] Import Model này
import com.example.filmspace_mobile.data.model.movie.RecommendationsResponse;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class MovieRepository {
    private static final String TAG = "MovieRepository";

    private final ApiService apiService;

    @Inject
    public MovieRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    /**
     * Fetch all movies from the API
     */
    public void getAllMovies(RepositoryCallback<List<Movie>> callback) {
        apiService.getAllMovies().enqueue(new Callback<List<Movie>>() {
            @Override
            public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = getHttpErrorMessage(response.code());
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<List<Movie>> call, Throwable t) {
                String errorMessage = getNetworkErrorMessage(t);
                callback.onError(errorMessage);
            }
        });
    }

    /**
     * Fetch movies with pagination
     */
    public void getMoviesPaginated(int page, int pageSize, RepositoryCallback<List<Movie>> callback) {
        apiService.getMoviesPaginated(page, pageSize).enqueue(new Callback<List<Movie>>() {
            @Override
            public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = getHttpErrorMessage(response.code());
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<List<Movie>> call, Throwable t) {
                String errorMessage = getNetworkErrorMessage(t);
                callback.onError(errorMessage);
            }
        });
    }

    /**
     * Fetch top rated movies
     */
    public void getTopRatedMovies(int movieId, int limit, RepositoryCallback<List<Movie>> callback) {
        apiService.getTopRatedMovies(movieId, limit).enqueue(new Callback<List<Movie>>() {
            @Override
            public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    getAllMoviesAndSortByRating(limit, callback);
                }
            }

            @Override
            public void onFailure(Call<List<Movie>> call, Throwable t) {
                getAllMoviesAndSortByRating(limit, callback);
            }
        });
    }

    private void getAllMoviesAndSortByRating(int limit, RepositoryCallback<List<Movie>> callback) {
        apiService.getAllMoviesForTopRated().enqueue(new Callback<List<Movie>>() {
            @Override
            public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Movie> movies = response.body();
                    movies.sort((m1, m2) -> Double.compare(m2.getRating(), m1.getRating()));
                    List<Movie> topRated = movies.size() > limit ?
                            movies.subList(0, limit) : movies;
                    callback.onSuccess(topRated);
                } else {
                    String errorMessage = getHttpErrorMessage(response.code());
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<List<Movie>> call, Throwable t) {
                String errorMessage = getNetworkErrorMessage(t);
                callback.onError(errorMessage);
            }
        });
    }

    /**
     * Fetch movies by genre ID
     */
    public void getMoviesByGenre(int genreId, RepositoryCallback<List<Movie>> callback) {
        apiService.getMoviesByGenre(genreId).enqueue(new Callback<List<Movie>>() {
            @Override
            public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = getHttpErrorMessage(response.code());
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<List<Movie>> call, Throwable t) {
                String errorMessage = getNetworkErrorMessage(t);
                callback.onError(errorMessage);
            }
        });
    }

    /**
     * Fetch movie by ID
     */
    public void getMovieById(int movieId, RepositoryCallback<Movie> callback) {
        apiService.getMovieById(movieId).enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = getHttpErrorMessage(response.code());
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                android.util.Log.e(TAG, "getMovieById request failed", t);
                String errorMessage = getNetworkErrorMessage(t);
                callback.onError(errorMessage);
            }
        });
    }

    /**
     * Fetch recommended movies
     */
    public void getRecommendedMovies(int limit, RepositoryCallback<List<Movie>> callback) {
        apiService.getRecommendedMovies(limit).enqueue(new Callback<RecommendationsResponse>() {
            @Override
            public void onResponse(Call<RecommendationsResponse> call, Response<RecommendationsResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<Movie> movies = response.body().getData();
                    if (BuildConfig.DEBUG && movies != null && !movies.isEmpty()) {
                        android.util.Log.d(TAG, "Recommended movies count: " + movies.size());
                    }
                    callback.onSuccess(movies);
                } else {
                    String errorMessage = getHttpErrorMessage(response.code());
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<RecommendationsResponse> call, Throwable t) {
                String errorMessage = getNetworkErrorMessage(t);
                callback.onError(errorMessage);
            }
        });
    }
    public void getAllCasts(RepositoryCallback<List<Cast>> callback) {
        apiService.getAllCasts().enqueue(new Callback<List<Cast>>() {
            @Override
            public void onResponse(Call<List<Cast>> call, Response<List<Cast>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to fetch casts");
                }
            }

            @Override
            public void onFailure(Call<List<Cast>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
    /**
     * [MỚI] Fetch movie views count
     * Hàm này nhận về Object MovieViewResponse nhưng trả về Long (totalViews) cho UI
     */

    public void getMovieViews(int movieId, RepositoryCallback<Long> callback) {
        apiService.getMovieViews(movieId).enqueue(new Callback<MovieViewResponse>() {
            @Override
            public void onResponse(Call<MovieViewResponse> call, Response<MovieViewResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Lấy trường totalViews từ Object JSON trả về
                    long totalViews = response.body().getTotalViews();
                    callback.onSuccess(totalViews);
                } else {
                    // Nếu lỗi hoặc không có data, trả về 0 để UI hiển thị "0 views" thay vì lỗi
                    callback.onSuccess(0L);
                }
            }

            @Override
            public void onFailure(Call<MovieViewResponse> call, Throwable t) {
                // Network error, log lại và trả về message lỗi
                // Tuy nhiên với view count, có thể bạn muốn trả về 0L trong onSuccess thay vì onError
                // để không làm gián đoạn trải nghiệm người dùng.
                // Nhưng ở đây mình giữ logic chuẩn là báo lỗi.
                callback.onError(getNetworkErrorMessage(t));
            }
        });
    }

    // --- Helper Methods ---

    private String getHttpErrorMessage(int code) {
        switch (code) {
            case 400: return "Invalid request. Please check your input.";
            case 401: return "Unauthorized. Please login again.";
            case 403: return "Access forbidden.";
            case 404: return "Movies not found.";
            case 500: case 502: case 503: return "Server error. Please try again later.";
            default: return "Failed to load movies. Please try again.";
        }
    }

    private String getNetworkErrorMessage(Throwable t) {
        if (t instanceof UnknownHostException) {
            return "No internet connection. Please check your network.";
        } else if (t instanceof SocketTimeoutException) {
            return "Request timed out. Please try again.";
        } else {
            return "Something went wrong. Please try again.";
        }
    }
}