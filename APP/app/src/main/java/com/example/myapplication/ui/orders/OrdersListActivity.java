package com.example.myapplication.ui.orders;

import android.os.Bundle;
import android.util.Log;
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
import com.example.myapplication.model.common.PageResponse;
import com.example.myapplication.model.orders.OrderResponse;
import com.example.myapplication.repository.OrdersRepository;
import com.example.myapplication.ui.orders.adapter.OrdersAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import java.util.ArrayList;
import java.util.List;
import android.content.Intent;

public class OrdersListActivity extends AppCompatActivity {

    private RecyclerView rvOrders;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private FloatingActionButton fabAddOrder;
    private AutoCompleteTextView statusFilterDropdown;
    private OrdersRepository ordersRepository;
    private OrdersAdapter adapter;
    private List<OrderResponse> orders = new ArrayList<>();
    private int currentPage = 0;
    private final int pageSize = 10;
    private String selectedStatus = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_list);

        ordersRepository = new OrdersRepository(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initViews();
        setupStatusFilter();
        setupRecyclerView();
        loadOrders();

        fabAddOrder.setOnClickListener(v -> {
            startActivity(new Intent(this, OrderFormActivity.class));
        });
    }

    private void initViews() {
        rvOrders = findViewById(R.id.rvOrders);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);
        fabAddOrder = findViewById(R.id.fabAddOrder);
        statusFilterDropdown = findViewById(R.id.statusFilterDropdown);
    }

    private void setupStatusFilter() {
        String[] statusOptions = {"All", "PENDING", "CONFIRMED", "COMPLETED", "CANCELLED"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, statusOptions);
        statusFilterDropdown.setAdapter(adapter);
        statusFilterDropdown.setText("All", false);

        statusFilterDropdown.setOnItemClickListener((parent, view, position, id) -> {
            String selected = statusOptions[position];
            selectedStatus = selected.equals("All") ? null : selected;
            loadOrders();
        });
    }

    private void setupRecyclerView() {
        adapter = new OrdersAdapter(this, orders, order -> {
            Intent i = new Intent(this, OrderDetailActivity.class);
            i.putExtra(OrderDetailActivity.EXTRA_ORDER_ID, order.id);
            startActivity(i);
        });
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        rvOrders.setAdapter(adapter);
    }

    private void loadOrders() {
        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);

        ordersRepository.list(currentPage, pageSize, selectedStatus, new OrdersRepository.OrdersListCallback() {
            @Override
            public void onSuccess(PageResponse<OrderResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response != null && response.content != null) {
                    orders.clear();
                    orders.addAll(response.content);
                    adapter.notifyDataSetChanged();
                    if (orders.isEmpty()) {
                        tvEmpty.setVisibility(View.VISIBLE);
                    }
                } else {
                    tvEmpty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(OrdersListActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                tvEmpty.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh list when coming back from detail screen
        loadOrders();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
