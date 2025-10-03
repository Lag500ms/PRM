package com.example.myapplication.network;

import android.content.Context;
import android.content.SharedPreferences;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

/**
 * OkHttp interceptor that attaches the JWT access token to every outgoing
 * request (except the login call). This ensures all dealer-protected
 * endpoints are authenticated without changing the existing auth flow.
 *
 * Why needed:
 * - Backend dealer APIs require Authorization: Bearer {token}
 * - Centralizing header injection avoids duplicating code across
 * repositories/services
 * - Skips adding the header for /v1/auth/login to prevent sending stale/empty
 * tokens
 */
public class AuthInterceptor implements Interceptor {

    private final Context context;
    private static final String PREF_NAME = "MyAppPrefs";
    private static final String TOKEN_KEY = "token";

    public AuthInterceptor(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        // Skip adding auth header for login calls
        if (original.url().encodedPath().contains("/v1/auth/login")) {
            return chain.proceed(original);
        }
        SharedPreferences sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String token = sp.getString(TOKEN_KEY, null);
        if (token == null || token.isEmpty()) {
            return chain.proceed(original);
        }
        Request authed = original.newBuilder()
                .addHeader("Authorization", "Bearer " + token)
                .build();
        return chain.proceed(authed);
    }
}
