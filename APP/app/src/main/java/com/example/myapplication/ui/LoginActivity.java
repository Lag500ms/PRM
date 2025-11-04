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
                    String token = response.getToken();
                    String username = response.getUsername();
                    
                    // Fetch account ID then navigate
                    fetchAccountIdThenNavigate(token, username, role);
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(LoginActivity.this, "Login failed: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void fetchAccountIdThenNavigate(String token, String username, String role) {
        // Save token first to use in authenticated API call
        SharedPrefManager.getInstance(this).saveUser(token, username, role);
        
        android.util.Log.d("LoginActivity", "Fetching account ID for username: " + username);
        
        // Fetch account ID
        com.example.myapplication.network.AccountApiService accountApi = 
            com.example.myapplication.network.RetrofitClient.createWithAuth(this, 
                com.example.myapplication.network.AccountApiService.class);
        
        accountApi.getByUsername(username).enqueue(new retrofit2.Callback<com.example.myapplication.model.account.response.AccountResponseDTO>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.myapplication.model.account.response.AccountResponseDTO> call, 
                                 retrofit2.Response<com.example.myapplication.model.account.response.AccountResponseDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String accountId = response.body().getId();
                    android.util.Log.d("LoginActivity", "Account ID fetched: " + accountId);
                    // Save account ID
                    SharedPrefManager.getInstance(LoginActivity.this).saveUserId(accountId);
                    Toast.makeText(LoginActivity.this, "Login success as " + role, Toast.LENGTH_SHORT).show();
                    navigateByRole(role);
                    finish();
                } else {
                    // Log error
                    android.util.Log.e("LoginActivity", "Failed to fetch account ID: " + response.code());
                    try {
                        if (response.errorBody() != null) {
                            android.util.Log.e("LoginActivity", "Error body: " + response.errorBody().string());
                        }
                    } catch (Exception e) {
                        android.util.Log.e("LoginActivity", "Error reading error body", e);
                    }
                    Toast.makeText(LoginActivity.this, "Login success (ID not fetched - code " + response.code() + ")", Toast.LENGTH_LONG).show();
                    navigateByRole(role);
                    finish();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.example.myapplication.model.account.response.AccountResponseDTO> call, Throwable t) {
                android.util.Log.e("LoginActivity", "Network error fetching account ID", t);
                Toast.makeText(LoginActivity.this, "Login success (network error: " + t.getMessage() + ")", Toast.LENGTH_LONG).show();
                navigateByRole(role);
                finish();
            }
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
