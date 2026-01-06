package com.example.filmspace_mobile.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.example.filmspace_mobile.BuildConfig;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class UserSessionManager {
    private static final String TAG = "UserSessionManager";
    // Tên file SharedPreferences (được mã hóa)
    private static final String PREF_NAME = "filmspace_mobile_secure_session";

    // Các Key để lưu dữ liệu
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_AVATAR_URL = "avatarUrl";
    private static final String KEY_NAME = "name";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_IS_PREMIUM = "isPremium";

    private final SharedPreferences prefs;

    // LiveData để theo dõi trạng thái đăng nhập (Reactive UI)
    private final MutableLiveData<Boolean> isLoggedInLiveData = new MutableLiveData<>();

    public UserSessionManager(Context context) {
        SharedPreferences tempPrefs;
        try {
            // Tạo Master Key để mã hóa
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            // Tạo EncryptedSharedPreferences (An toàn hơn SharedPreferences thường)
            tempPrefs = EncryptedSharedPreferences.create(
                    context,
                    PREF_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "Failed to create encrypted preferences, falling back to standard", e);
            }
            // Fallback về chế độ thường nếu thiết bị không hỗ trợ mã hóa
            tempPrefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        }
        prefs = tempPrefs;

        // Cập nhật trạng thái LiveData ban đầu
        isLoggedInLiveData.setValue(isLoggedIn());
    }

    /**
     * Lấy LiveData trạng thái đăng nhập để UI observe
     */
    public LiveData<Boolean> getIsLoggedInLiveData() {
        return isLoggedInLiveData;
    }

    /**
     * Lưu session người dùng sau khi Login/Register thành công
     * Hàm này sẽ xóa dữ liệu cũ trước khi lưu mới
     */
    public void saveUserSession(int userId, String username, String email,
                                String avatarUrl, String name, String token, boolean isPremium) {
        SharedPreferences.Editor editor = prefs.edit();

        // Xóa dữ liệu cũ
        editor.clear();

        // Lưu dữ liệu mới
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_AVATAR_URL, avatarUrl);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_TOKEN, token);
        editor.putBoolean(KEY_IS_PREMIUM, isPremium);
        editor.apply();

        // Báo cho UI biết đã đăng nhập
        isLoggedInLiveData.postValue(true);
    }

    /**
     * [MỚI - QUAN TRỌNG] Cập nhật trạng thái Premium riêng lẻ
     * Dùng hàm này trong PaymentReturnActivity sau khi thanh toán thành công
     */
    public void setPremium(boolean isPremium) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_IS_PREMIUM, isPremium);
        editor.apply();
        Log.d(TAG, "setPremium: Updated premium status to " + isPremium);
    }

    /**
     * Kiểm tra đã đăng nhập chưa
     */
    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Lấy User ID
     */
    public int getUserId() {
        return prefs.getInt(KEY_USER_ID, -1);
    }

    /**
     * Lấy Username
     */
    public String getUsername() {
        return prefs.getString(KEY_USERNAME, null);
    }

    /**
     * Lấy Email
     */
    public String getEmail() {
        return prefs.getString(KEY_EMAIL, null);
    }

    /**
     * Lấy Avatar URL
     */
    public String getAvatarUrl() {
        return prefs.getString(KEY_AVATAR_URL, null);
    }

    /**
     * Lấy tên hiển thị
     */
    public String getName() {
        return prefs.getString(KEY_NAME, null);
    }

    /**
     * Lấy Token xác thực
     */
    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    /**
     * Kiểm tra user có phải là Premium không
     */
    public boolean isPremium() {
        return prefs.getBoolean(KEY_IS_PREMIUM, false);
    }

    /**
     * Xóa session (Đăng xuất)
     */
    public void clearSession() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        // Báo cho UI biết đã đăng xuất
        isLoggedInLiveData.postValue(false);
    }

    /**
     * Cập nhật Avatar URL riêng lẻ
     */
    public void updateAvatarUrl(String avatarUrl) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_AVATAR_URL, avatarUrl);
        editor.apply();
    }

    /**
     * Cập nhật tên hiển thị riêng lẻ
     */
    public void updateName(String name) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_NAME, name);
        editor.apply();
    }
}