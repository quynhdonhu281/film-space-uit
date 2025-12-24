package com.example.filmspace_mobile.data.api;

import android.content.Context;

import com.example.filmspace_mobile.data.local.UserSessionManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private final UserSessionManager sessionManager;

    public AuthInterceptor(Context context) {
        this.sessionManager = new UserSessionManager(context);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        // Add token to request if user is logged in
        Request.Builder requestBuilder = originalRequest.newBuilder();
        if (sessionManager.isLoggedIn()) {
            String token = sessionManager.getToken();
            if (token != null && !token.isEmpty()) {
                requestBuilder.header("Authorization", "Bearer " + token);
            }
        }

        Request request = requestBuilder.build();
        Response response = chain.proceed(request);

        // Handle unauthorized responses (token expired or invalid)
        if (response.code() == 401) {
            // Check if this is a login/register endpoint (don't clear session for these)
            String path = originalRequest.url().encodedPath();
            boolean isAuthEndpoint = path.contains("/login") || 
                                     path.contains("/register") || 
                                     path.contains("/verify-otp") ||
                                     path.contains("/forgot-password") ||
                                     path.contains("/reset-password");
            
            if (!isAuthEndpoint && sessionManager.isLoggedIn()) {
                // Clear session only for protected endpoints with 401
                sessionManager.clearSession();
            }
        }
        
        // Check if account was deleted (403 Forbidden)
        if (response.code() == 403 && sessionManager.isLoggedIn()) {
            sessionManager.clearSession();
        }

        return response;
    }
}
