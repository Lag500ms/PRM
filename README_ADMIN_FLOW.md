# Script trả lời thầy - CRUD Admin (Vehicles, Categories, Accounts)

**Lời mở:** "Em phụ trách phần Admin cho 3 module: Vehicles, Categories, Accounts. Em giải thích theo từng CRUD: bấm ở đâu, gọi API nào, code nằm file/dòng nào, và UI hiển thị ra sao."

---

## 1) VEHICLES (Xe)

### READ (List) - Xem danh sách xe

"Ở AdminVehiclesListActivity dòng 133-169, em gọi vehicleRepository.getVehicles() để lấy danh sách. Repository này gọi API GET /api/v1/vehicles qua Retrofit. Khi nhận được JSON Page, em map từng item trong content thành VehicleResponseDTO rồi add vào list, sau đó adapter.notifyDataSetChanged() để RecyclerView hiển thị."

**Code cụ thể:**

```java
// AdminVehiclesListActivity.java dòng 133-169
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
```

```java
// VehicleRepository.java dòng 89-104
public void getVehicles(String keyword, int page, int size, RepositoryCallback<Map<String, Object>> callback) {
    apiService.getVehicles(keyword, page, size).enqueue(new Callback<Map<String, Object>>() {
        @Override
        public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
            if (response.isSuccessful() && response.body() != null)
                callback.onSuccess(response.body());
            else
                callback.onError("Error: " + response.code());
        }

        @Override
        public void onFailure(Call<Map<String, Object>> call, Throwable t) {
            callback.onError(t.getMessage());
        }
    });
}
```

**UI:** RecyclerView hiển thị danh sách xe với model, version, color, price. Nếu rỗng thì hiện TextView "Empty".

---

### READ (Detail) - Xem chi tiết 1 xe khi edit

"Khi bấm vào item trong list, VehiclesAdapter gọi listener.onVehicleClick(vehicle). Activity nhận callback, tạo Intent với vehicle_id và mở AdminVehicleFormActivity. Trong form, nếu có vehicle_id thì gọi loadVehicle() → vehicleRepository.getById(id) → API GET /api/v1/vehicles/{id} → fill các TextInputEditText với dữ liệu từ API."

**Code cụ thể:**

```java
// VehiclesAdapter.java dòng 87-92
itemView.setOnClickListener(v -> {
    if (listener != null) {
        listener.onVehicleClick(vehicle);  // Gọi callback
    }
});
```

```java
// AdminVehiclesListActivity.java dòng 70-75
adapter = new VehiclesAdapter(this, vehicles, 
    vehicle -> {
        // Tạo Intent với vehicle_id và mở màn hình edit
        Intent intent = new Intent(this, AdminVehicleFormActivity.class);
        intent.putExtra("vehicle_id", vehicle.getId());
        startActivity(intent);
    },
    vehicle -> deleteVehicle(vehicle));
```

```java
// AdminVehicleFormActivity.java dòng 69-81
vehicleId = getIntent().getStringExtra("vehicle_id");

if (vehicleId != null) {
    // Edit mode: load dữ liệu vehicle để fill form
    toolbar.setTitle("Edit Vehicle");
    loadVehicle();  // Gọi API GET /api/v1/vehicles/{id}
} else {
    // Create mode: form trống
    toolbar.setTitle("Add Vehicle");
}
```

```java
// AdminVehicleFormActivity.java dòng 109-132
private void loadVehicle() {
    progressBar.setVisibility(View.VISIBLE);
    vehicleRepository.getById(vehicleId, new VehicleRepository.RepositoryCallback<VehicleResponseDTO>() {
        @Override
        public void onSuccess(VehicleResponseDTO vehicle) {
            progressBar.setVisibility(View.GONE);
            // Fill các field với dữ liệu từ API
            etModel.setText(vehicle.getModel());
            etVersion.setText(vehicle.getVersion());
            etColor.setText(vehicle.getColor());
            etPrice.setText(String.valueOf(vehicle.getPrice()));
            etQuantity.setText(String.valueOf(vehicle.getQuantity()));
            etImage.setText(vehicle.getImage());
        }

        @Override
        public void onError(String errorMessage) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(AdminVehicleFormActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
        }
    });
}
```

