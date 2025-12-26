package com.example.filmspace_mobile.ui.setting;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.filmspace_mobile.R;

public class LegalPoliciesActivity extends AppCompatActivity {

    private ImageView backIcon;
    private TextView tvTerms;
    private TextView tvChanges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_legal_policies);

        initViews();
        setupListeners();
        loadContent();
    }

    private void initViews() {
        backIcon = findViewById(R.id.backIcon);
        tvTerms = findViewById(R.id.tvTerms);
        tvChanges = findViewById(R.id.tvChanges);
    }

    private void setupListeners() {
        backIcon.setOnClickListener(v -> finish());
    }

    private void loadContent() {
    }
}