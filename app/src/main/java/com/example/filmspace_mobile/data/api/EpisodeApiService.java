package com.example.filmspace_mobile.data.api;

import com.example.filmspace_mobile.data.model.movie.Episode;
import com.example.filmspace_mobile.data.model.movie.EpisodeListResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface EpisodeApiService {

    @GET("movies/{movieId}/Episodes")
    Call<List<Episode>> getMovieEpisodes(@Path("movieId") int movieId);

    @GET("movies/{movieId}/Episodes")
    Call<EpisodeListResponse> getMovieEpisodesWrapped(@Path("movieId") int movieId);

    @GET("movies/{movieId}/Episodes/{episodeId}")
    Call<Episode> getEpisode(
            @Path("movieId") int movieId,
            @Path("episodeId") int episodeId
    );
}