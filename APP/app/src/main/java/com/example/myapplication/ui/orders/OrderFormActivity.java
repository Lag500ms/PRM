package com.example.myapplication.ui.orders;


import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.example.myapplication.model.account.response.AccountResponseDTO;
import com.example.myapplication.model.account.response.AccountResponsePageDTO;
import com.example.myapplication.model.orders.CreateOrderRequest;
import com.example.myapplication.model.orders.OrderResponse;
import com.example.myapplication.model.orders.UpdateOrderRequest;
import com.example.myapplication.network.AccountApiService;
import com.example.myapplication.network.RetrofitClient;
import com.example.myapplication.repository.OrdersRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderFormActivity extends AppCompatActivity {

    public static final String EXTRA_ORDER_ID = "order_id";

    private AutoCompleteTextView actvCustomer;
    private TextInputEditText etPhone, etAddress, etTotal;
    private MaterialButton btnSave;
    private ProgressBar progressBar;
    private OrdersRepository ordersRepository;
    private AccountApiService accountApiService;
    private String orderId;

    // Store customer data
    private List<AccountResponseDTO> customerList = new ArrayList<>();
    private Map<String, AccountResponseDTO> customerMap = new HashMap<>();
    private String selectedCustomerFullName = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_form);

        com.google.android.material.appbar.MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        ordersRepository = new OrdersRepository(this);
        accountApiService = RetrofitClient.create(AccountApiService.class);

        actvCustomer = findViewById(R.id.actvCustomer);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        etTotal = findViewById(R.id.etTotal);
        btnSave = findViewById(R.id.btnSave);
        progressBar = findViewById(R.id.progressBar);

        // Load customer list from API
        loadCustomers();

        orderId = getIntent().getStringExtra(EXTRA_ORDER_ID);
        if (orderId != null) {
            loadOrder(orderId);
        }

        // Setup customer selection listener
        actvCustomer.setOnItemClickListener((parent, view, position, id) -> {
            String selectedName = (String) parent.getItemAtPosition(position);
            AccountResponseDTO selectedCustomer = customerMap.get(selectedName);

            if (selectedCustomer != null) {
                selectedCustomerFullName = selectedCustomer.getDetails() != null
                    ? selectedCustomer.getDetails().getFullName()
                    : selectedCustomer.getUsername();

                // Auto-fill phone and address
                String phone = selectedCustomer.getDetails() != null
                    ? selectedCustomer.getDetails().getPhone()
                    : "";
                String address = selectedCustomer.getDetails() != null
                    ? selectedCustomer.getDetails().getAddress()
                    : "";

                etPhone.setText(phone);
                etAddress.setText(address);
            }
        });

        btnSave.setOnClickListener(v -> save());
    }

    private void loadCustomers() {
        progressBar.setVisibility(View.VISIBLE);

        // Call searchAccountsPublic with keyword "customer"
        accountApiService.searchAccountsPublic("customer", 0, 100).enqueue(new Callback<AccountResponsePageDTO>() {
            @Override
            public void onResponse(Call<AccountResponsePageDTO> call, Response<AccountResponsePageDTO> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    customerList = response.body().getContent();

                    // Create list of customer names for dropdown
                    List<String> customerDisplayNames = new ArrayList<>();
                    customerMap.clear();

                    for (AccountResponseDTO customer : customerList) {
                        String fullName = customer.getDetails() != null && customer.getDetails().getFullName() != null
                            ? customer.getDetails().getFullName()
                            : customer.getUsername();

                        // Display format: "FullName (username)" to avoid duplicate names
                        String displayName = fullName + " (" + customer.getUsername() + ")";

                        customerDisplayNames.add(displayName);
                        customerMap.put(displayName, customer);
                    }

                    // Setup AutoCompleteTextView adapter
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        OrderFormActivity.this,
                        android.R.layout.simple_dropdown_item_1line,
                        customerDisplayNames
                    );
                    actvCustomer.setAdapter(adapter);

                    android.util.Log.d("OrderFormActivity", "Loaded " + customerDisplayNames.size() + " customers");
                } else {
                    Toast.makeText(OrderFormActivity.this, "Failed to load customers", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AccountResponsePageDTO> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(OrderFormActivity.this, "Error loading customers: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                android.util.Log.e("OrderFormActivity", "Failed to load customers", t);
            }
        });
    }

    private void loadOrder(String id) {
        progressBar.setVisibility(View.VISIBLE);
        ordersRepository.getById(id, new OrdersRepository.OrderDetailCallback() {
            @Override
            public void onSuccess(OrderResponse o) {
                progressBar.setVisibility(View.GONE);
                if (o != null) {
                    actvCustomer.setText(o.customer, false);
                    selectedCustomerFullName = o.customer;
                    etPhone.setText(o.phone);
                    etAddress.setText(o.address);
                    etTotal.setText(o.totalPrice != null ? o.totalPrice.toPlainString() : "");
                }
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(OrderFormActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void save() {
        String customer = actvCustomer.getText() != null ? actvCustomer.getText().toString().trim() : "";
        String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
        String address = etAddress.getText() != null ? etAddress.getText().toString().trim() : "";
        String totalStr = etTotal.getText() != null ? etTotal.getText().toString().trim() : "";

        if (customer.isEmpty() || phone.isEmpty() || address.isEmpty() || totalStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        BigDecimal total;
        try {
            total = new BigDecimal(totalStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid total price", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        OrdersRepository.OrderDetailCallback callback = new OrdersRepository.OrderDetailCallback() {
            @Override
            public void onSuccess(OrderResponse response) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(OrderFormActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);
                Toast.makeText(OrderFormActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
            }
        };

        if (orderId == null) {
            CreateOrderRequest req = new CreateOrderRequest();
            req.customer = selectedCustomerFullName != null ? selectedCustomerFullName : customer;
            req.phone = phone;
            req.address = address;
            req.totalPrice = total;
            ordersRepository.create(req, callback);
        } else {
            UpdateOrderRequest req = new UpdateOrderRequest();
            req.id = orderId;
            req.customer = selectedCustomerFullName != null ? selectedCustomerFullName : customer;
            req.phone = phone;
            req.address = address;
            req.totalPrice = total;
            ordersRepository.update(req, callback);
        }
    }
}
