package com.example.myapplication.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.model.inventory.InventoryResponse;
import com.example.myapplication.model.inventory.UpdateVehicleQuantityRequest;
import com.example.myapplication.model.inventory.VehicleItem;
import com.example.myapplication.repository.InventoryRepository;
import com.example.myapplication.ui.adapter.InventoryAdapter;
import com.example.myapplication.ui.inventory.InventoryReceiveActivity;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class InventoryActivity extends AppCompatActivity implements InventoryAdapter.OnItemClickListener, InventoryAdapter.OnItemLongClickListener {

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
        adapter = new InventoryAdapter(vehicles, this, this);
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
    public void onItemClick(VehicleItem item) {
        showUpdateQuantityDialog(item);
    }

    @Override
    public void onItemLongClick(VehicleItem item) {
        showEditDeleteDialog(item);
    }

    private void showEditDeleteDialog(VehicleItem item) {
        final CharSequence[] options = {"Edit", "Return Vehicle"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose an option");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) { // Edit
                showUpdateQuantityDialog(item);
            } else { // Return Vehicle
                showReturnVehicleDialog(item);
            }
        });
        builder.show();
    }

    private void showReturnVehicleDialog(VehicleItem item) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_update_quantity, null);
        final TextInputEditText input = dialogView.findViewById(R.id.etQuantity);
        input.setHint("Quantity to return");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Return Vehicle");
        builder.setView(dialogView);

        builder.setPositiveButton("Return", (dialog, which) -> {
            String quantityStr = input.getText().toString();
            if (!quantityStr.isEmpty()) {
                int quantityToReturn = Integer.parseInt(quantityStr);
                returnVehicle(item, quantityToReturn);
            } else {
                Toast.makeText(this, "Please enter a quantity", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void returnVehicle(VehicleItem vehicle, int quantity) {
        progressBar.setVisibility(View.VISIBLE);

        UpdateVehicleQuantityRequest request = new UpdateVehicleQuantityRequest();
        request.inventoryId = currentInventoryId;
        request.vehicleId = vehicle.vehicleId;
        request.quantity = quantity;

        inventoryRepository.returnVehicle(request, new InventoryRepository.InventoryDetailCallback() {
            @Override
            public void onSuccess(InventoryResponse response) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(InventoryActivity.this, "Vehicle returned successfully", Toast.LENGTH_SHORT).show();
                loadInventory(); // Refresh the list
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(InventoryActivity.this, "Error returning vehicle: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showUpdateQuantityDialog(VehicleItem item) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_update_quantity, null);
        final TextInputEditText input = dialogView.findViewById(R.id.etQuantity);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        builder.setPositiveButton("Update", (dialog, which) -> {
            String quantityStr = input.getText().toString();
            if (!quantityStr.isEmpty()) {
                int newQuantity = Integer.parseInt(quantityStr);
                updateQuantity(item, newQuantity);
            } else {
                Toast.makeText(this, "Please enter a quantity", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void updateQuantity(VehicleItem vehicle, int newQuantity) {
        progressBar.setVisibility(View.VISIBLE);

        UpdateVehicleQuantityRequest request = new UpdateVehicleQuantityRequest();
        request.inventoryId = currentInventoryId;
        request.vehicleId = vehicle.vehicleId;
        request.quantity = newQuantity;

        inventoryRepository.updateQuantity(request, new InventoryRepository.InventoryDetailCallback() {
            @Override
            public void onSuccess(InventoryResponse response) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(InventoryActivity.this, "Quantity updated successfully", Toast.LENGTH_SHORT).show();
                loadInventory(); // Refresh the list
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(InventoryActivity.this, "Error updating quantity: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
