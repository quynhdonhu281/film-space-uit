package com.example.filmspace_mobile.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.filmspace_mobile.data.local.converter.GenreListConverter;
import com.example.filmspace_mobile.data.local.dao.MovieDao;
import com.example.filmspace_mobile.data.local.entity.MovieEntity;

/**
 * Room database for caching app data
 */
@Database(entities = {MovieEntity.class}, version = 1, exportSchema = false)
@TypeConverters({GenreListConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    
    private static volatile AppDatabase INSTANCE;
    
    public abstract MovieDao movieDao();
    
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "filmspace_database"
                    ).build();
                }
            }
        }
        return INSTANCE;
    }
}
