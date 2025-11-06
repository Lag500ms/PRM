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

/**
 * AdminVehicleFormActivity - Màn hình form tạo/sửa xe (CREATE + UPDATE)
 * 
 * Chức năng:
 * - CREATE: Tạo xe mới (vehicleId == null)
 * - UPDATE: Sửa xe (vehicleId != null)
 * 
 * Flow CREATE:
 * - FAB button từ ListActivity → mở FormActivity (không có vehicle_id)
 * - User nhập form → bấm Save → save() → vehicleRepository.create()
 * - API POST /api/v1/vehicles → finish() → quay lại ListActivity → onResume() → loadVehicles()
 * 
 * Flow UPDATE:
 * - Click item từ ListActivity → mở FormActivity (có vehicle_id trong Intent)
 * - onCreate() → loadVehicle() → API GET /api/v1/vehicles/{id} → fill form
 * - User sửa → bấm Save → save() → vehicleRepository.update()
 * - API PUT /api/v1/vehicles/{id} → finish() → quay lại ListActivity → onResume() → loadVehicles()
 */
public class AdminVehicleFormActivity extends AppCompatActivity {

    // Form fields
    private TextInputEditText etModel, etVersion, etColor, etPrice, etQuantity, etImage;
    private AutoCompleteTextView categoryDropdown; // Dropdown chọn category
    private MaterialButton btnSave;
    private ProgressBar progressBar;
    
    // Data & Logic
    private VehicleRepository vehicleRepository;      // Repository để gọi API vehicles
    private CategoryApiService categoryApiService;    // API service để load categories
    private String vehicleId;                         // null = create mode, != null = edit mode
    private Map<String, String> categoryMap = new HashMap<>(); // Map categoryName → categoryId

