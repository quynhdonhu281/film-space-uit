package com.example.filmspace_mobile.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.filmspace_mobile.data.local.UserSessionManager;

/**
 * Utility class for admin operations
 * Provides easy access to user information for administrative purposes
 */
public class AdminUtils {
    private static final String TAG = "AdminUtils";
    
    /**
     * Get current logged in user ID for admin purposes
     * @param context Application context
     * @return User ID as integer, -1 if not logged in
     */
    public static int getCurrentUserId(Context context) {
        UserSessionManager sessionManager = new UserSessionManager(context);
        int userId = sessionManager.getUserId();
        
        Log.i(TAG, "Admin Query - Current User ID: " + userId);
        return userId;
    }
    
    /**
     * Get premium user ID from payment preferences
     * @param context Application context
     * @return Premium user ID, -1 if not found
     */
    public static int getPremiumUserId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        int premiumUserId = prefs.getInt("premium_user_id", -1);
        
        Log.i(TAG, "Admin Query - Premium User ID: " + premiumUserId);
        return premiumUserId;
    }
    
    /**
     * Get all user information for admin dashboard
     * @param context Application context
     * @return Formatted string with user info
     */
    public static String getUserInfoForAdmin(Context context) {
        UserSessionManager sessionManager = new UserSessionManager(context);
        SharedPreferences prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        
        StringBuilder info = new StringBuilder();
        info.append("=== USER INFO FOR ADMIN ===\n");
        info.append("User ID: ").append(sessionManager.getUserId()).append("\n");
        info.append("Username: ").append(sessionManager.getUsername()).append("\n");
        info.append("Email: ").append(sessionManager.getEmail()).append("\n");
        info.append("Is Premium: ").append(sessionManager.isPremium()).append("\n");
        info.append("Premium User ID: ").append(prefs.getInt("premium_user_id", -1)).append("\n");
        info.append("Premium Plan: ").append(prefs.getString("premium_plan_name", "None")).append("\n");
        info.append("Premium Activated: ").append(prefs.getLong("premium_activated_at", 0)).append("\n");
        info.append("===========================");
        
        String result = info.toString();
        Log.i(TAG, result);
        return result;
    }
    
    /**
     * Log payment transaction for admin tracking
     * @param userId User ID who made payment
     * @param planId Plan ID purchased
     * @param amount Amount paid
     * @param transactionId Transaction ID
     */
    public static void logPaymentForAdmin(int userId, String planId, double amount, String transactionId) {
        Log.i(TAG, "=== PAYMENT TRANSACTION LOG ===");
        Log.i(TAG, "User ID: " + userId);
        Log.i(TAG, "Plan ID: " + planId);
        Log.i(TAG, "Amount: $" + amount);
        Log.i(TAG, "Transaction ID: " + transactionId);
        Log.i(TAG, "Timestamp: " + System.currentTimeMillis());
        Log.i(TAG, "===============================");
    }
    
    /**
     * Log payment transaction for admin tracking (String userId overload)
     * @param userId User ID who made payment (String format)
     * @param planId Plan ID purchased
     * @param amount Amount paid
     * @param transactionId Transaction ID
     */
    public static void logPaymentForAdmin(String userId, String planId, double amount, String transactionId) {
        Log.i(TAG, "=== PAYMENT TRANSACTION LOG ===");
        Log.i(TAG, "User ID: " + userId);
        Log.i(TAG, "Plan ID: " + planId);
        Log.i(TAG, "Amount: $" + amount);
        Log.i(TAG, "Transaction ID: " + transactionId);
        Log.i(TAG, "Timestamp: " + System.currentTimeMillis());
        Log.i(TAG, "===============================");
    }
}