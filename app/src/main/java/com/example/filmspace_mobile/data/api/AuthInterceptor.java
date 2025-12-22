package com.example.filmspace_mobile.data.api;

import android.content.Context;

import com.example.filmspace_mobile.data.local.UserSessionManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private Context context;

    public AuthInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        UserSessionManager sessionManager = new UserSessionManager(context);

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

        // Check if user account is deleted or unauthorized
        if (response.code() == 401 || response.code() == 403) {
            // Clear session - user is unauthorized (possibly deleted)
            sessionManager.clearSession();
        }

        return response;
    }
}
