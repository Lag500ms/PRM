package com.example.myapplication.ui.customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.myapplication.R;
import com.example.myapplication.repository.CustomerRepository;
import com.google.android.material.button.MaterialButton;

public class CustomerDetailActivity extends AppCompatActivity {

    private static final int EDIT_CUSTOMER_REQUEST = 1001;

    private TextView tvCustomerName, tvPhone, tvAddress, tvEmail;
    private MaterialButton btnEdit, btnDelete;
    private ProgressBar progressBar;
    private Toolbar toolbar;
    private CustomerRepository customerRepository;

    private String customerId;
    private String customerName;
    private String customerPhone;
    private String customerAddress;
    private String customerEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_detail);

        // Initialize repository
        customerRepository = new CustomerRepository(this);

        // Get customer data from intent
        Intent intent = getIntent();
        customerId = intent.getStringExtra("CUSTOMER_ID");
        customerName = intent.getStringExtra("CUSTOMER_NAME");
        customerPhone = intent.getStringExtra("CUSTOMER_PHONE");
        customerAddress = intent.getStringExtra("CUSTOMER_ADDRESS");
        customerEmail = intent.getStringExtra("CUSTOMER_EMAIL");

        initViews();
        setupToolbar();
        displayCustomerInfo();
        setupListeners();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvPhone = findViewById(R.id.tvPhone);
        tvAddress = findViewById(R.id.tvAddress);
        tvEmail = findViewById(R.id.tvEmail);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void displayCustomerInfo() {
        tvCustomerName.setText(customerName != null ? customerName : "N/A");
        tvPhone.setText(customerPhone != null ? customerPhone : "No phone");
        tvAddress.setText(customerAddress != null ? customerAddress : "No address");
        tvEmail.setText(customerEmail != null ? customerEmail : "No email");
    }

    private void setupListeners() {
        btnEdit.setOnClickListener(v -> {
            // Open CustomerFormActivity in edit mode
            Intent intent = new Intent(CustomerDetailActivity.this, CustomerFormActivity.class);
            intent.putExtra("CUSTOMER_ID", customerId);
            intent.putExtra("CUSTOMER_NAME", customerName);
            intent.putExtra("CUSTOMER_EMAIL", customerEmail);
            intent.putExtra("CUSTOMER_PHONE", customerPhone);
            intent.putExtra("CUSTOMER_ADDRESS", customerAddress);
            startActivityForResult(intent, EDIT_CUSTOMER_REQUEST);
        });

        btnDelete.setOnClickListener(v -> {
            showDeleteConfirmDialog();
        });
    }

    private void showDeleteConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Customer")
                .setMessage("Are you sure you want to delete " + customerName + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteCustomer();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteCustomer() {
        if (customerId == null || customerId.isEmpty()) {
            Toast.makeText(this, "Cannot delete: Customer ID is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show progress
        progressBar.setVisibility(View.VISIBLE);
        btnEdit.setEnabled(false);
        btnDelete.setEnabled(false);

        android.util.Log.d("CustomerDetailActivity", "Deleting customer with ID: " + customerId);

        // Call repository to delete customer
        customerRepository.deleteCustomer(customerId, new CustomerRepository.CustomerCallback() {
            @Override
            public void onSuccess() {
                android.util.Log.d("CustomerDetailActivity", "Customer deleted successfully!");
                progressBar.setVisibility(View.GONE);
                Toast.makeText(CustomerDetailActivity.this,
                    "Customer deleted successfully!",
                    Toast.LENGTH_SHORT).show();
                // Return to previous screen with result
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onError(String error) {
                android.util.Log.e("CustomerDetailActivity", "Failed to delete customer: " + error);
                progressBar.setVisibility(View.GONE);
                btnEdit.setEnabled(true);
                btnDelete.setEnabled(true);
                Toast.makeText(CustomerDetailActivity.this,
                    "Failed to delete: " + error,
                    Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_CUSTOMER_REQUEST && resultCode == RESULT_OK) {
            // Customer was updated successfully, return to previous screen to refresh
            Toast.makeText(this, "Please refresh the customer list", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
