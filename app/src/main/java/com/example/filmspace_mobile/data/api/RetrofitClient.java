package com.example.filmspace_mobile.data.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "http://10.0.94.163:8080/";
    private static Retrofit retrofit = null;

    private static Gson createGson() {
        return new GsonBuilder()
                .setLenient()
                .registerTypeAdapter(int.class, new SafeIntegerAdapter())
                .registerTypeAdapter(Integer.class, new SafeIntegerAdapter())
                .create();
    }

    private static OkHttpClient createOkHttpClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .addInterceptor(new AuthInterceptor(com.example.filmspace_mobile.FilmSpaceApplication.getInstance()))
                .build();
    }

    public static Retrofit getInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(createOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create(createGson()))
                    .build();
        }
        return retrofit;
    }

    public static ApiService getApiService() {
        return getInstance().create(ApiService.class);
    }
}
