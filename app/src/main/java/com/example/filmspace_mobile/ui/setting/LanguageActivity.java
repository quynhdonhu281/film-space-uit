package com.example.filmspace_mobile.ui.setting;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.filmspace_mobile.R;

public class LanguageActivity extends AppCompatActivity {

    private ImageView backIcon;
    private LinearLayout languageEnglish;
    private ImageView checkIcon;

    private SharedPreferences prefs;
    private static final String PREFS_NAME = "language_prefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        initViews();
        setupListeners();
    }

    private void initViews() {
        backIcon = findViewById(R.id.backIcon);
        languageEnglish = findViewById(R.id.languageEnglish);
        checkIcon = findViewById(R.id.checkIcon);
    }

    private void setupListeners() {
        backIcon.setOnClickListener(v -> finish());

        languageEnglish.setOnClickListener(v -> {
            prefs.edit().putString("language", "en").apply();
            Toast.makeText(this, "Language set to English", Toast.LENGTH_SHORT).show();
        });
    }
}