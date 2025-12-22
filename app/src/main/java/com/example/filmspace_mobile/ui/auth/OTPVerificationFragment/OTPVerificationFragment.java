package com.example.filmspace_mobile.ui.auth.OTPVerificationFragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.text.TextWatcher;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.filmspace_mobile.BuildConfig;
import com.example.filmspace_mobile.R;
import com.example.filmspace_mobile.viewmodel.AuthViewModel;
import com.example.filmspace_mobile.ui.main.MainActivity;
import com.example.filmspace_mobile.data.local.UserSessionManager;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class OTPVerificationFragment extends Fragment {

    private String userEmail;
    private EditText firstDigit, secondDigit, thirdDigit, fourthDigit, fifthDigit, sixthDigit;
    private AuthViewModel authViewModel;
    private UserSessionManager sessionManager;

    public OTPVerificationFragment() {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_otp_verification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize ViewModel and SessionManager
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        sessionManager = new UserSessionManager(requireContext());
        
        // Initialize views
        firstDigit = view.findViewById(R.id.firstDigitNumberInput);
        secondDigit = view.findViewById(R.id.secondDigitNumberInput);
        thirdDigit = view.findViewById(R.id.thirdDigitNumberInput);
        fourthDigit = view.findViewById(R.id.fourthDigitNumberInput);
        fifthDigit = view.findViewById(R.id.fifthDigitNumberInput);
        sixthDigit = view.findViewById(R.id.sixthDigitNumberInput);
        
        // Auto-focus logic for 6 digits
        setupAutoFocus(firstDigit, null, secondDigit);
        setupAutoFocus(secondDigit, firstDigit, thirdDigit);
        setupAutoFocus(thirdDigit, secondDigit, fourthDigit);
        setupAutoFocus(fourthDigit, thirdDigit, fifthDigit);
        setupAutoFocus(fifthDigit, fourthDigit, sixthDigit);
        setupAutoFocus(sixthDigit, fifthDigit, null);
        
        // Observe OTP verification response
        authViewModel.getVerifyOTPResponse().observe(getViewLifecycleOwner(), verifyResponse -> {
            if (verifyResponse != null && verifyResponse.getToken() != null) {
                Toast.makeText(requireContext(), "OTP verified successfully", Toast.LENGTH_SHORT).show();
                
                // Save user session
                sessionManager.saveUserSession(
                    verifyResponse.getUserId(),
                    verifyResponse.getUsername(),
                    verifyResponse.getEmail(),
                    verifyResponse.getAvatarUrl(),
                    verifyResponse.getName(),
                    verifyResponse.getToken()
                );
                
                // Navigate directly to MainActivity
                Intent intent = new Intent(requireActivity(), MainActivity.class);
                startActivity(intent);
                requireActivity().finish();
            }
        });

        // Observe OTP verification error
        authViewModel.getVerifyOTPError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        // Observe OTP verification loading
        authViewModel.getVerifyOTPLoading().observe(getViewLifecycleOwner(), isLoading -> {
            view.findViewById(R.id.continueButton).setEnabled(!isLoading);
            view.findViewById(R.id.progressBar).setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        // Observe resend OTP response
        authViewModel.getResendOTPResponse().observe(getViewLifecycleOwner(), resendResponse -> {
            if (resendResponse != null) {
                Toast.makeText(requireContext(), "OTP resent successfully", Toast.LENGTH_SHORT).show();
            }
        });

        // Observe resend OTP error
        authViewModel.getResendOTPError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
        
        // Continue button
        view.findViewById(R.id.continueButton).setOnClickListener(v -> {
            String otp = firstDigit.getText().toString() + 
                        secondDigit.getText().toString() + 
                        thirdDigit.getText().toString() + 
                        fourthDigit.getText().toString() + 
                        fifthDigit.getText().toString() + 
                        sixthDigit.getText().toString();

            if (otp.length() == 6) {
                android.util.Log.d("OTPVerificationFragment", "Verifying OTP - Email: " + userEmail + ", OTP: " + otp);
                authViewModel.verifyOTP(userEmail, otp);
            } else {
                Toast.makeText(requireContext(), "Please enter complete 6-digit OTP", Toast.LENGTH_SHORT).show();
            }
        });
        
        // Resend code
        view.findViewById(R.id.resendCodeTextView).setOnClickListener(v -> {
            authViewModel.resendOTP(userEmail);
        });

        // Back button
        view.findViewById(R.id.imageButton).setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });
    }

    private void setupAutoFocus(EditText current, EditText previous, EditText next) {
        current.addTextChangedListener(new TextWatcher() {
            private boolean isDeleting = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                isDeleting = count > after;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1 && next != null && !isDeleting) {
                    next.requestFocus();
                } else if (s.length() == 0 && previous != null && isDeleting) {
                    previous.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
}