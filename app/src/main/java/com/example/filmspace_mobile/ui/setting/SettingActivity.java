package com.example.filmspace_mobile.ui.setting;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.filmspace_mobile.R;
import com.example.filmspace_mobile.data.model.SettingItem;
import com.example.filmspace_mobile.ui.adapters.SettingAdapter;
import com.example.filmspace_mobile.ui.auth.AuthActivity;
import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends AppCompatActivity {

    private RecyclerView settingsRecyclerView;
    private SettingAdapter adapter;
    private List<SettingItem> settingsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initViews();
        setupRecyclerView();
        loadSettings();
    }

    private void initViews() {
        settingsRecyclerView = findViewById(R.id.settingsRecyclerView);
    }

    private void setupRecyclerView() {
        settingsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        settingsList = new ArrayList<>();

        adapter = new SettingAdapter(settingsList, (item, position) -> {
            handleSettingClick(item);
        });

        settingsRecyclerView.setAdapter(adapter);
    }

    private void loadSettings() {
        settingsList.clear();

        // Account Section
        settingsList.add(new SettingItem(R.drawable.ic_profile, "Edit Profile", SettingItem.SettingType.NORMAL));
        settingsList.add(new SettingItem(R.drawable.ic_lock2, "Change Password", SettingItem.SettingType.NORMAL));

        // Preferences Section
        settingsList.add(new SettingItem(R.drawable.ic_noti, "Notifications", SettingItem.SettingType.NORMAL));
        settingsList.add(new SettingItem(R.drawable.ic_security, "Security", SettingItem.SettingType.NORMAL));
        settingsList.add(new SettingItem(R.drawable.ic_global, "Language", "English", SettingItem.SettingType.NORMAL));

        // Support Section
        settingsList.add(new SettingItem(R.drawable.ic_help, "Help and Support", SettingItem.SettingType.NORMAL));
        settingsList.add(new SettingItem(R.drawable.ic_legal, "Legal and Policies", SettingItem.SettingType.NORMAL));

        // Logout
        settingsList.add(new SettingItem(R.drawable.ic_logout, "Logout", SettingItem.SettingType.LOGOUT));

        adapter.notifyDataSetChanged();
    }

    private void handleSettingClick(SettingItem item) {
        Intent intent = null;

        switch (item.getTitle()) {
            case "Edit Profile":
                intent = new Intent(this, EditProfileActivity.class);
                break;

            case "Change Password":
                intent = new Intent(this, ChangePasswordActivity.class);
                break;

            case "Notifications":
                intent = new Intent(this, NotificationsActivity.class);
                break;

            case "Security":
                intent = new Intent(this, SecurityActivity.class);
                break;

            case "Language":
                intent = new Intent(this, LanguageActivity.class);
                break;

            case "Help and Support":
                intent = new Intent(this, HelpSupportActivity.class);
                break;

            case "Legal and Policies":
                intent = new Intent(this, LegalPoliciesActivity.class);
                break;

            case "Logout":
                showLogoutDialog();
                return;
        }

        if (intent != null) {
            startActivity(intent);
        }
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Clear user session/preferences
                    // SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
                    // prefs.edit().clear().apply();

                    Intent intent = new Intent(this, AuthActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}