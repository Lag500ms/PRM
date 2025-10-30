package com.example.myapplication.ui.inventory;


import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.example.myapplication.model.inventory.UpdateVehicleQuantityRequest;
import com.example.myapplication.model.vehicle.response.VehicleResponseDTO;
import com.example.myapplication.network.RetrofitClient;
import com.example.myapplication.network.VehicleApiService;
import com.example.myapplication.repository.InventoryRepository;
import com.example.myapplication.repository.VehicleRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryReceiveActivity extends AppCompatActivity {

    public static final String EXTRA_INVENTORY_ID = "inventory_id";

    private AutoCompleteTextView vehicleDropdown;
    private TextInputEditText etQuantity;
    private MaterialButton btnReceive;
    private ProgressBar progressBar;
    private InventoryRepository inventoryRepository;
    private VehicleRepository vehicleRepository;
    private String inventoryId;
    private Map<String, String> vehicleMap = new HashMap<>(); // Display name -> vehicleId

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_receive);

        inventoryRepository = new InventoryRepository(this);
        VehicleApiService vehicleApiService = RetrofitClient.createWithAuth(this, VehicleApiService.class);
        vehicleRepository = new VehicleRepository(vehicleApiService);

        vehicleDropdown = findViewById(R.id.vehicleDropdown);
        etQuantity = findViewById(R.id.etQuantity);
        btnReceive = findViewById(R.id.btnReceive);
        progressBar = findViewById(R.id.progressBar);

        inventoryId = getIntent().getStringExtra(EXTRA_INVENTORY_ID);

        loadVehicles();
        btnReceive.setOnClickListener(v -> receive());
    }

    private void loadVehicles() {
        progressBar.setVisibility(View.VISIBLE);
        vehicleRepository.getVehicles(null, 0, 100, new VehicleRepository.RepositoryCallback<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> data) {
                progressBar.setVisibility(View.GONE);
                List<Map<String, Object>> content = (List<Map<String, Object>>) data.get("content");
                if (content != null && !content.isEmpty()) {
                    List<String> vehicleNames = new ArrayList<>();
                    for (Map<String, Object> v : content) {
                        String id = (String) v.get("id");
                        String model = (String) v.get("model");
                        String version = (String) v.get("version");
                        String color = (String) v.get("color");
                        String displayName = model + " - " + version + " (" + color + ")";
                        vehicleNames.add(displayName);
                        vehicleMap.put(displayName, id);
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(InventoryReceiveActivity.this,
                            android.R.layout.simple_dropdown_item_1line, vehicleNames);
                    vehicleDropdown.setAdapter(adapter);
                } else {
                    Toast.makeText(InventoryReceiveActivity.this, "No vehicles available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String errorMessage) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(InventoryReceiveActivity.this, "Error loading vehicles: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void receive() {
        String selectedVehicleName = vehicleDropdown.getText() != null ? vehicleDropdown.getText().toString().trim() : "";
        String quantityStr = etQuantity.getText() != null ? etQuantity.getText().toString().trim() : "";
        
        if (inventoryId == null || selectedVehicleName.isEmpty() || quantityStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String vehicleId = vehicleMap.get(selectedVehicleName);
        if (vehicleId == null) {
            Toast.makeText(this, "Invalid vehicle selection", Toast.LENGTH_SHORT).show();
            return;
        }

        int qty = Integer.parseInt(quantityStr);
        progressBar.setVisibility(View.VISIBLE);
        btnReceive.setEnabled(false);

        UpdateVehicleQuantityRequest req = new UpdateVehicleQuantityRequest();
        req.inventoryId = inventoryId;
        req.vehicleId = vehicleId;
        req.quantity = qty;
        inventoryRepository.updateQuantity(req, new InventoryRepository.InventoryDetailCallback() {
            @Override
            public void onSuccess(com.example.myapplication.model.inventory.InventoryResponse response) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(InventoryReceiveActivity.this, "Received", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
                btnReceive.setEnabled(true);
                Toast.makeText(InventoryReceiveActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }
}


