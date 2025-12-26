package com.example.filmspace_mobile.ui.setting;

import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.filmspace_mobile.R;

public class ChangePasswordActivity extends AppCompatActivity {

    private ImageView backIcon;
    private EditText editNewPassword;
    private EditText editConfirmPassword;
    private ImageView eyeIcon1;
    private ImageView eyeIcon2;
    private Button btnChange;

    private boolean isNewPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        initViews();
        setupListeners();
    }

    private void initViews() {
        backIcon = findViewById(R.id.backIcon);
        editNewPassword = findViewById(R.id.editNewPassword);
        editConfirmPassword = findViewById(R.id.editConfirmPassword);
        eyeIcon1 = findViewById(R.id.eyeIcon1);
        eyeIcon2 = findViewById(R.id.eyeIcon2);
        btnChange = findViewById(R.id.btnChange);
    }

    private void setupListeners() {
        backIcon.setOnClickListener(v -> finish());

        eyeIcon1.setOnClickListener(v -> togglePasswordVisibility(editNewPassword, eyeIcon1, true));
        eyeIcon2.setOnClickListener(v -> togglePasswordVisibility(editConfirmPassword, eyeIcon2, false));

        btnChange.setOnClickListener(v -> changePassword());
    }

    private void togglePasswordVisibility(EditText editText, ImageView eyeIcon, boolean isNewPassword) {
        if (isNewPassword) {
            isNewPasswordVisible = !isNewPasswordVisible;
            if (isNewPasswordVisible) {
                editText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                eyeIcon.setImageResource(R.drawable.ic_eye); // Nếu có icon này
            } else {
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                eyeIcon.setImageResource(R.drawable.ic_eye);
            }
        } else {
            isConfirmPasswordVisible = !isConfirmPasswordVisible;
            if (isConfirmPasswordVisible) {
                editText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                eyeIcon.setImageResource(R.drawable.ic_eye);
            } else {
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                eyeIcon.setImageResource(R.drawable.ic_eye);
            }
        }

        // Move cursor to end
        editText.setSelection(editText.getText().length());
    }

    private void changePassword() {
        String newPassword = editNewPassword.getText().toString().trim();
        String confirmPassword = editConfirmPassword.getText().toString().trim();

        if (newPassword.isEmpty()) {
            editNewPassword.setError("Please enter new password");
            editNewPassword.requestFocus();
            return;
        }

        if (newPassword.length() < 6) {
            editNewPassword.setError("Password must be at least 6 characters");
            editNewPassword.requestFocus();
            return;
        }

        if (confirmPassword.isEmpty()) {
            editConfirmPassword.setError("Please confirm your password");
            editConfirmPassword.requestFocus();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            editConfirmPassword.setError("Passwords do not match");
            editConfirmPassword.requestFocus();
            return;
        }


        Toast.makeText(this, "Password changed successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }
}