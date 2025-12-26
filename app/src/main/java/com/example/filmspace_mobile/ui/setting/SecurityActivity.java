package com.example.filmspace_mobile.ui.setting;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import com.example.filmspace_mobile.R;

public class SecurityActivity extends AppCompatActivity {

    private ImageView backIcon;
    private SwitchCompat switchFaceID;
    private SwitchCompat switchRememberPassword;
    private SwitchCompat switchTouchID;

    private SharedPreferences prefs;
    private static final String PREFS_NAME = "security_prefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        initViews();
        loadSettings();
        setupListeners();
    }

    private void initViews() {
        backIcon = findViewById(R.id.backIcon);
        switchFaceID = findViewById(R.id.switchFaceID);
        switchRememberPassword = findViewById(R.id.switchRememberPassword);
        switchTouchID = findViewById(R.id.switchTouchID);
    }

    private void loadSettings() {
        switchFaceID.setChecked(prefs.getBoolean("face_id", true));
        switchRememberPassword.setChecked(prefs.getBoolean("remember_password", true));
        switchTouchID.setChecked(prefs.getBoolean("touch_id", true));
    }

    private void setupListeners() {
        backIcon.setOnClickListener(v -> finish());

        switchFaceID.setOnCheckedChangeListener((buttonView, isChecked) ->
                saveSetting("face_id", isChecked));

        switchRememberPassword.setOnCheckedChangeListener((buttonView, isChecked) ->
                saveSetting("remember_password", isChecked));

        switchTouchID.setOnCheckedChangeListener((buttonView, isChecked) ->
                saveSetting("touch_id", isChecked));
    }

    private void saveSetting(String key, boolean value) {
        prefs.edit().putBoolean(key, value).apply();
    }
}