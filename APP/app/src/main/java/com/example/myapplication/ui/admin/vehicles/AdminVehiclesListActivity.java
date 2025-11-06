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

/**
 * AdminVehiclesListActivity - Màn hình danh sách xe (READ List)
 * 
 * Chức năng:
 * - Hiển thị danh sách tất cả vehicles trong RecyclerView
 * - Click vào item → mở form edit (AdminVehicleFormActivity)
 * - Long click vào item → xóa vehicle (DELETE)
 * - FAB button → mở form tạo mới (AdminVehicleFormActivity)
 * 
 * Flow:
 * onCreate() → initViews() → setupRecyclerView() → loadVehicles()
 * loadVehicles() → VehicleRepository.getVehicles() → API GET /api/v1/vehicles
 * Response → parse JSON → vehicles list → adapter.notifyDataSetChanged() → RecyclerView hiển thị
 */
public class AdminVehiclesListActivity extends AppCompatActivity {

    // UI Components
    private RecyclerView rvVehicles;          // RecyclerView hiển thị danh sách xe
    private ProgressBar progressBar;          // ProgressBar khi đang load dữ liệu
    private TextView tvEmpty;                 // TextView hiển thị khi list rỗng
    private FloatingActionButton fabAddVehicle; // FAB button để tạo xe mới
    
    // Data & Logic
    private VehicleRepository vehicleRepository; // Repository để gọi API
    private VehiclesAdapter adapter;                 // Adapter cho RecyclerView
    private List<VehicleResponseDTO> vehicles = new ArrayList<>(); // List dữ liệu vehicles