```java
// VehicleRepository.java dòng 72-87
public void getById(String id, RepositoryCallback<VehicleResponseDTO> callback) {
    apiService.getVehicleById(id).enqueue(new Callback<VehicleResponseDTO>() {
        @Override
        public void onResponse(Call<VehicleResponseDTO> call, Response<VehicleResponseDTO> response) {
            if (response.isSuccessful() && response.body() != null)
                callback.onSuccess(response.body());
            else
                callback.onError("Error: " + response.code());
        }

        @Override
        public void onFailure(Call<VehicleResponseDTO> call, Throwable t) {
            callback.onError(t.getMessage());
        }
    });
}
```

**UI:** Click vào item trong RecyclerView → mở AdminVehicleFormActivity với form đã fill sẵn dữ liệu từ API. User có thể sửa và Save.

---

### CREATE - Tạo xe mới

"Từ form tạo (FAB button), em build VehicleRequestDTO rồi gọi vehicleRepository.create() ở dòng 21-36. Repository gọi API POST /api/v1/vehicles với body là request DTO. Thành công thì quay lại list và onResume() tự động reload."

**Code cụ thể:**

```java
// AdminVehicleFormActivity.java dòng 174-189
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
}
```

```java
// VehicleRepository.java dòng 21-36
public void create(VehicleRequestDTO request, RepositoryCallback<VehicleResponseDTO> callback) {
    apiService.createVehicle(request).enqueue(new Callback<VehicleResponseDTO>() {
        @Override
        public void onResponse(Call<VehicleResponseDTO> call, Response<VehicleResponseDTO> response) {
            if (response.isSuccessful() && response.body() != null)
                callback.onSuccess(response.body());
            else
                callback.onError("Error: " + response.code());
        }

        @Override
        public void onFailure(Call<VehicleResponseDTO> call, Throwable t) {
            callback.onError(t.getMessage());
        }
    });
}
```

**UI:** Form có các field: model, version, color, price, quantity, categoryId, image. Bấm Save → API call → Toast "Vehicle created" → finish() → quay lại list.

---

### UPDATE - Sửa xe

"Form edit cũng dùng AdminVehicleFormActivity, nhưng có vehicle_id trong Intent. Em gọi vehicleRepository.update(id, request) ở dòng 38-53 → API PUT /api/v1/vehicles/{id} → cập nhật xong finish() và reload list."

**Code cụ thể:**

```java
// AdminVehicleFormActivity.java dòng 191-206
else {
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
```

```java
// VehicleRepository.java dòng 38-53
public void update(String id, VehicleRequestDTO request, RepositoryCallback<VehicleResponseDTO> callback) {
    apiService.updateVehicle(id, request).enqueue(new Callback<VehicleResponseDTO>() {
        @Override
        public void onResponse(Call<VehicleResponseDTO> call, Response<VehicleResponseDTO> response) {
            if (response.isSuccessful() && response.body() != null)
                callback.onSuccess(response.body());
            else
                callback.onError("Error: " + response.code());
        }

        @Override
        public void onFailure(Call<VehicleResponseDTO> call, Throwable t) {
            callback.onError(t.getMessage());
        }
    });
}
```

---

### DELETE - Xóa xe

"Ở list, em nhấn giữ item (long click) → hiện dialog confirm ở dòng 108-131. Bấm Delete sẽ gọi vehicleRepository.delete(id) → API DELETE /api/v1/vehicles/{id} → thành công thì loadVehicles() để reload."

**Code cụ thể:**

```java
// AdminVehiclesListActivity.java dòng 108-131
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
```

