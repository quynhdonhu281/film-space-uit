package com.example.filmspace_mobile.ui.setting;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import com.example.filmspace_mobile.R;

public class NotificationsActivity extends AppCompatActivity {

    private ImageView backIcon;
    private SwitchCompat switchAssign;
    private SwitchCompat switchComment;
    private SwitchCompat switchFollow;
    private SwitchCompat switchNotification;

    private SharedPreferences prefs;
    private static final String PREFS_NAME = "notification_prefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        initViews();
        loadSettings();
        setupListeners();
    }

    private void initViews() {
        backIcon = findViewById(R.id.backIcon);
        switchAssign = findViewById(R.id.switchAssign);
        switchComment = findViewById(R.id.switchComment);
        switchFollow = findViewById(R.id.switchFollow);
        switchNotification = findViewById(R.id.switchNotification);
    }

    private void loadSettings() {
        switchAssign.setChecked(prefs.getBoolean("assign", true));
        switchComment.setChecked(prefs.getBoolean("comment", true));
        switchFollow.setChecked(prefs.getBoolean("follow", true));
        switchNotification.setChecked(prefs.getBoolean("notification", true));
    }

    private void setupListeners() {
        backIcon.setOnClickListener(v -> finish());

        switchAssign.setOnCheckedChangeListener((buttonView, isChecked) ->
                saveSetting("assign", isChecked));

        switchComment.setOnCheckedChangeListener((buttonView, isChecked) ->
                saveSetting("comment", isChecked));

        switchFollow.setOnCheckedChangeListener((buttonView, isChecked) ->
                saveSetting("follow", isChecked));

        switchNotification.setOnCheckedChangeListener((buttonView, isChecked) ->
                saveSetting("notification", isChecked));
    }

    private void saveSetting(String key, boolean value) {
        prefs.edit().putBoolean(key, value).apply();
    }
}