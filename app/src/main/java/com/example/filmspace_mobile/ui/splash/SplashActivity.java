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
    // Reduced splash duration for faster app startup
    private static final int SPLASH_DURATION = 1500; // 1.5 seconds (was 3 seconds)

    @Inject
    UserSessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Skip heavy initialization on splash screen
        // Transition to next screen as fast as possible
        navigateToNextScreenDelayed();
    }

    private void navigateToNextScreenDelayed() {
        // Minimal delay for visual splash effect, then transition
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            navigateToNextScreen();
        }, SPLASH_DURATION);
    }

    private void navigateToNextScreen() {
        Intent intent;
        if (sessionManager.isLoggedIn()) {
            intent = new Intent(SplashActivity.this, MainActivity.class);
        } else {
            intent = new Intent(SplashActivity.this, AuthActivity.class);
        }
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
