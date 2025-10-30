package com.example.myapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {

    private static final String PREF_NAME = "AuthPrefs";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_ROLE = "role";
    private static final String KEY_USER_ID = "user_id";

    private static SharedPrefManager instance;
    private final SharedPreferences prefs;

    private SharedPrefManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefManager(context.getApplicationContext());
        }
        return instance;
    }

    // 🔹 Lưu token và thông tin user (sau khi login)
    public void saveUser(String token, String username, String role) {
        prefs.edit()
                .putString(KEY_TOKEN, token)
                .putString(KEY_USERNAME, username)
                .putString(KEY_ROLE, role)
                .apply();
    }

    // 🔹 Lưu với userId
    public void saveUser(String token, String username, String role, String userId) {
        prefs.edit()
                .putString(KEY_TOKEN, token)
                .putString(KEY_USERNAME, username)
                .putString(KEY_ROLE, role)
                .putString(KEY_USER_ID, userId)
                .apply();
    }

    // 🔹 Lấy token
    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    // 🔹 Lấy username
    public String getUsername() {
        return prefs.getString(KEY_USERNAME, null);
    }

    // 🔹 Lấy role
    public String getRole() {
        return prefs.getString(KEY_ROLE, null);
    }

    // 🔹 Lấy userId
    public String getUserId() {
        return prefs.getString(KEY_USER_ID, null);
    }

    // 🔹 Kiểm tra đã đăng nhập chưa
    public boolean isLoggedIn() {
        return getToken() != null;
    }

    // 🔹 Xóa toàn bộ dữ liệu khi logout
    public void clear() {
        prefs.edit().clear().apply();
    }
}
