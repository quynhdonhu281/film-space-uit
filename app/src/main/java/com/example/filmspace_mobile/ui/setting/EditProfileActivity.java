package com.example.filmspace_mobile.ui.setting;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.filmspace_mobile.R;
import com.example.filmspace_mobile.data.local.UserSessionManager;
import com.example.filmspace_mobile.data.model.auth.UserResponse;
import com.example.filmspace_mobile.data.repository.RepositoryCallback;
import com.example.filmspace_mobile.data.repository.UserRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EditProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView backIcon;
    private ImageView profileImage;
    private EditText editUsername;
    private EditText editEmail;
    private Button btnSave;
    private ProgressBar progressBar;

    private Uri selectedImageUri;
    private File selectedImageFile;

    @Inject
    UserRepository userRepository;

    @Inject
    UserSessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initViews();
        setupListeners();
        loadUserData();
    }

    private void initViews() {
        backIcon = findViewById(R.id.backIcon);
        profileImage = findViewById(R.id.profileImage);
        editUsername = findViewById(R.id.editUsername);
        editEmail = findViewById(R.id.editEmail);
        btnSave = findViewById(R.id.btnSave);
        progressBar = new ProgressBar(this);
    }

    private void setupListeners() {
        backIcon.setOnClickListener(v -> finish());

        profileImage.setOnClickListener(v -> openImagePicker());

        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void loadUserData() {
        // Load from session first for immediate display
        String username = sessionManager.getUsername();
        String email = sessionManager.getEmail();
        String avatarUrl = sessionManager.getAvatarUrl();

        if (username != null) {
            editUsername.setText(username);
        }
        if (email != null) {
            editEmail.setText(email);
        }
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Glide.with(this)
                    .load(avatarUrl)
                    .placeholder(R.drawable.ic_profile_edit)
                    .circleCrop()
                    .into(profileImage);
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            
            // Show selected image immediately in UI
            Glide.with(this)
                    .load(selectedImageUri)
                    .placeholder(R.drawable.ic_profile_edit)
                    .circleCrop()
                    .into(profileImage);
            
            // Convert URI to File for upload
            selectedImageFile = getFileFromUri(selectedImageUri);
        }
    }

    private File getFileFromUri(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;

            String fileName = getFileNameFromUri(uri);
            File tempFile = new File(getCacheDir(), fileName);
            
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            
            outputStream.close();
            inputStream.close();
            
            return tempFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getFileNameFromUri(Uri uri) {
        String fileName = "avatar.jpg";
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            if (nameIndex >= 0 && cursor.moveToFirst()) {
                fileName = cursor.getString(nameIndex);
            }
            cursor.close();
        }
        return fileName;
    }

    private void saveProfile() {
        String username = editUsername.getText().toString().trim();
        String email = editEmail.getText().toString().trim();

        if (username.isEmpty()) {
            editUsername.setError("Username is required");
            editUsername.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            editEmail.setError("Email is required");
            editEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError("Please enter a valid email");
            editEmail.requestFocus();
            return;
        }

        // Show loading
        btnSave.setEnabled(false);
        btnSave.setText("Saving...");

        int userId = sessionManager.getUserId();
        String name = sessionManager.getName(); // Keep existing name

        if (selectedImageFile != null) {
            // Update with new avatar
            userRepository.updateUserWithAvatar(userId, username, name, email, selectedImageFile, new RepositoryCallback<UserResponse>() {
                @Override
                public void onSuccess(UserResponse user) {
                    handleUpdateSuccess(user);
                }

                @Override
                public void onError(String message) {
                    handleUpdateError(message);
                }
            });
        } else {
            // Update without avatar
            userRepository.updateUserWithoutAvatar(userId, username, name, email, new RepositoryCallback<UserResponse>() {
                @Override
                public void onSuccess(UserResponse user) {
                    handleUpdateSuccess(user);
                }

                @Override
                public void onError(String message) {
                    handleUpdateError(message);
                }
            });
        }
    }

    private void handleUpdateSuccess(UserResponse user) {
        // Update session with new data
        sessionManager.saveUserSession(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getAvatarUrl(),
                user.getName(),
                sessionManager.getToken() // Keep existing token
        );

        runOnUiThread(() -> {
            btnSave.setEnabled(true);
            btnSave.setText("Save Changes");
            Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void handleUpdateError(String message) {
        runOnUiThread(() -> {
            btnSave.setEnabled(true);
            btnSave.setText("Save Changes");
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        });
    }
}