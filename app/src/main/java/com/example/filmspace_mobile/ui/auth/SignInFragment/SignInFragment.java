package com.example.filmspace_mobile.ui.auth.SignInFragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.filmspace_mobile.R;
import com.example.filmspace_mobile.viewmodel.AuthViewModel;
import com.example.filmspace_mobile.ui.main.MainActivity;
import com.google.android.material.textfield.TextInputEditText;

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

        // Initialize ViewModel
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

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
                Toast.makeText(requireContext(), "Login successful", Toast.LENGTH_SHORT).show();
                // Navigate to MainActivity
                Intent intent = new Intent(requireActivity(), MainActivity.class);
                startActivity(intent);
                requireActivity().finish();
            }
        });

        // Observe login error
        authViewModel.getLoginError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        // Observe login loading
        authViewModel.getLoginLoading().observe(getViewLifecycleOwner(), isLoading -> {
            continueButton.setEnabled(!isLoading);
            view.findViewById(R.id.progressBar).setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        // Continue button click
        continueButton.setOnClickListener(v -> handleSignIn());

        // Continue with Google button click
        continueWithGoogleButton.setOnClickListener(v -> handleGoogleSignIn());

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
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // Validate email format
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

        // Call login via ViewModel
        userEmail = email;
        authViewModel.login(email, password);
    }



    private void navigateToOTPVerification() {
        Bundle bundle = new Bundle();
        bundle.putString("email", userEmail);
        Navigation.findNavController(requireView()).navigate(R.id.action_signInFragment_to_OTPVerificationFragment, bundle);
    }

    private void handleGoogleSignIn() {
        // TODO: Implement Google Sign-In
        /*
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        */

        Toast.makeText(requireContext(), "Google Sign-In not implemented yet", Toast.LENGTH_SHORT).show();
    }
}