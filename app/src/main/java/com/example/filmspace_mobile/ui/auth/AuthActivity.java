package com.example.filmspace_mobile.ui.auth;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.fragment.NavHostFragment;

import com.example.filmspace_mobile.R;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        // Check if we should show register screen
        boolean showRegister = getIntent().getBooleanExtra("showRegister", false);
        
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.authFragmentContainer);
        
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            NavGraph navGraph = navController.getNavInflater().inflate(R.navigation.auth_navigation);
            
            if (showRegister) {
                // Set register fragment as start destination so back button works correctly
                navGraph.setStartDestination(R.id.registerFragment);
            } else {
                // Default to sign in
                navGraph.setStartDestination(R.id.signInFragment);
            }
            
            navController.setGraph(navGraph);
        }
    }
}