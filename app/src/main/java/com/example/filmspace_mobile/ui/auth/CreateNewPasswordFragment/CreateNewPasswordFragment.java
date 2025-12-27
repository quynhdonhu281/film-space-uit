package com.example.filmspace_mobile.ui.auth.CreateNewPasswordFragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.filmspace_mobile.R;
import com.example.filmspace_mobile.viewmodel.AuthViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.util.regex.Pattern;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CreateNewPasswordFragment extends Fragment {

    private String userEmail;
    private AuthViewModel authViewModel;
    private android.os.CountDownTimer countDownTimer;

    public CreateNewPasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userEmail = getArguments().getString("email");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_new_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        TextInputEditText passwordInput = view.findViewById(R.id.passwordTextInput);
        TextInputEditText confirmPasswordInput = view.findViewById(R.id.confirmPasswordTextInput);
        TextInputEditText otpInput = view.findViewById(R.id.otpTextInput);

        // Add password matching TextWatcher
        confirmPasswordInput.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = passwordInput.getText().toString();
                String confirmPassword = s.toString();
                
                if (!confirmPassword.isEmpty() && !password.equals(confirmPassword)) {
                    confirmPasswordInput.setError("Passwords do not match");
                } else {
                    confirmPasswordInput.setError(null);
                }
            }
            
            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        // Observe forgot password response for resend OTP
        authViewModel.getForgotPasswordResponse().observe(getViewLifecycleOwner(), response -> {
            if (response != null) {
                Toast.makeText(requireContext(), "OTP resent to your email", Toast.LENGTH_SHORT).show();
            }
        });

        // Observe forgot password error for resend OTP
        authViewModel.getForgotPasswordError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), "Failed to resend OTP: " + error, Toast.LENGTH_SHORT).show();
            }
        });

        // Observe reset password response
        authViewModel.getResetPasswordResponse().observe(getViewLifecycleOwner(), resetResponse -> {
            if (resetResponse != null && resetResponse.getMessage() != null) {
                Toast.makeText(requireContext(), resetResponse.getMessage(), Toast.LENGTH_SHORT).show();
                Navigation.findNavController(view).navigate(R.id.action_createNewPasswordFragment_to_signInFragment);
            }
        });

        // Observe reset password error
        authViewModel.getResetPasswordError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        // Observe reset password loading
        authViewModel.getResetPasswordLoading().observe(getViewLifecycleOwner(), isLoading -> {
            view.findViewById(R.id.continueButton).setEnabled(!isLoading);
            view.findViewById(R.id.progressBar).setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        // Resend OTP button
        android.widget.TextView resendCodeTextView = view.findViewById(R.id.resendCodeTextView);
        resendCodeTextView.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(userEmail)) {
                resendCodeTextView.setEnabled(false);
                authViewModel.forgotPassword(userEmail);
                startCountdown(resendCodeTextView);
            } else {
                Toast.makeText(requireContext(), "Email not found", Toast.LENGTH_SHORT).show();
            }
        });

        view.findViewById(R.id.continueButton).setOnClickListener(v -> {
            String password = passwordInput.getText().toString().trim();
            String confirmPassword = confirmPasswordInput.getText().toString().trim();
            String otp = otpInput.getText().toString().trim();

            // Validate OTP
            if (TextUtils.isEmpty(otp)) {
                otpInput.setError("OTP is required");
                otpInput.requestFocus();
                return;
            }

            if (otp.length() != 6) {
                otpInput.setError("OTP must be 6 digits");
                otpInput.requestFocus();
                return;
            }

            // Validate password
            if (TextUtils.isEmpty(password)) {
                passwordInput.setError("Password is required");
                passwordInput.requestFocus();
                return;
            }

            // At least 8 characters
            if (password.length() < 8) {
                passwordInput.setError("Password must be at least 8 characters");
                passwordInput.requestFocus();
                return;
            }

            // Contains uppercase
            if (!Pattern.compile("[A-Z]").matcher(password).find()) {
                passwordInput.setError("Password must contain at least one uppercase letter");
                passwordInput.requestFocus();
                return;
            }

            // Contains lowercase
            if (!Pattern.compile("[a-z]").matcher(password).find()) {
                passwordInput.setError("Password must contain at least one lowercase letter");
                passwordInput.requestFocus();
                return;
            }

            // Contains special character
            if (!Pattern.compile("[^a-zA-Z0-9]").matcher(password).find()) {
                passwordInput.setError("Password must contain at least one special character");
                passwordInput.requestFocus();
                return;
            }

            // Confirm password matches
            if (!password.equals(confirmPassword)) {
                confirmPasswordInput.setError("Passwords do not match");
                confirmPasswordInput.requestFocus();
                return;
            }

            // Call reset password via ViewModel with OTP from user input
            authViewModel.resetPassword(userEmail, otp, password);
        });

        view.findViewById(R.id.imageButton).setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });
    }

    private void startCountdown(android.widget.TextView textView) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new android.os.CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int secondsLeft = (int) (millisUntilFinished / 1000);
                textView.setText("Resend (" + secondsLeft + "s)");
            }

            @Override
            public void onFinish() {
                textView.setEnabled(true);
                textView.setText("Resend code");
            }
        };
        countDownTimer.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}