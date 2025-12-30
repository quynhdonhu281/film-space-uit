package com.example.filmspace_mobile.di;

import android.content.Context;

import com.example.filmspace_mobile.BuildConfig;
import com.example.filmspace_mobile.FilmSpaceApplication;
import com.example.filmspace_mobile.data.api.ApiService;
import com.example.filmspace_mobile.data.api.AuthInterceptor;
import com.example.filmspace_mobile.data.api.SafeIntegerAdapter;
import com.example.filmspace_mobile.data.api.deserializer.CastDeserializer;
import com.example.filmspace_mobile.data.api.deserializer.EpisodeDeserializer;
import com.example.filmspace_mobile.data.api.deserializer.LoginResponseDeserializer;
import com.example.filmspace_mobile.data.api.deserializer.MovieDeserializer;
import com.example.filmspace_mobile.data.api.deserializer.UserResponseDeserializer;
import com.example.filmspace_mobile.data.local.UserSessionManager;
import com.example.filmspace_mobile.data.model.auth.LoginResponse;
import com.example.filmspace_mobile.data.model.auth.UserResponse;
import com.example.filmspace_mobile.data.model.movie.Cast;
import com.example.filmspace_mobile.data.model.movie.Episode;
import com.example.filmspace_mobile.data.model.movie.Movie;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {

    @Provides
    @Singleton
    public Gson provideGson() {
        return new GsonBuilder()
                .setLenient()
                .registerTypeAdapter(int.class, new SafeIntegerAdapter())
                .registerTypeAdapter(Integer.class, new SafeIntegerAdapter())
                .registerTypeAdapter(Movie.class, new MovieDeserializer())
                .registerTypeAdapter(Cast.class, new CastDeserializer())
                .registerTypeAdapter(Episode.class, new EpisodeDeserializer())
                .registerTypeAdapter(LoginResponse.class, new LoginResponseDeserializer())
                .registerTypeAdapter(UserResponse.class, new UserResponseDeserializer())
                .create();
    }

    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient(@ApplicationContext Context context) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .addInterceptor(new AuthInterceptor((FilmSpaceApplication) context.getApplicationContext()));

        // Only add logging interceptor in debug builds
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(logging);
        }

        return builder.build();
    }

    @Provides
    @Singleton
    public Retrofit provideRetrofit(OkHttpClient okHttpClient, Gson gson) {
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.API_BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    @Provides
    @Singleton
    public ApiService provideApiService(Retrofit retrofit) {
        return retrofit.create(ApiService.class);
    }

    @Provides
    @Singleton
    public UserSessionManager provideUserSessionManager(@ApplicationContext Context context) {
        return new UserSessionManager(context);
    }
}
