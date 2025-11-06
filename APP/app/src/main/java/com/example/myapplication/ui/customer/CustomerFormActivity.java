package com.example.myapplication.ui.customer;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.myapplication.R;
import com.example.myapplication.model.customer.CreateCustomerRequest;
import com.example.myapplication.repository.CustomerRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class CustomerFormActivity extends AppCompatActivity {

    private TextInputEditText etUserName, etEmail, etPhone, etAddress;
    private MaterialButton btnSave;
    private ProgressBar progressBar;
    private Toolbar toolbar;
    private CustomerRepository customerRepository;

    // Edit mode fields
    private boolean isEditMode = false;
    private String customerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_form);

        // Khởi tạo repository
        customerRepository = new CustomerRepository(this);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initViews();
        checkEditMode();
        setupListeners();
    }

    private void initViews() {
        etUserName = findViewById(R.id.etUserName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        btnSave = findViewById(R.id.btnSave);
        progressBar = findViewById(R.id.progressBar);
    }

    private void checkEditMode() {
        // Check if we're in edit mode
        customerId = getIntent().getStringExtra("CUSTOMER_ID");
        isEditMode = customerId != null;

        if (isEditMode) {
            // Set title for edit mode
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Edit Customer");
            }

            // Pre-fill form with customer data
            String name = getIntent().getStringExtra("CUSTOMER_NAME");
            String email = getIntent().getStringExtra("CUSTOMER_EMAIL");
            String phone = getIntent().getStringExtra("CUSTOMER_PHONE");
            String address = getIntent().getStringExtra("CUSTOMER_ADDRESS");

            if (etUserName != null) etUserName.setText(name);
            if (etEmail != null) etEmail.setText(email);
            if (etPhone != null) etPhone.setText(phone);
            if (etAddress != null) etAddress.setText(address);

            // Disable email field in edit mode (email should not be changed)
            if (etEmail != null) {
                etEmail.setEnabled(false);
                etEmail.setFocusable(false);
            }
        } else {
            // Create mode
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Add Customer");
            }
        }
    }

    private void setupListeners() {
        btnSave.setOnClickListener(v -> {
            if (isEditMode) {
                updateCustomer();
            } else {
                saveCustomer();
            }
        });
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void updateCustomer() {
        String fullName = etUserName.getText() != null ? etUserName.getText().toString().trim() : "";
        String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
        String address = etAddress.getText() != null ? etAddress.getText().toString().trim() : "";

        android.util.Log.d("CustomerFormActivity", "Updating customer - ID: " + customerId + ", FullName: " + fullName);

        // Validate
        if (fullName.isEmpty()) {
            etUserName.setError("Full name is required");
            etUserName.requestFocus();
            return;
        }

        if (phone.isEmpty()) {
            etPhone.setError("Phone is required");
            etPhone.requestFocus();
            return;
        }

        if (address.isEmpty()) {
            etAddress.setError("Address is required");
            etAddress.requestFocus();
            return;
        }

        // Show progress
        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        android.util.Log.d("CustomerFormActivity", "Calling updateCustomer...");

        // Call repository to update customer
        customerRepository.updateCustomer(customerId, fullName, phone, address, new CustomerRepository.CustomerCallback() {
            @Override
            public void onSuccess() {
                android.util.Log.d("CustomerFormActivity", "Customer updated successfully!");
                progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);
                Toast.makeText(CustomerFormActivity.this,
                    "Customer updated successfully!",
                    Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish(); // Return to previous screen
            }

            @Override
            public void onError(String error) {
                android.util.Log.e("CustomerFormActivity", "Failed to update customer: " + error);
                progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);
                Toast.makeText(CustomerFormActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveCustomer() {
        String fullName = etUserName.getText() != null ? etUserName.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
        String address = etAddress.getText() != null ? etAddress.getText().toString().trim() : "";

        android.util.Log.d("CustomerFormActivity", "Saving customer - FullName: " + fullName + ", Email: " + email);

        // Validate
        if (fullName.isEmpty()) {
            etUserName.setError("Full name is required");
            etUserName.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Invalid email format");
            etEmail.requestFocus();
            return;
        }

        if (phone.isEmpty()) {
            etPhone.setError("Phone is required");
            etPhone.requestFocus();
            return;
        }

        if (address.isEmpty()) {
            etAddress.setError("Address is required");
            etAddress.requestFocus();
            return;
        }

        // Show progress
        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        // Tạo CreateCustomerRequest
        CreateCustomerRequest request = new CreateCustomerRequest();
        request.username = "Customer"; // Hardcode
        request.password = "1"; // Hardcode
        request.email = email;
        request.fullName = fullName;
        request.phone = phone;
        request.address = address;
        request.createdByAdmin = false;

        android.util.Log.d("CustomerFormActivity", "Request created, calling repository...");

        // Gọi repository để tạo customer
        customerRepository.createCustomer(request, new CustomerRepository.CustomerCallback() {
            @Override
            public void onSuccess() {
                android.util.Log.d("CustomerFormActivity", "Customer created successfully!");
                progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);
                Toast.makeText(CustomerFormActivity.this,
                    "Customer created successfully!",
                    Toast.LENGTH_SHORT).show();
                finish(); // Quay về CustomerManagementActivity
            }

            @Override
            public void onError(String error) {
                android.util.Log.e("CustomerFormActivity", "Failed to create customer: " + error);
                progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);
                Toast.makeText(CustomerFormActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
