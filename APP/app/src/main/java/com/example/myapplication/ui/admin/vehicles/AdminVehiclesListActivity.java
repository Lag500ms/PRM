package com.example.myapplication.ui.admin.vehicles;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.model.vehicle.response.VehicleResponseDTO;
import com.example.myapplication.network.RetrofitClient;
import com.example.myapplication.network.VehicleApiService;
import com.example.myapplication.repository.VehicleRepository;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdminVehiclesListActivity extends AppCompatActivity {

    private RecyclerView rvVehicles;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private FloatingActionButton fabAddVehicle;
    private VehicleRepository vehicleRepository;
    private VehiclesAdapter adapter;
    private List<VehicleResponseDTO> vehicles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_vehicles_list);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        VehicleApiService apiService = RetrofitClient.createWithAuth(this, VehicleApiService.class);
        vehicleRepository = new VehicleRepository(apiService);

        initViews();
        setupRecyclerView();
        loadVehicles();

        fabAddVehicle.setOnClickListener(v -> {
            startActivity(new Intent(this, AdminVehicleFormActivity.class));
        });
    }

    private void initViews() {
        rvVehicles = findViewById(R.id.rvVehicles);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);
        fabAddVehicle = findViewById(R.id.fabAddVehicle);
    }

    private void setupRecyclerView() {
        // Load categories first to get names
        loadCategoriesCache();
        
        adapter = new VehiclesAdapter(this, vehicles, 
            vehicle -> {
                Intent intent = new Intent(this, AdminVehicleFormActivity.class);
                intent.putExtra("vehicle_id", vehicle.getId());
                startActivity(intent);
            },
            vehicle -> deleteVehicle(vehicle));
        rvVehicles.setLayoutManager(new LinearLayoutManager(this));
        rvVehicles.setAdapter(adapter);
    }

    private void loadCategoriesCache() {
        com.example.myapplication.network.CategoryApiService categoryApi = 
            com.example.myapplication.network.RetrofitClient.createWithAuth(this, 
                com.example.myapplication.network.CategoryApiService.class);
        
        categoryApi.getAllCategories().enqueue(new retrofit2.Callback<java.util.List<com.example.myapplication.model.category.CategoryResponseDTO>>() {
            @Override
            public void onResponse(retrofit2.Call<java.util.List<com.example.myapplication.model.category.CategoryResponseDTO>> call, 
                                 retrofit2.Response<java.util.List<com.example.myapplication.model.category.CategoryResponseDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Cache categories
                    for (com.example.myapplication.model.category.CategoryResponseDTO cat : response.body()) {
                        VehiclesAdapter.categoryCache.put(cat.getId(), cat.getName());
                    }
                    // Refresh adapter to show category names
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<java.util.List<com.example.myapplication.model.category.CategoryResponseDTO>> call, Throwable t) {
                android.util.Log.e("AdminVehiclesList", "Failed to load categories", t);
            }
        });
    }

    private void deleteVehicle(VehicleResponseDTO vehicle) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Delete Vehicle")
                .setMessage("Are you sure you want to delete " + vehicle.getModel() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    progressBar.setVisibility(View.VISIBLE);
                    vehicleRepository.delete(vehicle.getId(), new VehicleRepository.RepositoryCallback<Void>() {
                        @Override
                        public void onSuccess(Void data) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(AdminVehiclesListActivity.this, "Vehicle deleted", Toast.LENGTH_SHORT).show();
                            loadVehicles();
                        }

                        @Override
                        public void onError(String errorMessage) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(AdminVehiclesListActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void loadVehicles() {
        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);

        vehicleRepository.getVehicles(null, 0, 100, new VehicleRepository.RepositoryCallback<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> data) {
                progressBar.setVisibility(View.GONE);
                List<Map<String, Object>> content = (List<Map<String, Object>>) data.get("content");
                if (content != null && !content.isEmpty()) {
                    vehicles.clear();
                    for (Map<String, Object> v : content) {
                        VehicleResponseDTO dto = new VehicleResponseDTO();
                        dto.setId((String) v.get("id"));
                        dto.setModel((String) v.get("model"));
                        dto.setVersion((String) v.get("version"));
                        dto.setColor((String) v.get("color"));
                        dto.setPrice(v.get("price") != null ? ((Number) v.get("price")).doubleValue() : 0.0);
                        dto.setQuantity(v.get("quantity") != null ? ((Number) v.get("quantity")).intValue() : 0);
                        dto.setCategoryId((String) v.get("categoryId"));
                        dto.setImage((String) v.get("image"));
                        vehicles.add(dto);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    tvEmpty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(String errorMessage) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AdminVehiclesListActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                tvEmpty.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadVehicles();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

