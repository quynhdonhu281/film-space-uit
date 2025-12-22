package com.example.filmspace_mobile.ui.auth.ForgotPasswordFragment;

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
import android.widget.Toast;

import com.example.filmspace_mobile.R;
import com.example.filmspace_mobile.viewmodel.AuthViewModel;
import com.google.android.material.textfield.TextInputEditText;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ForgotPasswordFragment extends Fragment {

    private AuthViewModel authViewModel;

    public ForgotPasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        TextInputEditText emailInput = view.findViewById(R.id.emailTextInput);

        // Observe forgot password response
        authViewModel.getForgotPasswordResponse().observe(getViewLifecycleOwner(), response -> {
            if (response != null) {
                Toast.makeText(requireContext(), "OTP sent to your email", Toast.LENGTH_SHORT).show();
                // Navigate directly to Create New Password
                Bundle bundle = new Bundle();
                bundle.putString("email", emailInput.getText().toString().trim());
                Navigation.findNavController(view).navigate(R.id.action_forgotPasswordFragment_to_createNewPasswordFragment, bundle);
            }
        });

        // Observe forgot password error
        authViewModel.getForgotPasswordError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        // Observe forgot password loading
        authViewModel.getForgotPasswordLoading().observe(getViewLifecycleOwner(), isLoading -> {
            view.findViewById(R.id.continueButton).setEnabled(!isLoading);
            view.findViewById(R.id.progressBar).setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        view.findViewById(R.id.continueButton).setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                emailInput.setError("Email is required");
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInput.setError("Invalid email format");
                return;
            }

            // Call forgot password via ViewModel
            authViewModel.forgotPassword(email);
        });

        view.findViewById(R.id.imageButton).setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });
    }
}