package com.example.filmspace_mobile.ui.test;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.filmspace_mobile.R;
import com.example.filmspace_mobile.ui.movie.VideoPlayerActivity;

public class TestLauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Create simple layout with button
        Button testButton = new Button(this);
        testButton.setText("Test VideoPlayer");
        testButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, VideoPlayerActivity.class);
            intent.putExtra(VideoPlayerActivity.EXTRA_MOVIE_ID, 12); // Test movie ID
            intent.putExtra(VideoPlayerActivity.EXTRA_MOVIE_TITLE, "Test Movie");
            startActivity(intent);
        });
        
        setContentView(testButton);
    }
}