    /**
     * onCreate() - Khởi tạo Activity: setup views, load categories, nếu có vehicle_id thì load dữ liệu để edit
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_vehicle_form);

        // Setup toolbar với nút back
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // Tạo VehicleApiService và CategoryApiService với JWT token
        VehicleApiService vehicleApiService = RetrofitClient.createWithAuth(this, VehicleApiService.class);
        vehicleRepository = new VehicleRepository(vehicleApiService);
        categoryApiService = RetrofitClient.createWithAuth(this, CategoryApiService.class);

        // Khởi tạo các view từ layout
        etModel = findViewById(R.id.etModel);
        etVersion = findViewById(R.id.etVersion);
        etColor = findViewById(R.id.etColor);
        etPrice = findViewById(R.id.etPrice);
        etQuantity = findViewById(R.id.etQuantity);
        etImage = findViewById(R.id.etImage);
        categoryDropdown = findViewById(R.id.categoryDropdown);
        btnSave = findViewById(R.id.btnSave);
        progressBar = findViewById(R.id.progressBar);

        // Lấy vehicle_id từ Intent
        // - Nếu ListActivity mở FormActivity với vehicle_id → UPDATE mode
        // - Nếu ListActivity mở FormActivity không có vehicle_id → CREATE mode
        vehicleId = getIntent().getStringExtra("vehicle_id");

        // Load categories để hiển thị dropdown (cần load trước khi fill form nếu UPDATE mode)
        loadCategories();
        
        if (vehicleId != null) {
            // UPDATE mode: có vehicle_id → load dữ liệu từ API để fill form
            toolbar.setTitle("Edit Vehicle");
            loadVehicle(); // Gọi API GET /api/v1/vehicles/{id} → fill form
        } else {
            // CREATE mode: không có vehicle_id → form trống, user tự nhập
            toolbar.setTitle("Add Vehicle");
        }

        // Setup btnSave: click → gọi save() → CREATE hoặc UPDATE tùy vehicleId
        btnSave.setOnClickListener(v -> save());
    }

    /**
     * loadCategories() - Load categories để hiển thị dropdown, lưu map tên → ID để convert khi save()
     */
    private void loadCategories() {
        // Gọi API GET /api/v1/categories/getAll
        categoryApiService.getAllCategories().enqueue(new Callback<List<CategoryResponseDTO>>() {
            @Override
            public void onResponse(Call<List<CategoryResponseDTO>> call, Response<List<CategoryResponseDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> categoryNames = new ArrayList<>();
                    // Duyệt từng category: lấy tên để hiển thị, lưu map tên → ID
                    for (CategoryResponseDTO cat : response.body()) {
                        categoryNames.add(cat.getName()); // List tên để hiển thị dropdown
                        categoryMap.put(cat.getName(), cat.getId()); // Map tên → ID để convert khi save()
                    }
                    // Tạo ArrayAdapter với danh sách tên categories
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(AdminVehicleFormActivity.this,
                            android.R.layout.simple_dropdown_item_1line, categoryNames);
                    // Gắn adapter vào AutoCompleteTextView → user có thể chọn category
                    categoryDropdown.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<CategoryResponseDTO>> call, Throwable t) {
                Toast.makeText(AdminVehicleFormActivity.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * loadVehicle() - Load dữ liệu vehicle từ API để fill form (chỉ dùng cho UPDATE mode)
     */
    private void loadVehicle() {
        // Hiện progressBar để báo đang load
        progressBar.setVisibility(View.VISIBLE);
        
        // Gọi Repository.getById() → API GET /api/v1/vehicles/{id}
        vehicleRepository.getById(vehicleId, new VehicleRepository.RepositoryCallback<VehicleResponseDTO>() {
            @Override
            public void onSuccess(VehicleResponseDTO vehicle) {
                // Ẩn progressBar
                progressBar.setVisibility(View.GONE);
                
                // Fill các field với dữ liệu từ API
                etModel.setText(vehicle.getModel());
                etVersion.setText(vehicle.getVersion());
                etColor.setText(vehicle.getColor());
                etPrice.setText(String.valueOf(vehicle.getPrice()));
                etQuantity.setText(String.valueOf(vehicle.getQuantity()));
                etImage.setText(vehicle.getImage());
                // Category dropdown sẽ được set sau khi categories load xong
                // (có thể cần thêm logic để set category dropdown nếu cần)
            }

            @Override
            public void onError(String errorMessage) {
                // Lỗi → ẩn progressBar, hiện Toast error
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AdminVehicleFormActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * save() - Lưu vehicle: validate form → build request → gọi create() hoặc update() tùy vehicleId
     */
    private void save() {
        // Lấy dữ liệu từ form (kiểm tra null an toàn)
        String model = etModel.getText() != null ? etModel.getText().toString().trim() : "";
        String version = etVersion.getText() != null ? etVersion.getText().toString().trim() : "";
        String color = etColor.getText() != null ? etColor.getText().toString().trim() : "";
        String priceStr = etPrice.getText() != null ? etPrice.getText().toString().trim() : "";
        String quantityStr = etQuantity.getText() != null ? etQuantity.getText().toString().trim() : "";
        String image = etImage.getText() != null ? etImage.getText().toString().trim() : "";
        String categoryName = categoryDropdown.getText() != null ? categoryDropdown.getText().toString().trim() : "";

        // Validate: kiểm tra các field bắt buộc
        if (model.isEmpty() || version.isEmpty() || color.isEmpty() || priceStr.isEmpty() || quantityStr.isEmpty() || categoryName.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert categoryName → categoryId (từ categoryMap đã load trong loadCategories())
        String categoryId = categoryMap.get(categoryName);
        if (categoryId == null) {
            Toast.makeText(this, "Invalid category", Toast.LENGTH_SHORT).show();
            return;
        }

        // Parse price và quantity từ String → số
        double price = Double.parseDouble(priceStr);
        int quantity = Integer.parseInt(quantityStr);

        // Hiện progressBar, disable btnSave để tránh double-click
        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        // Build VehicleRequestDTO với dữ liệu từ form
        VehicleRequestDTO request = new VehicleRequestDTO();
        request.setModel(model);
        request.setVersion(version);
        request.setColor(color);
        request.setPrice(price);
        request.setQuantity(quantity);
        request.setImage(image.isEmpty() ? null : image); // Image có thể null
        request.setCategoryId(categoryId);
        
        // Lấy accountId từ SharedPrefManager (đã lưu khi login thành công)
        String accountId = SharedPrefManager.getInstance(this).getUserId();
        android.util.Log.d("AdminVehicleForm", "Account ID from SharedPref: " + accountId);
        android.util.Log.d("AdminVehicleForm", "Username from SharedPref: " + SharedPrefManager.getInstance(this).getUsername());
        
        // Validate accountId (bắt buộc phải có)
        if (accountId == null || accountId.isEmpty()) {
            Toast.makeText(this, "Account ID not found. Please logout and login again.", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            btnSave.setEnabled(true);
            return;
        }
        request.setAccountId(accountId);

        // Kiểm tra vehicleId để quyết định CREATE hay UPDATE
        if (vehicleId == null) {
            // CREATE mode: vehicleId == null → gọi create()
            vehicleRepository.create(request, new VehicleRepository.RepositoryCallback<VehicleResponseDTO>() {
                @Override
                public void onSuccess(VehicleResponseDTO data) {
                    // Tạo thành công
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(AdminVehicleFormActivity.this, "Vehicle created", Toast.LENGTH_SHORT).show();
                    finish(); // Quay lại ListActivity → onResume() → loadVehicles() → hiển thị item mới
                }

                @Override
                public void onError(String errorMessage) {
                    // Tạo thất bại
                    progressBar.setVisibility(View.GONE);
                    btnSave.setEnabled(true); // Enable lại để user có thể thử lại
                    Toast.makeText(AdminVehicleFormActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                }
            });
        } else {
            // UPDATE mode: vehicleId != null → gọi update()
            vehicleRepository.update(vehicleId, request, new VehicleRepository.RepositoryCallback<VehicleResponseDTO>() {
                @Override
                public void onSuccess(VehicleResponseDTO data) {
                    // Cập nhật thành công
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(AdminVehicleFormActivity.this, "Vehicle updated", Toast.LENGTH_SHORT).show();
                    finish(); // Quay lại ListActivity → onResume() → loadVehicles() → hiển thị dữ liệu mới
                }

                @Override
                public void onError(String errorMessage) {
                    // Cập nhật thất bại
                    progressBar.setVisibility(View.GONE);
                    btnSave.setEnabled(true); // Enable lại để user có thể thử lại
                    Toast.makeText(AdminVehicleFormActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}

