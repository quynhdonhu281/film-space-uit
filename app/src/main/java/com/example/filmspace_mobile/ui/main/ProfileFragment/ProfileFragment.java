package com.example.filmspace_mobile.ui.main.ProfileFragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.filmspace_mobile.R;
import com.example.filmspace_mobile.data.local.UserSessionManager;
import com.example.filmspace_mobile.data.model.SettingItem;
import com.example.filmspace_mobile.data.repository.AuthRepository;
import com.example.filmspace_mobile.data.repository.RepositoryCallback;
import com.example.filmspace_mobile.databinding.FragmentProfileBinding;
import com.example.filmspace_mobile.ui.adapters.SettingAdapter;
import com.example.filmspace_mobile.ui.auth.AuthActivity;
import com.example.filmspace_mobile.ui.setting.ChangePasswordActivity;
import com.example.filmspace_mobile.ui.setting.EditProfileActivity;
import com.example.filmspace_mobile.ui.setting.HelpSupportActivity;
import com.example.filmspace_mobile.ui.setting.LanguageActivity;
import com.example.filmspace_mobile.ui.setting.LegalPoliciesActivity;
import com.example.filmspace_mobile.ui.setting.NotificationsActivity;
import com.example.filmspace_mobile.ui.setting.SecurityActivity;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Fragment that displays settings UI inline as part of MainActivity
 */
@AndroidEntryPoint
public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private SettingAdapter adapter;
    private List<SettingItem> settingsList;

    @Inject
    UserSessionManager sessionManager;

    @Inject
    AuthRepository authRepository;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        setupLoginButtons();
        loadSettings();
        
        // Observe auth state changes reactively
        sessionManager.getIsLoggedInLiveData().observe(getViewLifecycleOwner(), isLoggedIn -> {
            if (isLoggedIn != null) {
                updateUIForAuthState(isLoggedIn);
            }
        });
    }
    
    /**
     * Update UI based on authentication state
     */
    private void updateUIForAuthState(boolean isLoggedIn) {
        if (isLoggedIn) {
            // User is logged in, show settings
            binding.settingsRecyclerView.setVisibility(View.VISIBLE);
            binding.loginSection.setVisibility(View.GONE);
        } else {
            // User not logged in, show login/register buttons
            binding.settingsRecyclerView.setVisibility(View.GONE);
            binding.loginSection.setVisibility(View.VISIBLE);
        }
    }

    private void setupLoginButtons() {
        binding.btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AuthActivity.class);
            startActivity(intent);
        });

        binding.btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AuthActivity.class);
            intent.putExtra("showRegister", true);
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        binding.settingsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        settingsList = new ArrayList<>();

        adapter = new SettingAdapter(settingsList, (item, position) -> {
            handleSettingClick(item);
        });

        binding.settingsRecyclerView.setAdapter(adapter);
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
        settingsList.add(new SettingItem(R.drawable.ic_help, "Help & Support", SettingItem.SettingType.NORMAL));
        settingsList.add(new SettingItem(R.drawable.ic_legal, "Legal and Policies", SettingItem.SettingType.NORMAL));

        // Logout
        settingsList.add(new SettingItem(R.drawable.ic_logout, "Logout", SettingItem.SettingType.LOGOUT));

        adapter.notifyDataSetChanged();
    }

    private void handleSettingClick(SettingItem item) {
        Intent intent = null;

        switch (item.getTitle()) {
            case "Edit Profile":
                intent = new Intent(getContext(), EditProfileActivity.class);
                break;

            case "Change Password":
                intent = new Intent(getContext(), ChangePasswordActivity.class);
                break;

            case "Notifications":
                intent = new Intent(getContext(), NotificationsActivity.class);
                break;

            case "Security":
                intent = new Intent(getContext(), SecurityActivity.class);
                break;

            case "Language":
                intent = new Intent(getContext(), LanguageActivity.class);
                break;

            case "Help & Support":
                intent = new Intent(getContext(), HelpSupportActivity.class);
                break;

            case "Legal and Policies":
                intent = new Intent(getContext(), LegalPoliciesActivity.class);
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
        new AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    performLogout();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void performLogout() {
        // Call logout API and clear session
        authRepository.logout(new RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (getView() != null) {
                            Snackbar.make(getView(), R.string.logout_successful, Snackbar.LENGTH_SHORT).show();
                        }
                        
                        Intent intent = new Intent(getContext(), AuthActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        
                        getActivity().finish();
                    });
                }
            }

            @Override
            public void onError(String error) {
                // Session is already cleared, just navigate to login
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Intent intent = new Intent(getContext(), AuthActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        
                        getActivity().finish();
                    });
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Auth state is automatically updated via LiveData observer in onViewCreated
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}