```java
// VehicleRepository.java dòng 55-70
public void delete(String id, RepositoryCallback<Void> callback) {
    apiService.deleteVehicle(id).enqueue(new Callback<Void>() {
        @Override
        public void onResponse(Call<Void> call, Response<Void> response) {
            if (response.isSuccessful())
                callback.onSuccess(null);
            else
                callback.onError("Error: " + response.code());
        }

        @Override
        public void onFailure(Call<Void> call, Throwable t) {
            callback.onError(t.getMessage());
        }
    });
}
```

**Tổng kết Vehicles:** "Flow là Dashboard → Vehicles → List gọi GET; Click vào item → mở form edit với dữ liệu đã load; FAB button → mở form trống để tạo mới; Form tạo/sửa gọi POST/PUT; Long click → xóa gọi DELETE. Tất cả endpoint đều yêu cầu JWT token qua RetrofitClient.createWithAuth(), và BE kiểm tra ROLE_ADMIN."

---

## 2) CATEGORIES (Danh mục)

### READ (List) - Xem danh sách categories

"AdminCategoriesActivity dòng 63-89 gọi categoryApiService.getAllCategories() → API GET /api/v1/categories/getAll. Nhận List<CategoryResponseDTO>, clear list cũ, addAll mới, rồi adapter.notifyDataSetChanged()."

**Code cụ thể:**

```java
// AdminCategoriesActivity.java dòng 63-89
private void loadCategories() {
    progressBar.setVisibility(View.VISIBLE);
    tvEmpty.setVisibility(View.GONE);

    categoryApiService.getAllCategories().enqueue(new Callback<List<CategoryResponseDTO>>() {
        @Override
        public void onResponse(Call<List<CategoryResponseDTO>> call, Response<List<CategoryResponseDTO>> response) {
            progressBar.setVisibility(View.GONE);
            if (response.isSuccessful() && response.body() != null) {
                categories.clear();
                categories.addAll(response.body());
                adapter.notifyDataSetChanged();
                
                if (categories.isEmpty()) {
                    tvEmpty.setVisibility(View.VISIBLE);
                }
            }
        }

        @Override
        public void onFailure(Call<List<CategoryResponseDTO>> call, Throwable t) {
            progressBar.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
            Toast.makeText(AdminCategoriesActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    });
}
```

```java
// CategoryApiService.java dòng 11-12
@GET("v1/categories/getAll")
Call<List<CategoryResponseDTO>> getAllCategories();
```

---

### CREATE - Tạo category mới

"Bấm FAB → showCreateDialog() hiện dialog với TextInputEditText. Bấm Create → createCategory() ở dòng 126-149 → API POST /api/v1/categories/create → xong gọi loadCategories() để reload."

**Code cụ thể:**

```java
// AdminCategoriesActivity.java dòng 126-149
private void createCategory(String name) {
    progressBar.setVisibility(View.VISIBLE);
    com.example.myapplication.model.category.CategoryRequestDTO request = new com.example.myapplication.model.category.CategoryRequestDTO();
    request.setName(name);

    categoryApiService.createCategory(request).enqueue(new Callback<CategoryResponseDTO>() {
        @Override
        public void onResponse(Call<CategoryResponseDTO> call, Response<CategoryResponseDTO> response) {
            progressBar.setVisibility(View.GONE);
            if (response.isSuccessful()) {
                Toast.makeText(AdminCategoriesActivity.this, "Category created", Toast.LENGTH_SHORT).show();
                loadCategories();
            } else {
                Toast.makeText(AdminCategoriesActivity.this, "Failed to create", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<CategoryResponseDTO> call, Throwable t) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(AdminCategoriesActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    });
}
```

```java
// CategoryApiService.java dòng 17-19
@POST("v1/categories/create")
Call<CategoryResponseDTO> createCategory(@Body CategoryRequestDTO request);
```

---

### UPDATE - Sửa category

"Bấm vào item trong list → showEditDialog() hiện dialog với tên cũ đã fill sẵn. Bấm Update → updateCategory() ở dòng 151-174 → API PUT /api/v1/categories/update/{id} → reload list."

**Code cụ thể:**

