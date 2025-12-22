package com.example.filmspace_mobile;

import android.app.Application;

public class FilmSpaceApplication extends Application {
    private static FilmSpaceApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static FilmSpaceApplication getInstance() {
        return instance;
    }
}
