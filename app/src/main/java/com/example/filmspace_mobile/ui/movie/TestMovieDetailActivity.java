package com.example.filmspace_mobile.ui.movie;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Test launcher activity to directly open MovieDetailActivity with a test movie ID
 * This is a temporary activity for testing the movie detail functionality
 */
public class TestMovieDetailActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Launch MovieDetailActivity with test movie ID = 1
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra("movieId", 1);
        startActivity(intent);
        
        // Close this test activity immediately
        finish();
    }
}