    /**
     * onCreate() - Khởi tạo Activity
     * 
     * Flow:
     * 1. Set layout
     * 2. Setup toolbar với nút back
     * 3. Tạo VehicleApiService qua RetrofitClient (tự động thêm JWT token)
     * 4. Tạo VehicleRepository với apiService
     * 5. Khởi tạo views (RecyclerView, ProgressBar, ...)
     * 6. Setup RecyclerView với Adapter
     * 7. Load danh sách vehicles từ API
     * 8. Setup FAB button để mở form tạo mới
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_vehicles_list);

        // Setup toolbar với nút back
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // Tạo VehicleApiService qua RetrofitClient.createWithAuth()
        // → RetrofitClient tự động thêm JWT token vào header qua AuthInterceptor
        VehicleApiService apiService = RetrofitClient.createWithAuth(this, VehicleApiService.class);
        vehicleRepository = new VehicleRepository(apiService);

        // Khởi tạo các view (RecyclerView, ProgressBar, FAB, ...)
        initViews();
        
        // Setup RecyclerView: tạo Adapter, set LayoutManager, gắn Adapter vào RecyclerView
        setupRecyclerView();
        
        // Load danh sách vehicles từ API
        loadVehicles();

        // FAB button: click → mở AdminVehicleFormActivity (không có vehicle_id = create mode)
        fabAddVehicle.setOnClickListener(v -> {
            startActivity(new Intent(this, AdminVehicleFormActivity.class));
        });
    }

    /**
     * initViews() - Khởi tạo các view từ layout
     * 
     * Tìm và gán các view từ XML layout vào biến Java:
     * - rvVehicles: RecyclerView để hiển thị danh sách
     * - progressBar: hiển thị khi đang load
     * - tvEmpty: hiển thị khi list rỗng
     * - fabAddVehicle: nút tạo mới
     */
    private void initViews() {
        rvVehicles = findViewById(R.id.rvVehicles);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);
        fabAddVehicle = findViewById(R.id.fabAddVehicle);
    }

    /**
     * setupRecyclerView() - Cấu hình RecyclerView và Adapter
     * 
     * Flow:
     * 1. Load categories cache trước (để hiển thị tên category thay vì ID)
     * 2. Tạo VehiclesAdapter với 2 callback:
     *    - Click vào item → mở AdminVehicleFormActivity với vehicle_id (edit mode)
     *    - Long click vào item → gọi deleteVehicle() để xóa
     * 3. Set LinearLayoutManager (vertical list)
     * 4. Gắn adapter vào RecyclerView
     * 
     * Adapter sẽ tự động gọi:
     * - getItemCount() → số lượng items
     * - onCreateViewHolder() → tạo ViewHolder từ item_vehicle.xml
     * - onBindViewHolder() → gắn dữ liệu vào ViewHolder
     */
    private void setupRecyclerView() {
        // Load categories cache trước để Adapter có thể hiển thị tên category
        loadCategoriesCache();
        
        // Tạo VehiclesAdapter với 2 callback:
        // 1. Click vào item → mở form edit
        adapter = new VehiclesAdapter(this, vehicles, 
            vehicle -> {
                // Tạo Intent với vehicle_id và mở AdminVehicleFormActivity
                // Form sẽ nhận vehicle_id → loadVehicle() → fill form với dữ liệu từ API
                Intent intent = new Intent(this, AdminVehicleFormActivity.class);
                intent.putExtra("vehicle_id", vehicle.getId());
                startActivity(intent);
            },
            // 2. Long click → xóa vehicle
            vehicle -> deleteVehicle(vehicle));
        
        // Set LinearLayoutManager để RecyclerView hiển thị dạng list dọc
        rvVehicles.setLayoutManager(new LinearLayoutManager(this));
        
        // Gắn adapter vào RecyclerView
        // → RecyclerView sẽ tự động gọi adapter.getItemCount(), onCreateViewHolder(), onBindViewHolder()
        rvVehicles.setAdapter(adapter);
    }

    /**
     * loadCategoriesCache() - Load categories và cache để Adapter hiển thị tên category thay vì ID
     */
    private void loadCategoriesCache() {
        // Tạo CategoryApiService với JWT token
        com.example.myapplication.network.CategoryApiService categoryApi = 
            com.example.myapplication.network.RetrofitClient.createWithAuth(this, 
                com.example.myapplication.network.CategoryApiService.class);
        
        // Gọi API GET /api/v1/categories/getAll
        categoryApi.getAllCategories().enqueue(new retrofit2.Callback<java.util.List<com.example.myapplication.model.category.CategoryResponseDTO>>() {
            @Override
            public void onResponse(retrofit2.Call<java.util.List<com.example.myapplication.model.category.CategoryResponseDTO>> call, 
                                 retrofit2.Response<java.util.List<com.example.myapplication.model.category.CategoryResponseDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Cache categories: lưu categoryId → categoryName vào static Map
                    for (com.example.myapplication.model.category.CategoryResponseDTO cat : response.body()) {
                        VehiclesAdapter.categoryCache.put(cat.getId(), cat.getName());
                    }
                    // Refresh adapter để hiển thị tên category (thay vì "Unknown")
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

    /**
     * loadVehicles() - Load danh sách vehicles từ API → parse JSON → hiển thị trong RecyclerView
     */
    private void loadVehicles() {
        // Hiện progressBar để báo đang load
        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);

        // Gọi Repository.getVehicles() → API GET /api/v1/vehicles
        // keyword=null (không search), page=0, size=100 (lấy 100 items đầu tiên)
        vehicleRepository.getVehicles(null, 0, 100, new VehicleRepository.RepositoryCallback<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> data) {
                // Ẩn progressBar
                progressBar.setVisibility(View.GONE);
                
                // Lấy "content" từ response (JSON Page format)
                // Response có dạng: {"content": [...], "totalElements": 10, "totalPages": 1}
                List<Map<String, Object>> content = (List<Map<String, Object>>) data.get("content");
                
                if (content != null && !content.isEmpty()) {
                    // Clear list cũ
                    vehicles.clear();
                    
                    // Duyệt từng item trong content và map sang VehicleResponseDTO
                    for (Map<String, Object> v : content) {
                        VehicleResponseDTO dto = new VehicleResponseDTO();
                        dto.setId((String) v.get("id"));
                        dto.setModel((String) v.get("model"));
                        dto.setVersion((String) v.get("version"));
                        dto.setColor((String) v.get("color"));
                        // Ép kiểu an toàn: kiểm tra null trước khi cast
                        dto.setPrice(v.get("price") != null ? ((Number) v.get("price")).doubleValue() : 0.0);
                        dto.setQuantity(v.get("quantity") != null ? ((Number) v.get("quantity")).intValue() : 0);
                        dto.setCategoryId((String) v.get("categoryId"));
                        dto.setImage((String) v.get("image"));
                        vehicles.add(dto);
                    }
                    
                    // Báo cho RecyclerView: dữ liệu đã thay đổi, vẽ lại
                    // → RecyclerView sẽ gọi adapter.onBindViewHolder() cho từng item
                    adapter.notifyDataSetChanged();
                } else {
                    // List rỗng → hiện TextView "Empty"
                    tvEmpty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(String errorMessage) {
                // Lỗi → ẩn progressBar, hiện Toast error, hiện tvEmpty
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AdminVehiclesListActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                tvEmpty.setVisibility(View.VISIBLE);
            }
        });
    }









































    @Override
    protected void onResume() {
        super.onResume();
        // Reload danh sách để cập nhật dữ liệu mới nhất
        loadVehicles();
    }

    /**
     * onSupportNavigateUp() - Xử lý nút back trên toolbar
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

