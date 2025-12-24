package com.example.filmspace_mobile.ui.auth.RegisterFragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.filmspace_mobile.BuildConfig;
import com.example.filmspace_mobile.R;
import com.example.filmspace_mobile.viewmodel.AuthViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.util.regex.Pattern;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RegisterFragment extends Fragment {

    private AuthViewModel authViewModel;
    private String userEmail;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel with Hilt
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        TextInputEditText fullnameInput = view.findViewById(R.id.fullnameTextInput);
        TextInputEditText usernameInput = view.findViewById(R.id.usernameTextInput);
        TextInputEditText emailInput = view.findViewById(R.id.emailTextInput);
        TextInputEditText passwordInput = view.findViewById(R.id.passwordTextInput);
        TextInputEditText confirmPasswordInput = view.findViewById(R.id.confirmPasswordTextInput);
        
        // Prevent spaces in username input
        usernameInput.setFilters(new android.text.InputFilter[] {
            new android.text.InputFilter() {
                @Override
                public CharSequence filter(CharSequence source, int start, int end, android.text.Spanned dest, int dstart, int dend) {
                    if (source.toString().contains(" ")) {
                        return source.toString().replace(" ", "");
                    }
                    return null;
                }
            }
        });
        
        // Add password strength indicator
        passwordInput.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = s.toString();
                if (password.isEmpty()) {
                    passwordInput.setError(null);
                    return;
                }
                
                // Check password strength
                boolean hasLength = password.length() >= 8;
                boolean hasUpper = Pattern.compile("[A-Z]").matcher(password).find();
                boolean hasLower = Pattern.compile("[a-z]").matcher(password).find();
                boolean hasSpecial = Pattern.compile("[^a-zA-Z0-9]").matcher(password).find();
                
                if (!hasLength || !hasUpper || !hasLower || !hasSpecial) {
                    StringBuilder hint = new StringBuilder("Weak: Need ");
                    if (!hasLength) hint.append("8+ chars, ");
                    if (!hasUpper) hint.append("uppercase, ");
                    if (!hasLower) hint.append("lowercase, ");
                    if (!hasSpecial) hint.append("special char, ");
                    passwordInput.setError(hint.substring(0, hint.length() - 2));
                } else {
                    passwordInput.setError(null);
                }
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        });
        
        // Confirm password matching feedback
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
            public void afterTextChanged(Editable s) {}
        });

        // Observe register response
        authViewModel.getRegisterResponse().observe(getViewLifecycleOwner(), registerResponse -> {
            if (registerResponse != null && registerResponse.getMessage() != null) {
                if (BuildConfig.DEBUG) {
                    android.util.Log.d("RegisterFragment", "Register response: message=" + registerResponse.getMessage() + ", email=" + registerResponse.getEmail());
                }
                Toast.makeText(requireContext(), "Registration successful! OTP sent to your email", Toast.LENGTH_SHORT).show();
                // Navigate to OTP verification with email
                Bundle bundle = new Bundle();
                bundle.putString("email", userEmail);
                Navigation.findNavController(view).navigate(R.id.action_registerFragment_to_OTPVerificationFragment, bundle);
            } else {
                if (BuildConfig.DEBUG) {
                    android.util.Log.d("RegisterFragment", "Register response is null or no message");
                }
            }
        });

        // Observe register error
        authViewModel.getRegisterError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                if (BuildConfig.DEBUG) {
                    android.util.Log.e("RegisterFragment", "Register error: " + error);
                }
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
            }
        });

        // Observe register loading
        authViewModel.getRegisterLoading().observe(getViewLifecycleOwner(), isLoading -> {
            view.findViewById(R.id.continueButton).setEnabled(!isLoading);
            view.findViewById(R.id.progressBar).setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        view.findViewById(R.id.continueButton).setOnClickListener(v -> {
            String fullname = fullnameInput.getText().toString().trim();
            String username = usernameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String confirmPassword = confirmPasswordInput.getText().toString().trim();

            // Validate fullname
            if (TextUtils.isEmpty(fullname)) {
                fullnameInput.setError("Fullname is required");
                fullnameInput.requestFocus();
                return;
            }

            if (!fullname.matches("[a-zA-Z ]+")) {
                fullnameInput.setError("Fullname must contain only English letters");
                fullnameInput.requestFocus();
                return;
            }

            // Validate username
            if (TextUtils.isEmpty(username)) {
                usernameInput.setError("Username is required");
                usernameInput.requestFocus();
                return;
            }

            if (username.contains(" ")) {
                usernameInput.setError("Username cannot contain spaces");
                usernameInput.requestFocus();
                return;
            }

            if (username.length() < 3) {
                usernameInput.setError("Username must be at least 3 characters");
                usernameInput.requestFocus();
                return;
            }

            // Validate email
            if (TextUtils.isEmpty(email)) {
                emailInput.setError("Email is required");
                emailInput.requestFocus();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInput.setError("Invalid email format");
                emailInput.requestFocus();
                return;
            }

            // Validate password
            if (TextUtils.isEmpty(password)) {
                passwordInput.setError("Password is required");
                passwordInput.requestFocus();
                return;
            }

            if (password.length() < 8 ||
                !Pattern.compile("[A-Z]").matcher(password).find() ||
                !Pattern.compile("[a-z]").matcher(password).find() ||
                !Pattern.compile("[^a-zA-Z0-9]").matcher(password).find()) {
                passwordInput.setError("Password must be 8+ chars with uppercase, lowercase, and special character");
                passwordInput.requestFocus();
                return;
            }

            if (!password.equals(confirmPassword)) {
                confirmPasswordInput.setError("Passwords do not match");
                confirmPasswordInput.requestFocus();
                return;
            }

            // Store email for later use
            userEmail = email;
            // Call register via ViewModel with all fields
            authViewModel.register(email, password, username, fullname);
        });

        // Continue with Google
        view.findViewById(R.id.continueWithGoogleButton).setOnClickListener(v -> {
            // TODO: Implement Google Sign-Up
            Toast.makeText(requireContext(), "Google Sign-Up not implemented", Toast.LENGTH_SHORT).show();
        });

        // Sign in link
        view.findViewById(R.id.signInTextView).setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_registerFragment_to_signInFragment);
        });

        view.findViewById(R.id.imageButton).setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });
    }
}