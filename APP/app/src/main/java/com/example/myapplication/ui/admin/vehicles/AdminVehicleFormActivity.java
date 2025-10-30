package com.example.myapplication.ui.admin.vehicles;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.example.myapplication.model.category.CategoryResponseDTO;
import com.example.myapplication.model.vehicle.request.VehicleRequestDTO;
import com.example.myapplication.model.vehicle.response.VehicleResponseDTO;
import com.example.myapplication.network.CategoryApiService;
import com.example.myapplication.network.RetrofitClient;
import com.example.myapplication.network.VehicleApiService;
import com.example.myapplication.repository.VehicleRepository;
import com.example.myapplication.utils.SharedPrefManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminVehicleFormActivity extends AppCompatActivity {

    private TextInputEditText etModel, etVersion, etColor, etPrice, etQuantity, etImage;
    private AutoCompleteTextView categoryDropdown;
    private MaterialButton btnSave;
    private ProgressBar progressBar;
    private VehicleRepository vehicleRepository;
    private CategoryApiService categoryApiService;
    private String vehicleId;
    private Map<String, String> categoryMap = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_vehicle_form);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        VehicleApiService vehicleApiService = RetrofitClient.createWithAuth(this, VehicleApiService.class);
        vehicleRepository = new VehicleRepository(vehicleApiService);
        categoryApiService = RetrofitClient.createWithAuth(this, CategoryApiService.class);

        etModel = findViewById(R.id.etModel);
        etVersion = findViewById(R.id.etVersion);
        etColor = findViewById(R.id.etColor);
        etPrice = findViewById(R.id.etPrice);
        etQuantity = findViewById(R.id.etQuantity);
        etImage = findViewById(R.id.etImage);
        categoryDropdown = findViewById(R.id.categoryDropdown);
        btnSave = findViewById(R.id.btnSave);
        progressBar = findViewById(R.id.progressBar);

        vehicleId = getIntent().getStringExtra("vehicle_id");

        loadCategories();
        
        if (vehicleId != null) {
            toolbar.setTitle("Edit Vehicle");
            loadVehicle();
        } else {
            toolbar.setTitle("Add Vehicle");
        }

        btnSave.setOnClickListener(v -> save());
    }

    private void loadCategories() {
        categoryApiService.getAllCategories().enqueue(new Callback<List<CategoryResponseDTO>>() {
            @Override
            public void onResponse(Call<List<CategoryResponseDTO>> call, Response<List<CategoryResponseDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> categoryNames = new ArrayList<>();
                    for (CategoryResponseDTO cat : response.body()) {
                        categoryNames.add(cat.getName());
                        categoryMap.put(cat.getName(), cat.getId());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(AdminVehicleFormActivity.this,
                            android.R.layout.simple_dropdown_item_1line, categoryNames);
                    categoryDropdown.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<CategoryResponseDTO>> call, Throwable t) {
                Toast.makeText(AdminVehicleFormActivity.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadVehicle() {
        progressBar.setVisibility(View.VISIBLE);
        vehicleRepository.getById(vehicleId, new VehicleRepository.RepositoryCallback<VehicleResponseDTO>() {
            @Override
            public void onSuccess(VehicleResponseDTO vehicle) {
                progressBar.setVisibility(View.GONE);
                etModel.setText(vehicle.getModel());
                etVersion.setText(vehicle.getVersion());
                etColor.setText(vehicle.getColor());
                etPrice.setText(String.valueOf(vehicle.getPrice()));
                etQuantity.setText(String.valueOf(vehicle.getQuantity()));
                etImage.setText(vehicle.getImage());
                // Category will be set after categories are loaded
            }

            @Override
            public void onError(String errorMessage) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AdminVehicleFormActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void save() {
        String model = etModel.getText() != null ? etModel.getText().toString().trim() : "";
        String version = etVersion.getText() != null ? etVersion.getText().toString().trim() : "";
        String color = etColor.getText() != null ? etColor.getText().toString().trim() : "";
        String priceStr = etPrice.getText() != null ? etPrice.getText().toString().trim() : "";
        String quantityStr = etQuantity.getText() != null ? etQuantity.getText().toString().trim() : "";
        String image = etImage.getText() != null ? etImage.getText().toString().trim() : "";
        String categoryName = categoryDropdown.getText() != null ? categoryDropdown.getText().toString().trim() : "";

        if (model.isEmpty() || version.isEmpty() || color.isEmpty() || priceStr.isEmpty() || quantityStr.isEmpty() || categoryName.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String categoryId = categoryMap.get(categoryName);
        if (categoryId == null) {
            Toast.makeText(this, "Invalid category", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceStr);
        int quantity = Integer.parseInt(quantityStr);

        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        VehicleRequestDTO request = new VehicleRequestDTO();
        request.setModel(model);
        request.setVersion(version);
        request.setColor(color);
        request.setPrice(price);
        request.setQuantity(quantity);
        request.setImage(image.isEmpty() ? null : image);
        request.setCategoryId(categoryId);
        // Use username as accountId since userId is not available in login response
        request.setAccountId(SharedPrefManager.getInstance(this).getUsername());

        if (vehicleId == null) {
            vehicleRepository.create(request, new VehicleRepository.RepositoryCallback<VehicleResponseDTO>() {
                @Override
                public void onSuccess(VehicleResponseDTO data) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(AdminVehicleFormActivity.this, "Vehicle created", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onError(String errorMessage) {
                    progressBar.setVisibility(View.GONE);
                    btnSave.setEnabled(true);
                    Toast.makeText(AdminVehicleFormActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                }
            });
        } else {
            vehicleRepository.update(vehicleId, request, new VehicleRepository.RepositoryCallback<VehicleResponseDTO>() {
                @Override
                public void onSuccess(VehicleResponseDTO data) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(AdminVehicleFormActivity.this, "Vehicle updated", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onError(String errorMessage) {
                    progressBar.setVisibility(View.GONE);
                    btnSave.setEnabled(true);
                    Toast.makeText(AdminVehicleFormActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}

