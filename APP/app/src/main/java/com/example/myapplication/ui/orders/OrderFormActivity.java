package com.example.myapplication.ui.orders;


import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.example.myapplication.model.orders.CreateOrderRequest;
import com.example.myapplication.model.orders.OrderResponse;
import com.example.myapplication.model.orders.UpdateOrderRequest;
import com.example.myapplication.repository.OrdersRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.math.BigDecimal;

public class OrderFormActivity extends AppCompatActivity {

    public static final String EXTRA_ORDER_ID = "order_id";

    private TextInputEditText etCustomer, etPhone, etAddress, etTotal;
    private MaterialButton btnSave;
    private ProgressBar progressBar;
    private OrdersRepository ordersRepository;
    private String orderId;

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
        etCustomer = findViewById(R.id.etCustomer);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        etTotal = findViewById(R.id.etTotal);
        btnSave = findViewById(R.id.btnSave);
        progressBar = findViewById(R.id.progressBar);

        orderId = getIntent().getStringExtra(EXTRA_ORDER_ID);
        if (orderId != null) {
            loadOrder(orderId);
        }

        btnSave.setOnClickListener(v -> save());
    }

    private void loadOrder(String id) {
        progressBar.setVisibility(View.VISIBLE);
        ordersRepository.getById(id, new OrdersRepository.OrderDetailCallback() {
            @Override
            public void onSuccess(OrderResponse o) {
                progressBar.setVisibility(View.GONE);
                if (o != null) {
                    etCustomer.setText(o.customer);
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
        String customer = etCustomer.getText() != null ? etCustomer.getText().toString().trim() : "";
        String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
        String address = etAddress.getText() != null ? etAddress.getText().toString().trim() : "";
        String totalStr = etTotal.getText() != null ? etTotal.getText().toString().trim() : "";

        if (customer.isEmpty() || phone.isEmpty() || address.isEmpty() || totalStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        BigDecimal total = new BigDecimal(totalStr);
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
            req.customer = customer;
            req.phone = phone;
            req.address = address;
            req.totalPrice = total;
            ordersRepository.create(req, callback);
        } else {
            UpdateOrderRequest req = new UpdateOrderRequest();
            req.id = orderId;
            req.customer = customer;
            req.phone = phone;
            req.address = address;
            req.totalPrice = total;
            ordersRepository.update(req, callback);
        }
    }
}


