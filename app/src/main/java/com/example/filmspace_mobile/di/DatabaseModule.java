package com.example.filmspace_mobile.di;

import android.content.Context;

import com.example.filmspace_mobile.data.local.AppDatabase;
import com.example.filmspace_mobile.data.local.dao.MovieDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

/**
 * Hilt module for providing database dependencies
 */
@Module
@InstallIn(SingletonComponent.class)
public class DatabaseModule {
    
    @Provides
    @Singleton
    public AppDatabase provideAppDatabase(@ApplicationContext Context context) {
        return AppDatabase.getInstance(context);
    }
    
    @Provides
    @Singleton
    public MovieDao provideMovieDao(AppDatabase database) {
        return database.movieDao();
    }
}