```java
// AdminCategoriesActivity.java dòng 151-174
private void updateCategory(String id, String name) {
    progressBar.setVisibility(View.VISIBLE);
    com.example.myapplication.model.category.CategoryRequestDTO request = new com.example.myapplication.model.category.CategoryRequestDTO();
    request.setName(name);

    categoryApiService.updateCategory(id, request).enqueue(new Callback<CategoryResponseDTO>() {
        @Override
        public void onResponse(Call<CategoryResponseDTO> call, Response<CategoryResponseDTO> response) {
            progressBar.setVisibility(View.GONE);
            if (response.isSuccessful()) {
                Toast.makeText(AdminCategoriesActivity.this, "Category updated", Toast.LENGTH_SHORT).show();
                loadCategories();
            } else {
                Toast.makeText(AdminCategoriesActivity.this, "Failed to update", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<CategoryResponseDTO> call, Throwable t) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(AdminCategoriesActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    });
}
```

```java
// CategoryApiService.java dòng 20-21
@PUT("v1/categories/update/{id}")
Call<CategoryResponseDTO> updateCategory(@Path("id") String id, @Body CategoryRequestDTO request);
```

---

### DELETE - Xóa category

"Bấm vào item → deleteCategory() ở dòng 176-203 hiện dialog confirm. Bấm Delete → API DELETE /api/v1/categories/{id} → reload list."

**Code cụ thể:**

```java
// AdminCategoriesActivity.java dòng 176-203
private void deleteCategory(CategoryResponseDTO category) {
    new AlertDialog.Builder(this)
            .setTitle("Delete Category")
            .setMessage("Are you sure you want to delete " + category.getName() + "?")
            .setPositiveButton("Delete", (dialog, which) -> {
                progressBar.setVisibility(View.VISIBLE);
                categoryApiService.deleteCategory(category.getId()).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            Toast.makeText(AdminCategoriesActivity.this, "Category deleted", Toast.LENGTH_SHORT).show();
                            loadCategories();
                        } else {
                            Toast.makeText(AdminCategoriesActivity.this, "Failed to delete", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(AdminCategoriesActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            })
            .setNegativeButton("Cancel", null)
            .show();
}
```

```java
// CategoryApiService.java dòng 23-24
@DELETE("v1/categories/{id}")
Call<Void> deleteCategory(@Path("id") String id);
```

**Tổng kết Categories:** "CRUD đầy đủ bằng dialog + API. Sau mỗi thao tác thành công, em luôn gọi loadCategories() để reload list và đảm bảo UI nhất quán với dữ liệu BE."

---

## 3) ACCOUNTS (Dealer Accounts)

### READ (List) - Xem danh sách dealer

"AdminAccountsListActivity dòng 74-101 gọi repo.getAll() → Repository gọi API GET /api/v1/accounts/getAll → nhận List<AccountResponseDTO>, clear list cũ, addAll mới, adapter.notifyDataSetChanged()."

**Code cụ thể:**

```java
// AdminAccountsListActivity.java dòng 74-101
private void load() {
    progressBar.setVisibility(View.VISIBLE);
    tvEmpty.setVisibility(View.GONE);
    repo.getAll(new AdminAccountsRepository.AccountsListCallback() {
        @Override
        public void onSuccess(List<AccountResponseDTO> list) {
            progressBar.setVisibility(View.GONE);
            accounts.clear();
            if (list != null) {
                accounts.addAll(list);
            }
            adapter.notifyDataSetChanged();
            if (accounts.isEmpty()) tvEmpty.setVisibility(View.VISIBLE);
        }

        @Override
        public void onError(String error) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(AdminAccountsListActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
            tvEmpty.setVisibility(View.VISIBLE);
        }
    });
}
```

```java
// AdminAccountsRepository.java dòng 23-39
public void getAll(AccountsListCallback callback) {
    api.getAll().enqueue(new Callback<List<AccountResponseDTO>>() {
        @Override
        public void onResponse(Call<List<AccountResponseDTO>> call, Response<List<AccountResponseDTO>> response) {
            if (response.isSuccessful() && response.body() != null) {
                callback.onSuccess(response.body());
            } else {
                callback.onError("Failed: " + response.code());
            }
        }

        @Override
        public void onFailure(Call<List<AccountResponseDTO>> call, Throwable t) {
            callback.onError(t.getMessage());
        }
    });
}
```

