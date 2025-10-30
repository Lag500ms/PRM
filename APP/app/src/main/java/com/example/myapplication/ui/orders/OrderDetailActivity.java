package com.example.myapplication.ui.orders;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.example.myapplication.model.orders.OrderResponse;
import com.example.myapplication.model.orders.UpdateOrderRequest;
import com.example.myapplication.model.orders.UpdateStatusRequest;
import com.example.myapplication.repository.OrdersRepository;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

public class OrderDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ORDER_ID = "order_id";

    private TextView tvCustomer, tvPhone, tvAddress, tvTotal, tvStatus;
    private MaterialButton btnEdit, btnDelete, btnApprove, btnCancel;
    private ProgressBar progressBar;
    private OrdersRepository ordersRepository;
    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        // Setup toolbar with back button
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        ordersRepository = new OrdersRepository(this);
        tvCustomer = findViewById(R.id.tvCustomer);
        tvPhone = findViewById(R.id.tvPhone);
        tvAddress = findViewById(R.id.tvAddress);
        tvTotal = findViewById(R.id.tvTotal);
        tvStatus = findViewById(R.id.tvStatus);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);
        btnApprove = findViewById(R.id.btnApprove);
        btnCancel = findViewById(R.id.btnCancel);
        progressBar = findViewById(R.id.progressBar);

        orderId = getIntent().getStringExtra(EXTRA_ORDER_ID);
        load();

        btnEdit.setOnClickListener(v -> {
            Intent i = new Intent(this, OrderFormActivity.class);
            i.putExtra(OrderFormActivity.EXTRA_ORDER_ID, orderId);
            startActivity(i);
        });

        btnDelete.setOnClickListener(v -> delete());
        btnApprove.setOnClickListener(v -> updateStatus("CONFIRMED"));
        btnCancel.setOnClickListener(v -> updateStatus("CANCELLED"));

        findViewById(R.id.btnEdit).setOnClickListener(v -> editOrder());
        findViewById(R.id.btnDelete).setOnClickListener(v -> deleteOrder());
    }

    private void editOrder() {
        Intent intent = new Intent(this, OrderFormActivity.class);
        intent.putExtra("order_id", orderId);
        startActivity(intent);
        finish();
    }

    private void deleteOrder() {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Delete Order")
                .setMessage("Are you sure you want to delete this order?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    ordersRepository.delete(orderId, new OrdersRepository.DeleteCallback() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(OrderDetailActivity.this, "Order deleted", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Toast.makeText(OrderDetailActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // refresh after edit
        load();
    }

    private void load() {
        progressBar.setVisibility(View.VISIBLE);
        ordersRepository.getById(orderId, new OrdersRepository.OrderDetailCallback() {
            @Override
            public void onSuccess(OrderResponse o) {
                progressBar.setVisibility(View.GONE);
                if (o != null) {
                    tvCustomer.setText(o.customer);
                    tvPhone.setText(o.phone);
                    tvAddress.setText(o.address);
                    tvTotal.setText(o.totalPrice != null ? o.totalPrice.toPlainString() : "");
                    tvStatus.setText(o.status);
                }
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(OrderDetailActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateStatus(String newStatus) {
        progressBar.setVisibility(View.VISIBLE);
        UpdateStatusRequest req = new UpdateStatusRequest();
        req.id = orderId;
        req.status = newStatus;
        ordersRepository.updateStatus(req, new OrdersRepository.OrderDetailCallback() {
            @Override
            public void onSuccess(OrderResponse response) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(OrderDetailActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                load();
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(OrderDetailActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void delete() {
        progressBar.setVisibility(View.VISIBLE);
        ordersRepository.delete(orderId, new OrdersRepository.DeleteCallback() {
            @Override
            public void onSuccess() {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(OrderDetailActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(OrderDetailActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }
}


