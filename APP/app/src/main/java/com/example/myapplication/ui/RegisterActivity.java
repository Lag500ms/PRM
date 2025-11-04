package com.example.myapplication.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.example.myapplication.model.account.request.RegisterRequestDTO;
import com.example.myapplication.network.AccountApiService;
import com.example.myapplication.network.RetrofitClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Màn hình đăng ký tài khoản công khai
 * Tạo account với trạng thái inactive (chờ admin approve)
 */
public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etUsername, etEmail, etPassword, etConfirmPassword;
    private MaterialButton btnRegister;
    private ProgressBar progressBar;
    private AccountApiService accountApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        // Khởi tạo API service (không cần auth cho register)
        accountApiService = RetrofitClient.create(AccountApiService.class);

        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);

        // Back button trong toolbar
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Login link
        findViewById(R.id.tvLogin).setOnClickListener(v -> finish());

        // Nút Register
        btnRegister.setOnClickListener(v -> register());
    }

    private void register() {
        String username = etUsername.getText() != null ? etUsername.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString() : "";
        String confirmPassword = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString() : "";

        // Validation
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Disable button và show progress
        progressBar.setVisibility(View.VISIBLE);
        btnRegister.setEnabled(false);

        // Tạo RegisterRequestDTO
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setUsername(username);
        request.setEmail(email);
        request.setPassword(password);
        request.setCreatedByAdmin(false); // Public register, không phải admin tạo

        // Gọi API register
        accountApiService.register(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progressBar.setVisibility(View.GONE);
                btnRegister.setEnabled(true);

                if (response.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, 
                        "Registration successful! Please wait for admin approval.", 
                        Toast.LENGTH_LONG).show();
                    finish(); // Quay về LoginActivity
                } else {
                    String errorMsg = "Registration failed";
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            if (errorBody.contains("username") || errorBody.contains("Username")) {
                                errorMsg = "Username already exists";
                            } else if (errorBody.contains("email") || errorBody.contains("Email")) {
                                errorMsg = "Email already exists";
                            }
                        }
                    } catch (Exception e) {
                        errorMsg = "Registration failed: " + response.code();
                    }
                    Toast.makeText(RegisterActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnRegister.setEnabled(true);
                Toast.makeText(RegisterActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}

