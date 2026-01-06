package com.example.filmspace_mobile.data.api;

import com.example.filmspace_mobile.utils.ApiConfig;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.example.filmspace_mobile.data.api.deserializer.EpisodeDeserializer;
import com.example.filmspace_mobile.data.api.deserializer.MovieDeserializer;
import com.example.filmspace_mobile.data.api.deserializer.CastDeserializer;
import com.example.filmspace_mobile.data.model.movie.Movie;
import com.example.filmspace_mobile.data.model.movie.Episode;
import com.example.filmspace_mobile.data.model.movie.Cast;


import java.util.concurrent.TimeUnit;

public class RetrofitClient {

    private static Retrofit retrofit = null;
    private static final Object LOCK = new Object();

    public static Retrofit getInstance() {
        if (retrofit == null) {
            synchronized (LOCK) {
                if (retrofit == null) {
                    retrofit = createRetrofit();
                }
            }
        }
        return retrofit;
    }
    private static Gson createGsonWithDeserializers() {
        return new GsonBuilder()
                .setLenient()
                .registerTypeAdapter(Episode.class, new EpisodeDeserializer())
                .registerTypeAdapter(Movie.class, new MovieDeserializer())
                .registerTypeAdapter(Cast.class, new CastDeserializer())
                .create();
    }

    private static Retrofit createRetrofit() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(ApiConfig.CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(ApiConfig.READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(ApiConfig.WRITE_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(chain -> {
                    okhttp3.Request original = chain.request();
                    okhttp3.Request request = original.newBuilder()
                            .header("Content-Type", "application/json")
                            .header("Accept", "application/json")
                            .method(original.method(), original.body())
                            .build();
                    return chain.proceed(request);
                })
                .build();

        return new Retrofit.Builder()
                .baseUrl(ApiConfig.API_BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(createGsonWithDeserializers()))
                .build();
    }

    // Get Episode API Service
    public static EpisodeApiService getEpisodeApiService() {
        return getInstance().create(EpisodeApiService.class);
    }

    // Get Payment API Service (existing)
    public static PaymentApiService getPaymentApiService() {
        return getInstance().create(PaymentApiService.class);
    }

    public static void resetInstance() {
        synchronized (LOCK) {
            retrofit = null;
        }
    }
}