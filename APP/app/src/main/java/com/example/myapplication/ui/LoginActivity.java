package com.example.myapplication.ui;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.model.response.LoginResponse;
import com.example.myapplication.repository.AuthRepository;

public class LoginActivity extends AppCompatActivity {

    private AuthRepository authRepository;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "MyAppPrefs";
    private static final String TOKEN_KEY = "token";
    private static final String USERNAME_KEY = "username";
    private static final String ROLE_KEY = "role";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        authRepository = new AuthRepository();
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

//        EditText etUsername = findViewById(R.id.etUsername);
//        EditText etPassword = findViewById(R.id.etPassword);
//        Button btnLogin = findViewById(R.id.btnLogin);

//        btnLogin.setOnClickListener(v -> {
//            String username = etUsername.getText().toString();
//            String password = etPassword.getText().toString();
//
//            authRepository.login(username, password, new AuthRepository.LoginCallback() {
//                @Override
//                public void onSuccess(LoginResponse response) {
//                    // Lưu token, username, role vào SharedPreferences
//                    sharedPreferences.edit()
//                            .putString(TOKEN_KEY, response.getToken())
//                            .putString(USERNAME_KEY, response.getUsername())
//                            .putString(ROLE_KEY, response.getRole())
//                            .apply();
//
//                    Toast.makeText(LoginActivity.this,
//                            "Login success! Data saved.",
//                            Toast.LENGTH_LONG).show();
//
//                    // TODO: chuyển sang màn hình khác
//                    // startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
//                    // finish();
//                }
//
//                @Override
//                public void onError(String error) {
//                    Toast.makeText(LoginActivity.this,
//                            "Login failed: " + error,
//                            Toast.LENGTH_SHORT).show();
//                }
//            });
//        });
    }

    // Hàm tiện lợi lấy token/username/role ở các Activity khác
    public static String getToken(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getString(TOKEN_KEY, null);
    }

    public static String getUsername(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getString(USERNAME_KEY, null);
    }

    public static String getRole(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getString(ROLE_KEY, null);
    }
}
