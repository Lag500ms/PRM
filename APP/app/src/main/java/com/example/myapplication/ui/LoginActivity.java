package com.example.myapplication.ui;


import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.model.account.response.LoginResponse;
import com.example.myapplication.repository.AuthRepository;
import com.example.myapplication.utils.SharedPrefManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        authRepository = new AuthRepository();
        TextInputEditText etEmail = findViewById(R.id.etEmail);
        TextInputEditText etPassword = findViewById(R.id.etPassword);
        MaterialButton btnSignIn = findViewById(R.id.btnSignIn);

        // Auto redirect if already logged in
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            navigateByRole(SharedPrefManager.getInstance(this).getRole());
            finish();
            return;
        }

        btnSignIn.setOnClickListener(v -> {
            String username = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
            String password = etPassword.getText() != null ? etPassword.getText().toString() : "";
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter username and password", Toast.LENGTH_SHORT).show();
                return;
            }

            authRepository.login(username, password, new AuthRepository.LoginCallback() {
                @Override
                public void onSuccess(LoginResponse response) {
                    String role = response.getRole();
                    SharedPrefManager.getInstance(LoginActivity.this)
                            .saveUser(response.getToken(), response.getUsername(), role);
                    Toast.makeText(LoginActivity.this, "Login success as " + role, Toast.LENGTH_SHORT).show();
                    navigateByRole(role);
                    finish();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(LoginActivity.this, "Login failed: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void navigateByRole(String role) {
        if (role == null) {
            return;
        }
        // BE returns "ROLE_ADMIN" or "ROLE_DEALER" from Spring Security
        if ("ROLE_ADMIN".equalsIgnoreCase(role) || "ADMIN".equalsIgnoreCase(role)) {
            startActivity(new Intent(this, DashboardAdminActivity.class));
        } else {
            startActivity(new Intent(this, DashboardDealerActivity.class));
        }
    }
}
