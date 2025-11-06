package com.example.myapplication.ui.customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.model.account.response.AccountResponseDTO;
import com.example.myapplication.repository.CustomerRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class CustomerManagementActivity extends AppCompatActivity {

    private RecyclerView rvCustomers;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private AutoCompleteTextView statusFilterDropdown;
    private CustomerRepository customerRepository;
    private CustomerAdapter adapter;
    private final List<CustomerInfo> customers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_management);

        customerRepository = new CustomerRepository(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initViews();
        setupStatusFilter();
        setupRecyclerView();
        loadCustomers();
    }

    private void initViews() {
        rvCustomers = findViewById(R.id.rvCustomers);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);
        statusFilterDropdown = findViewById(R.id.statusFilterDropdown);

        FloatingActionButton fabAddCustomer = findViewById(R.id.fabAddCustomer);
        fabAddCustomer.setOnClickListener(v -> {
            startActivity(new Intent(this, CustomerFormActivity.class));
        });
    }

    private void setupStatusFilter() {
        String[] statusOptions = {"All", "Active", "Inactive"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, statusOptions);
        statusFilterDropdown.setAdapter(adapter);
        statusFilterDropdown.setText("All", false);

        statusFilterDropdown.setOnItemClickListener((parent, view, position, id) -> {
            filterCustomers();
        });
    }

    private void setupRecyclerView() {
        adapter = new CustomerAdapter(customers);
        rvCustomers.setAdapter(adapter);
        rvCustomers.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCustomers();
    }

    private void loadCustomers() {
        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);

        customerRepository.searchCustomers(0, 100, new CustomerRepository.CustomerSearchCallback() {
            @Override
            public void onSuccess(com.example.myapplication.model.account.response.AccountResponsePageDTO response) {
                progressBar.setVisibility(View.GONE);
                customers.clear();

                // Convert AccountResponseDTO to CustomerInfo
                for (AccountResponseDTO account : response.getContent()) {
                    CustomerInfo customer = new CustomerInfo();
                    customer.id = account.getId();  // Set customer ID
                    customer.name = account.getDetails() != null && account.getDetails().getFullName() != null
                        ? account.getDetails().getFullName()
                        : account.getUsername();
                    customer.phone = account.getDetails() != null ? account.getDetails().getPhoneNumber() : "";
                    customer.address = account.getDetails() != null ? account.getDetails().getAddress() : "";
                    customer.email = account.getEmail();
                    customer.isActive = account.isActive();
                    customers.add(customer);
                }

                filterCustomers();
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(CustomerManagementActivity.this, "Failed to load customers: " + error, Toast.LENGTH_SHORT).show();
                tvEmpty.setVisibility(View.VISIBLE);
            }
        });
    }

    private void filterCustomers() {
        String selectedStatus = statusFilterDropdown.getText().toString();
        List<CustomerInfo> filteredList = new ArrayList<>();

        for (CustomerInfo customer : customers) {
            if (selectedStatus.equals("All")) {
                filteredList.add(customer);
            } else if (selectedStatus.equals("Active") && customer.isActive) {
                filteredList.add(customer);
            } else if (selectedStatus.equals("Inactive") && !customer.isActive) {
                filteredList.add(customer);
            }
        }

        adapter.updateList(filteredList);

        if (filteredList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public static class CustomerInfo {
        public String id;  // Add customer ID field
        public String name;
        public String phone;
        public String address;
        public String email;
        public boolean isActive;
    }
}