```java
// AccountApiService.java dòng 22-23
@GET("v1/accounts/getAll")
Call<List<AccountResponseDTO>> getAll();
```

---

### CREATE - Tạo dealer mới

"Bấm FAB → mở AdminCreateAccountActivity. Form có username, email, password. Bấm Create → create() ở dòng 44-75: build RegisterRequestDTO, gọi repo.createDealerByAdmin() → API POST /api/v1/accounts/save → thành công thì finish() và quay lại list."

**Code cụ thể:**

```java
// AdminCreateAccountActivity.java dòng 44-75
private void create() {
    String username = etUsername.getText() != null ? etUsername.getText().toString().trim() : "";
    String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
    String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";
    if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
        Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        return;
    }
    progressBar.setVisibility(View.VISIBLE);
    btnCreate.setEnabled(false);

    RegisterRequestDTO req = new RegisterRequestDTO();
    req.setUsername(username);
    req.setEmail(email);
    req.setPassword(password);
    repo.createDealerByAdmin(req, new AdminAccountsRepository.CreateCallback() {
        @Override
        public void onSuccess() {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(AdminCreateAccountActivity.this, "Created", Toast.LENGTH_SHORT).show();
            finish();
        }

        @Override
        public void onError(String error) {
            progressBar.setVisibility(View.GONE);
            btnCreate.setEnabled(true);
            Toast.makeText(AdminCreateAccountActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
        }
    });
}
```

```java
// AdminAccountsRepository.java dòng 41-57
public void createDealerByAdmin(RegisterRequestDTO request, CreateCallback callback) {
    api.saveByAdmin(request).enqueue(new Callback<Void>() {
        @Override
        public void onResponse(Call<Void> call, Response<Void> response) {
            if (response.isSuccessful()) {
                callback.onSuccess();
            } else {
                callback.onError("Failed: " + response.code());
            }
        }

        @Override
        public void onFailure(Call<Void> call, Throwable t) {
            callback.onError(t.getMessage());
        }
    });
}
```

```java
// AccountApiService.java dòng 19-20
@POST("v1/accounts/save")
Call<Void> saveByAdmin(@Body RegisterRequestDTO request);
```

---

### UPDATE (Toggle Active) - Bật/tắt trạng thái active

"Trong adapter AccountsAdapter ở dòng 194-237, mỗi item có nút Activate/Deactivate. Bấm vào → gọi apiService.changeAccountStatus(email, newStatus) → API PUT /api/v1/accounts/account/status?email=...&active=... → thành công thì onDataChanged.run() để reload danh sách."

**Code cụ thể:**

```java
// AdminAccountsListActivity.java dòng 194-237 (trong AccountsAdapter.onBindViewHolder)
h.btnToggleStatus.setOnClickListener(v -> {
    int currentPos = h.getAdapterPosition();
    if (currentPos == RecyclerView.NO_POSITION) return;
    
    AccountResponseDTO account = items.get(currentPos);
    h.btnToggleStatus.setEnabled(false);
    
    boolean newStatus = !account.isActive();
    apiService.changeAccountStatus(account.getEmail(), newStatus).enqueue(new Callback<Map<String, String>>() {
        @Override
        public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
            h.btnToggleStatus.setEnabled(true);
            if (response.isSuccessful()) {
                android.widget.Toast.makeText(v.getContext(), 
                    newStatus ? "Account activated" : "Account deactivated", 
                    android.widget.Toast.LENGTH_SHORT).show();
                // Reload danh sách để lấy dữ liệu mới nhất từ BE
                if (onDataChanged != null) {
                    onDataChanged.run();
                }
            } else {
                android.widget.Toast.makeText(v.getContext(), "Failed to change status", android.widget.Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onFailure(Call<Map<String, String>> call, Throwable t) {
            h.btnToggleStatus.setEnabled(true);
            android.widget.Toast.makeText(v.getContext(), "Error: " + t.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
        }
    });
});
```

