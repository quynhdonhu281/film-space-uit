package com.example.filmspace_mobile.ui.main;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.filmspace_mobile.ui.main.HomeFragment.HomeFragment;
import com.example.filmspace_mobile.R;
import com.example.filmspace_mobile.databinding.ActivityMainBinding;

import androidx.core.splashscreen.SplashScreen;

import java.util.HashMap;
import java.util.Map;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    
    // Fragment cache to prevent recreation
    private final Map<Integer, Fragment> fragmentMap = new HashMap<>();
    private Fragment activeFragment;
    
    // Global loading and error views
    private ProgressBar globalLoadingIndicator;
    private LinearLayout globalErrorView;
    private TextView errorMessageText;
    private Button retryButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // Initialize global views
        globalLoadingIndicator = binding.globalLoadingIndicator;
        globalErrorView = binding.globalErrorView;
        errorMessageText = binding.errorMessageText;
        retryButton = binding.retryButton;
        
        // Setup retry button
        retryButton.setOnClickListener(v -> {
            hideError();
            // Reload current fragment data
            if (activeFragment instanceof HomeFragment) {
                ((HomeFragment) activeFragment).reloadData();
            }
        });
        
        // Initialize with HomeFragment
        if (savedInstanceState == null) {
            showFragment(R.id.home);
        }

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            showFragment(id);
            return true;
        });
    }

    /**
     * Show fragment using show/hide pattern with caching
     * This prevents fragment recreation and preserves state
     */
    private void showFragment(int menuItemId) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Get or create fragment using factory
        Fragment fragment = fragmentMap.get(menuItemId);
        if (fragment == null) {
            fragment = MainFragmentFactory.createFragment(menuItemId);
            fragmentMap.put(menuItemId, fragment);
            transaction.add(R.id.frameLayout, fragment, MainFragmentFactory.getFragmentTag(menuItemId));
        }

        // Hide current active fragment
        if (activeFragment != null && activeFragment != fragment) {
            transaction.hide(activeFragment);
        }

        // Show target fragment
        transaction.show(fragment);
        activeFragment = fragment;
        transaction.commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save selected tab
        if (binding.bottomNavigationView.getSelectedItemId() != 0) {
            outState.putInt("selected_tab", binding.bottomNavigationView.getSelectedItemId());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore selected tab
        int selectedTab = savedInstanceState.getInt("selected_tab", R.id.home);
        binding.bottomNavigationView.setSelectedItemId(selectedTab);
    }
    
    /**
     * Show global loading indicator
     */
    public void showLoading() {
        runOnUiThread(() -> {
            globalLoadingIndicator.setVisibility(View.VISIBLE);
            globalErrorView.setVisibility(View.GONE);
        });
    }
    
    /**
     * Hide global loading indicator
     */
    public void hideLoading() {
        runOnUiThread(() -> {
            globalLoadingIndicator.setVisibility(View.GONE);
        });
    }
    
    /**
     * Show global error view
     */
    public void showError(String message) {
        runOnUiThread(() -> {
            globalLoadingIndicator.setVisibility(View.GONE);
            globalErrorView.setVisibility(View.VISIBLE);
            errorMessageText.setText(message);
        });
    }
    
    /**
     * Hide global error view
     */
    public void hideError() {
        runOnUiThread(() -> {
            globalErrorView.setVisibility(View.GONE);
        });
    }
}
