package com.example.filmspace_mobile.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import com.example.filmspace_mobile.R;
import com.example.filmspace_mobile.data.local.UserSessionManager;
import com.example.filmspace_mobile.ui.auth.AuthActivity;
import com.example.filmspace_mobile.ui.main.MainActivity;

import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_DURATION = 2000; // 2 seconds

    @Inject
    UserSessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Check if user is already logged in after splash delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent;
            if (sessionManager.isLoggedIn()) {
                // User is already logged in, go to MainActivity
                intent = new Intent(SplashActivity.this, MainActivity.class);
            } else {
                // User not logged in, go to AuthActivity
                intent = new Intent(SplashActivity.this, AuthActivity.class);
            }
            startActivity(intent);
            finish();
        }, SPLASH_DURATION);
    }
}
