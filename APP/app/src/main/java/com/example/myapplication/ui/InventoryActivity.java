package com.example.myapplication.ui.inventory;

import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.model.inventory.InventoryResponse;
import com.example.myapplication.model.inventory.VehicleItem;
import com.example.myapplication.repository.InventoryRepository;
import com.example.myapplication.ui.inventory.adapter.InventoryAdapter;
import java.util.ArrayList;
import java.util.List;

public class InventoryActivity extends AppCompatActivity {

    private RecyclerView rvInventory;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private InventoryRepository inventoryRepository;
    private InventoryAdapter adapter;
    private List<VehicleItem> vehicles = new ArrayList<>();
    private String currentInventoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        inventoryRepository = new InventoryRepository(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initViews();
        setupRecyclerView();
        loadInventory();

        findViewById(R.id.fabReceive).setOnClickListener(v -> {
            Intent i = new Intent(this, InventoryReceiveActivity.class);
            i.putExtra(InventoryReceiveActivity.EXTRA_INVENTORY_ID, currentInventoryId);
            startActivity(i);
        });
    }

    private void initViews() {
        rvInventory = findViewById(R.id.rvInventory);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);
    }

    private void setupRecyclerView() {
        adapter = new InventoryAdapter(vehicles);
        rvInventory.setLayoutManager(new LinearLayoutManager(this));
        rvInventory.setAdapter(adapter);
    }

    private void loadInventory() {
        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);

        inventoryRepository.getInventories(new InventoryRepository.InventoryListCallback() {
            @Override
            public void onSuccess(List<InventoryResponse> inventories) {
                progressBar.setVisibility(View.GONE);
                if (inventories != null && !inventories.isEmpty()) {
                    // Get vehicles from first inventory
                    InventoryResponse first = inventories.get(0);
                    currentInventoryId = first.id;
                    if (first.vehicles != null) {
                        vehicles.clear();
                        vehicles.addAll(first.vehicles);
                        adapter.notifyDataSetChanged();
                        if (vehicles.isEmpty()) {
                            tvEmpty.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    tvEmpty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(InventoryActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                tvEmpty.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