```java
// AccountApiService.java dòng 40-44
@PUT("v1/accounts/account/status")
Call<Map<String, String>> changeAccountStatus(
        @Query("email") String email,
        @Query("active") boolean active
);
```

**UI:** Mỗi item hiển thị username, email, role (DEALER), status (Active/Inactive), và nút Activate/Deactivate. Sau khi toggle, list tự reload để cập nhật trạng thái.

---

### DELETE - Xóa account

"Nhấn giữ item (long click) → deleteAccount() ở dòng 103-130 hiện dialog confirm. Bấm Delete → accountApiService.delete(id) → API DELETE /api/v1/accounts/{id} → thành công thì load() để reload danh sách."

**Code cụ thể:**

```java
// AdminAccountsListActivity.java dòng 103-130
private void deleteAccount(AccountResponseDTO account) {
    new android.app.AlertDialog.Builder(this)
            .setTitle("Delete Account")
            .setMessage("Are you sure you want to delete " + account.getUsername() + "?")
            .setPositiveButton("Delete", (dialog, which) -> {
                progressBar.setVisibility(View.VISIBLE);
                accountApiService.delete(account.getId()).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            Toast.makeText(AdminAccountsListActivity.this, "Account deleted", Toast.LENGTH_SHORT).show();
                            load();
                        } else {
                            Toast.makeText(AdminAccountsListActivity.this, "Failed to delete", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(AdminAccountsListActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            })
            .setNegativeButton("Cancel", null)
            .show();
}
```

```java
// AccountApiService.java dòng 37-38
@DELETE("v1/accounts/{id}")
Call<Void> delete(@Path("id") String id);
```

**Tổng kết Accounts:** "List, tạo Dealer, bật/tắt active, xóa – đều là API bảo vệ bằng JWT token (qua RetrofitClient.createWithAuth()). FE luôn reload sau mỗi thao tác để đảm bảo UI đúng với dữ liệu BE."

---

## 4) Bảo mật và điều hướng

### JWT Authentication

"Tất cả API call đều dùng RetrofitClient.createWithAuth(context, ApiService.class) để tự động thêm JWT token vào header Authorization: Bearer <token>. Token được lưu trong SharedPrefManager sau khi login thành công."

### Điều hướng theo ROLE sau login

"LoginActivity sau khi login thành công, lưu token và role vào SharedPreferences, gọi API getByUsername để lấy accountId, rồi điều hướng: nếu role là ADMIN → DashboardAdminActivity, ngược lại → DashboardDealerActivity."

---

## 5) Câu trả lời nhanh nếu thầy hỏi vặn

**"Dữ liệu hiện ra từ đâu?"**  
→ "Từ API REST ở BE (endpoint cụ thể như /api/v1/vehicles, /api/v1/categories/getAll). FE gọi bằng Retrofit qua Repository pattern. Nhận JSON → map sang DTO → đẩy vào RecyclerView adapter → notifyDataSetChanged() để hiển thị."

**"Tại sao sau khi sửa/xóa là thấy ngay?"**  
→ "Em luôn gọi lại method load (như loadVehicles(), loadCategories(), load()) sau mỗi thao tác thành công để reload dữ liệu từ server và đồng bộ với UI."

**"Phân quyền thế nào?"**  
→ "BE dùng @PreAuthorize trên controller để kiểm tra ROLE. FE chỉ cần truyền JWT token qua RetrofitClient.createWithAuth(). Nếu không đủ quyền, BE trả 403 và FE hiển thị error."

**"Nếu BE trả khác format?"**  
→ "Em có xử lý null check và ép kiểu an toàn (ví dụ Vehicles dòng 144-154: kiểm tra v.get("price") != null trước khi cast). Nếu response body null hoặc format sai, em hiển thị Toast error và set tvEmpty visible."
