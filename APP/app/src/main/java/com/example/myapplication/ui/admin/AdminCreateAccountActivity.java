package com.example.myapplication.ui.admin;


import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.example.myapplication.model.account.request.RegisterRequestDTO;
import com.example.myapplication.repository.AdminAccountsRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

/**
 * AdminCreateAccountActivity - Màn hình tạo dealer account mới (CREATE)
 * 
 * Chức năng:
 * - Form nhập username, email, password
 * - Bấm Create → gọi API POST /api/v1/accounts/save → tạo dealer account
 * - Thành công → finish() → quay lại ListActivity → onResume() → reload list
 */
public class AdminCreateAccountActivity extends AppCompatActivity {

    private TextInputEditText etUsername, etEmail, etPassword;
    private MaterialButton btnCreate;
    private ProgressBar progressBar;
    private AdminAccountsRepository repo;

    /**
     * onCreate() - Khởi tạo Activity: setup views, setup btnCreate click listener
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_create_account);

        com.google.android.material.appbar.MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        repo = new AdminAccountsRepository(this);
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnCreate = findViewById(R.id.btnCreate);
        progressBar = findViewById(R.id.progressBar);

        btnCreate.setOnClickListener(v -> create());
    }

    /**
     * create() - Tạo dealer account: validate form → build request → gọi API POST → finish()
     */
    private void create() {
        String username = etUsername.getText() != null ? etUsername.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        btnCreate.setEnabled(false);

        RegisterRequestDTO req = new RegisterRequestDTO();
        req.setUsername(username);
        req.setEmail(email);
        req.setPassword(password);
        repo.createDealerByAdmin(req, new AdminAccountsRepository.CreateCallback() {
            @Override
            public void onSuccess() {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AdminCreateAccountActivity.this, "Created", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
                btnCreate.setEnabled(true);
                Toast.makeText(AdminCreateAccountActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }
}


