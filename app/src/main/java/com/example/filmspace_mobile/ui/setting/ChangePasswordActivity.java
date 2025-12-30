package com.example.filmspace_mobile.ui.setting;

import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.filmspace_mobile.R;
import com.example.filmspace_mobile.data.local.UserSessionManager;
import com.example.filmspace_mobile.data.model.auth.ResetPasswordResponse;
import com.example.filmspace_mobile.data.repository.AuthRepository;
import com.example.filmspace_mobile.data.repository.RepositoryCallback;
import com.google.android.material.snackbar.Snackbar;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ChangePasswordActivity extends AppCompatActivity {

    private ImageView backIcon;
    private EditText editNewPassword;
    private EditText editConfirmPassword;
    private EditText editOtp;
    private ImageView eyeIcon1;
    private ImageView eyeIcon2;
    private Button btnChange;
    private Button btnSendOtp;

    private boolean isNewPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;
    private boolean isOtpSent = false;
    private android.os.CountDownTimer countDownTimer;

    @Inject
    UserSessionManager sessionManager;

    @Inject
    AuthRepository authRepository;

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
        editOtp = findViewById(R.id.editOtp);
        eyeIcon1 = findViewById(R.id.eyeIcon1);
        eyeIcon2 = findViewById(R.id.eyeIcon2);
        btnChange = findViewById(R.id.btnChange);
        btnSendOtp = findViewById(R.id.btnSendOtp);
    }

    private void setupListeners() {
        backIcon.setOnClickListener(v -> finish());

        eyeIcon1.setOnClickListener(v -> togglePasswordVisibility(editNewPassword, eyeIcon1, true));
        eyeIcon2.setOnClickListener(v -> togglePasswordVisibility(editConfirmPassword, eyeIcon2, false));

        btnSendOtp.setOnClickListener(v -> sendOtp());
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

    private void sendOtp() {
        String userEmail = sessionManager.getEmail();
        if (userEmail == null || userEmail.isEmpty()) {
            Snackbar.make(findViewById(android.R.id.content), R.string.user_email_not_found, Snackbar.LENGTH_SHORT).show();
            return;
        }

        btnSendOtp.setEnabled(false);
        btnSendOtp.setText("Sending...");

        authRepository.forgotPassword(userEmail, new RepositoryCallback<String>() {
            @Override
            public void onSuccess(String message) {
                runOnUiThread(() -> {
                    isOtpSent = true;
                    Snackbar.make(findViewById(android.R.id.content), "OTP sent to your email", Snackbar.LENGTH_SHORT).show();
                    startCountdown();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    btnSendOtp.setEnabled(true);
                    btnSendOtp.setText(isOtpSent ? "Resend OTP" : "Send OTP");
                    Snackbar.make(findViewById(android.R.id.content), error, Snackbar.LENGTH_LONG).show();
                });
            }
        });
    }

    private void startCountdown() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new android.os.CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int secondsLeft = (int) (millisUntilFinished / 1000);
                btnSendOtp.setText("Resend (" + secondsLeft + "s)");
            }

            @Override
            public void onFinish() {
                btnSendOtp.setEnabled(true);
                btnSendOtp.setText("Resend OTP");
            }
        };
        countDownTimer.start();
    }

    private void changePassword() {
        String newPassword = editNewPassword.getText().toString().trim();
        String confirmPassword = editConfirmPassword.getText().toString().trim();
        String otp = editOtp.getText().toString().trim();

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

        if (otp.isEmpty() || otp.length() != 6) {
            editOtp.setError("Please enter 6-digit OTP");
            editOtp.requestFocus();
            return;
        }

        if (!isOtpSent) {
            Snackbar.make(findViewById(android.R.id.content), "Please send OTP first", Snackbar.LENGTH_SHORT).show();
            return;
        }

        // Get user email
        String userEmail = sessionManager.getEmail();
        if (userEmail == null || userEmail.isEmpty()) {
            Snackbar.make(findViewById(android.R.id.content), R.string.user_email_not_found, Snackbar.LENGTH_SHORT).show();
            return;
        }

        // Verify OTP and change password
        verifyOTPAndChangePassword(userEmail, otp, newPassword);
    }



    private void verifyOTPAndChangePassword(String email, String otp, String newPassword) {
        btnChange.setEnabled(false);
        btnChange.setText("Changing password...");

        authRepository.resetPassword(email, otp, newPassword, new RepositoryCallback<ResetPasswordResponse>() {
            @Override
            public void onSuccess(ResetPasswordResponse response) {
                runOnUiThread(() -> {
                    // Show success message
                    new AlertDialog.Builder(ChangePasswordActivity.this)
                        .setTitle("Password Changed")
                        .setMessage("Your password has been changed successfully. Please log in again.")
                        .setPositiveButton("OK", (dialog, which) -> {
                            // Logout user
                            sessionManager.clearSession();
                            // Navigate to login
                            android.content.Intent intent = new android.content.Intent(ChangePasswordActivity.this, com.example.filmspace_mobile.ui.auth.AuthActivity.class);
                            intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        })
                        .setCancelable(false)
                        .show();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    btnChange.setEnabled(true);
                    btnChange.setText("Change Password");
                    Snackbar.make(findViewById(android.R.id.content), error, Snackbar.LENGTH_LONG).show();
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}