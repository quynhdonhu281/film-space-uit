package com.example.filmspace_mobile.ui.main;

import androidx.fragment.app.Fragment;

import com.example.filmspace_mobile.R;
import com.example.filmspace_mobile.ui.main.FavoriteFragment.FavoriteFragment;
import com.example.filmspace_mobile.ui.main.HomeFragment.HomeFragment;
import com.example.filmspace_mobile.ui.main.ProfileFragment.ProfileFragment;
import com.example.filmspace_mobile.ui.main.SearchFragment.SearchFragment;

/**
 * Factory for creating main navigation fragments
 * This decouples fragment creation from MainActivity, making it easier to test
 */
public class MainFragmentFactory {
    
    /**
     * Create fragment based on menu item ID
     * @param menuItemId The menu item ID from bottom navigation
     * @return The corresponding fragment instance
     */
    public static Fragment createFragment(int menuItemId) {
        if (menuItemId == R.id.home) {
            return new HomeFragment();
        } else if (menuItemId == R.id.search) {
            return new SearchFragment();
        } else if (menuItemId == R.id.favorite) {
            return new FavoriteFragment();
        } else if (menuItemId == R.id.profile) {
            return new ProfileFragment();
        }
        return new HomeFragment(); // Default
    }
    
    /**
     * Get unique tag for fragment
     * @param menuItemId The menu item ID from bottom navigation
     * @return The fragment tag
     */
    public static String getFragmentTag(int menuItemId) {
        if (menuItemId == R.id.home) {
            return "HOME_FRAGMENT";
        } else if (menuItemId == R.id.search) {
            return "SEARCH_FRAGMENT";
        } else if (menuItemId == R.id.favorite) {
            return "FAVORITE_FRAGMENT";
        } else if (menuItemId == R.id.profile) {
            return "PROFILE_FRAGMENT";
        }
        return "UNKNOWN_FRAGMENT";
    }
}
