package com.example.filmspace_mobile.ui.auth.SignInFragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.filmspace_mobile.R;
import com.example.filmspace_mobile.utils.ValidationHelper;
import com.example.filmspace_mobile.viewmodel.AuthViewModel;
import com.example.filmspace_mobile.ui.main.MainActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SignInFragment extends Fragment {

    private TextInputEditText emailInput, passwordInput;
    private Button continueButton;
    private LinearLayout continueWithGoogleButton;
    private TextView forgotPasswordTextView, signUpTextView;
    private ImageButton backButton;
    private String userEmail;
    private AuthViewModel authViewModel;

    public SignInFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_in, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel with Hilt
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        // Initialize views
        emailInput = view.findViewById(R.id.emailTextInput);
        passwordInput = view.findViewById(R.id.passwordTextInput);
        continueButton = view.findViewById(R.id.continueButton);
        continueWithGoogleButton = view.findViewById(R.id.continueWithGoogleButton);
        forgotPasswordTextView = view.findViewById(R.id.forgotPasswordTextView);
        signUpTextView = view.findViewById(R.id.signUpTextView);
        backButton = view.findViewById(R.id.imageButton);

        // Observe login response
        authViewModel.getLoginResponse().observe(getViewLifecycleOwner(), loginResponse -> {
            if (loginResponse != null && loginResponse.getToken() != null) {
                Snackbar.make(view, R.string.login_successful, Snackbar.LENGTH_SHORT).show();
                // Clear data to prevent re-triggering
                authViewModel.clearLoginData();
                // Navigate to MainActivity with proper flags to avoid unnecessary recreation
                Intent intent = new Intent(requireActivity(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                // No need to call finish() - flags handle clearing the back stack
            }
        });

        // Observe login error
        authViewModel.getLoginError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Snackbar.make(view, error, Snackbar.LENGTH_LONG).show();
                // Clear error after showing
                authViewModel.clearLoginData();
            }
        });

        // Observe login loading
        authViewModel.getLoginLoading().observe(getViewLifecycleOwner(), isLoading -> {
            continueButton.setEnabled(!isLoading);
            view.findViewById(R.id.progressBar).setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        // Continue button click
        continueButton.setOnClickListener(v -> handleSignIn());

        // Hide Google Sign-In button (not implemented yet)
        continueWithGoogleButton.setVisibility(View.GONE);

        // Forgot Password click
        forgotPasswordTextView.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_signInFragment_to_forgotPasswordFragment);
        });

        // Sign Up click
        signUpTextView.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_signInFragment_to_registerFragment);
        });

        // Back button click
        backButton.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });
    }

    private void handleSignIn() {
        // Validate inputs using ValidationHelper
        if (!ValidationHelper.validateEmail(emailInput)) {
            return;
        }
        
        if (!ValidationHelper.validatePassword(passwordInput)) {
            return;
        }

        // Call login via ViewModel
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        userEmail = email;
        authViewModel.login(email, password);
    }



    private void navigateToOTPVerification() {
        Bundle bundle = new Bundle();
        bundle.putString("email", userEmail);
        Navigation.findNavController(requireView()).navigate(R.id.action_signInFragment_to_OTPVerificationFragment, bundle);
    }
}