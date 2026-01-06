package com.example.filmspace_mobile.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.filmspace_mobile.data.local.UserSessionManager;
import com.example.filmspace_mobile.data.model.movie.Episode;
import com.example.filmspace_mobile.ui.subscription.PaymentMethodActivity;

/**
 * Utility class for handling premium episode access logic
 * Centralizes premium checking to ensure consistency across the app
 */
public class PremiumUtils {
    private static final String TAG = "PremiumUtils";

    /**
     * Check if user can access an episode
     * @param episode The episode to check
     * @param sessionManager User session manager instance
     * @return true if user can access the episode, false otherwise
     */
    public static boolean canUserAccessEpisode(Episode episode, UserSessionManager sessionManager) {
        if (episode == null) {
            Log.w(TAG, "Episode is null, denying access");
            return false;
        }

        // If episode is not premium, everyone can access
        if (!episode.isPremium()) {
            Log.d(TAG, "Episode " + episode.getId() + " is free, allowing access");
            return true;
        }

        // If episode is premium, check user's premium status
        boolean userIsPremium = sessionManager.isPremium();
        Log.d(TAG, "Episode " + episode.getId() + " is premium, user premium status: " + userIsPremium);
        
        return userIsPremium;
    }

    /**
     * Show premium dialog when user tries to access premium content without subscription
     * @param context The context to show dialog in
     * @param episode The premium episode user tried to access
     */
    public static void showPremiumDialog(Context context, Episode episode) {
        Log.d(TAG, "Showing premium dialog for episode: " + episode.getId());
        
        new AlertDialog.Builder(context)
                .setTitle("Nội dung Premium")
                .setMessage("Tập phim này yêu cầu gói Premium. Bạn có muốn nâng cấp tài khoản không?")
                .setPositiveButton("Nâng cấp", (dialog, which) -> {
                    Intent intent = new Intent(context, PaymentMethodActivity.class);
                    context.startActivity(intent);
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .show();
    }

    /**
     * Show premium dialog with simple message (for auto-play scenarios)
     * @param context The context to show dialog in
     */
    public static void showPremiumDialog(Context context) {
        Log.d(TAG, "Showing simple premium dialog");
        
        new AlertDialog.Builder(context)
                .setTitle("Nội dung Premium")
                .setMessage("Tập tiếp theo yêu cầu gói Premium. Vui lòng nâng cấp tài khoản để tiếp tục xem.")
                .setPositiveButton("Nâng cấp", (dialog, which) -> {
                    Intent intent = new Intent(context, PaymentMethodActivity.class);
                    context.startActivity(intent);
                })
                .setNegativeButton("Đóng", (dialog, which) -> dialog.dismiss())
                .show();
    }

    /**
     * Log premium access attempt for debugging
     * @param episode The episode being accessed
     * @param userIsPremium User's premium status
     * @param accessGranted Whether access was granted
     */
    public static void logPremiumAccess(Episode episode, boolean userIsPremium, boolean accessGranted) {
        Log.i(TAG, String.format("Premium Access - Episode: %d, Premium: %s, User Premium: %s, Access: %s",
                episode.getId(), 
                episode.isPremium() ? "YES" : "NO",
                userIsPremium ? "YES" : "NO",
                accessGranted ? "GRANTED" : "DENIED"));
    }
}