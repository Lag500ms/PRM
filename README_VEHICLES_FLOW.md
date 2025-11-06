# VEHICLES - Flow CRUD đầy đủ

## Files liên quan:
1. `AdminVehiclesListActivity.java` - Màn hình danh sách
2. `AdminVehicleFormActivity.java` - Màn hình form (create/edit)
3. `VehiclesAdapter.java` - Adapter hiển thị list
4. `VehicleRepository.java` - Gọi API
5. `VehicleApiService.java` - Interface Retrofit

---

## 🔵 READ (List) - Xem danh sách

**Flow:**
```
AdminVehiclesListActivity.onCreate()
  ↓ dòng 51
loadVehicles()
  ↓ dòng 140
vehicleRepository.getVehicles()
  ↓ VehicleRepository.java dòng 89
apiService.getVehicles() → Retrofit
  ↓ VehicleApiService.java dòng 30
GET /api/v1/vehicles
  ↓ Response JSON
onSuccess() → parse Map → VehicleResponseDTO
  ↓ dòng 159
adapter.notifyDataSetChanged()
  ↓ VehiclesAdapter.java dòng 48
onBindViewHolder() → hiển thị từng item
```

**Code chính:**

```java
// AdminVehiclesListActivity.java dòng 136-172
private void loadVehicles() {
    vehicleRepository.getVehicles(null, 0, 100, new RepositoryCallback<Map<String, Object>>() {
        @Override
        public void onSuccess(Map<String, Object> data) {
            List<Map<String, Object>> content = (List<Map<String, Object>>) data.get("content");
            vehicles.clear();
            for (Map<String, Object> v : content) {
                VehicleResponseDTO dto = new VehicleResponseDTO();
                dto.setId((String) v.get("id"));
                dto.setModel((String) v.get("model"));
                // ... map các field
                vehicles.add(dto);
            }
            adapter.notifyDataSetChanged();  // Báo RecyclerView update
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
        }
    });
}
```

```java
// VehicleApiService.java dòng 30-34
@GET("v1/vehicles")
Call<Map<String, Object>> getVehicles(
        @Query("keyword") String keyword,
        @Query("page") int page,
        @Query("size") int size);
```

---

## 🔵 READ (Detail) - Click vào item → Mở form edit

**Flow:**
```
User click vào item trong RecyclerView
  ↓ VehiclesAdapter.java dòng 88
itemView.setOnClickListener() → listener.onVehicleClick(vehicle)
  ↓ AdminVehiclesListActivity.java dòng 70-75
Intent với vehicle_id → startActivity(AdminVehicleFormActivity)
  ↓ AdminVehicleFormActivity.java dòng 70
vehicleId = getIntent().getStringExtra("vehicle_id")
  ↓ dòng 77
loadVehicle()
  ↓ dòng 112
vehicleRepository.getById(vehicleId)
  ↓ VehicleRepository.java dòng 72
apiService.getVehicleById(id)
  ↓ VehicleApiService.java dòng 26
GET /api/v1/vehicles/{id}
  ↓ Response
onSuccess() → fill các TextInputEditText
```

**Code chính:**

```java
// VehiclesAdapter.java dòng 88-92
itemView.setOnClickListener(v -> {
    listener.onVehicleClick(vehicle);  // Gọi callback
});
```

```java
// AdminVehiclesListActivity.java dòng 70-75
adapter = new VehiclesAdapter(this, vehicles, 
    vehicle -> {
        Intent intent = new Intent(this, AdminVehicleFormActivity.class);
        intent.putExtra("vehicle_id", vehicle.getId());
        startActivity(intent);
    }, ...);
```

```java
// AdminVehicleFormActivity.java dòng 109-132
private void loadVehicle() {
    vehicleRepository.getById(vehicleId, new RepositoryCallback<VehicleResponseDTO>() {
        @Override
        public void onSuccess(VehicleResponseDTO vehicle) {
            etModel.setText(vehicle.getModel());
            etVersion.setText(vehicle.getVersion());
            // ... fill các field
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
        }
    });
}
```

---

## 🟢 CREATE - Tạo xe mới

**Flow:**
```
User bấm FAB button
  ↓ AdminVehiclesListActivity.java dòng 53
startActivity(AdminVehicleFormActivity) - không có vehicle_id
  ↓ AdminVehicleFormActivity.java dòng 70
vehicleId == null → Create mode
  ↓ User nhập form → bấm Save
  ↓ dòng 134
save()
  ↓ dòng 160-177
Build VehicleRequestDTO từ form
  ↓ dòng 179
vehicleId == null → gọi create()
  ↓ dòng 180
vehicleRepository.create(request)
  ↓ VehicleRepository.java dòng 21
apiService.createVehicle(request)
  ↓ VehicleApiService.java dòng 14
POST /api/v1/vehicles
  ↓ Response
onSuccess() → Toast "Vehicle created" → finish()
  ↓ Quay lại List → onResume() → loadVehicles()
```

**Code chính:**

```java
// AdminVehicleFormActivity.java dòng 134-211
private void save() {
    // Lấy dữ liệu từ form
    String model = etModel.getText().toString().trim();
    // ... lấy các field khác
    
    VehicleRequestDTO request = new VehicleRequestDTO();
    request.setModel(model);
    // ... set các field
    
    if (vehicleId == null) {
        // CREATE
        vehicleRepository.create(request, new RepositoryCallback<VehicleResponseDTO>() {
            @Override
            public void onSuccess(VehicleResponseDTO data) {
                Toast.makeText(this, "Vehicle created", Toast.LENGTH_SHORT).show();
                finish();  // Quay lại list
            }
        });
    }
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
        }
    });
}
```

```java
// VehicleApiService.java dòng 14-15
@POST("v1/vehicles")
Call<VehicleResponseDTO> createVehicle(@Body VehicleRequestDTO request);
```

---

## 🟡 UPDATE - Sửa xe

**Flow:**
```
User click vào item → mở form với vehicle_id
  ↓ AdminVehicleFormActivity.java dòng 70
vehicleId != null → Edit mode → loadVehicle() fill form
  ↓ User sửa → bấm Save
  ↓ dòng 196
vehicleId != null → gọi update()
  ↓ dòng 196
vehicleRepository.update(vehicleId, request)
  ↓ VehicleRepository.java dòng 38
apiService.updateVehicle(id, request)
  ↓ VehicleApiService.java dòng 18
PUT /api/v1/vehicles/{id}
  ↓ Response
onSuccess() → Toast "Vehicle updated" → finish()
  ↓ Quay lại List → onResume() → loadVehicles()
```

**Code chính:**

```java
// AdminVehicleFormActivity.java dòng 196-210
else {
    // UPDATE
    vehicleRepository.update(vehicleId, request, new RepositoryCallback<VehicleResponseDTO>() {
        @Override
        public void onSuccess(VehicleResponseDTO data) {
            Toast.makeText(this, "Vehicle updated", Toast.LENGTH_SHORT).show();
            finish();
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
        }
    });
}
```

```java
// VehicleApiService.java dòng 18-19
@PUT("v1/vehicles/{id}")
Call<VehicleResponseDTO> updateVehicle(@Path("id") String id, @Body VehicleRequestDTO request);
```

---

## 🔴 DELETE - Xóa xe

**Flow:**
```
User long click vào item trong list
  ↓ VehiclesAdapter.java dòng 95
itemView.setOnLongClickListener() → deleteListener.onVehicleDelete(vehicle)
  ↓ AdminVehiclesListActivity.java dòng 111
deleteVehicle(vehicle) → hiện AlertDialog confirm
  ↓ User bấm "Delete"
  ↓ dòng 117
vehicleRepository.delete(vehicle.getId())
  ↓ VehicleRepository.java dòng 55
apiService.deleteVehicle(id)
  ↓ VehicleApiService.java dòng 22
DELETE /api/v1/vehicles/{id}
  ↓ Response
onSuccess() → Toast "Vehicle deleted" → loadVehicles() reload
```

**Code chính:**

```java
// VehiclesAdapter.java dòng 95-100
itemView.setOnLongClickListener(v -> {
    deleteListener.onVehicleDelete(vehicle);
    return true;
});
```

```java
// AdminVehiclesListActivity.java dòng 111-134
private void deleteVehicle(VehicleResponseDTO vehicle) {
    new AlertDialog.Builder(this)
            .setTitle("Delete Vehicle")
            .setMessage("Are you sure you want to delete " + vehicle.getModel() + "?")
            .setPositiveButton("Delete", (dialog, which) -> {
                vehicleRepository.delete(vehicle.getId(), new RepositoryCallback<Void>() {
                    @Override
                    public void onSuccess(Void data) {
                        Toast.makeText(this, "Vehicle deleted", Toast.LENGTH_SHORT).show();
                        loadVehicles();  // Reload list
                    }
                });
            })
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
        }
    });
}
```

```java
// VehicleApiService.java dòng 22-23
@DELETE("v1/vehicles/{id}")
Call<Void> deleteVehicle(@Path("id") String id);
```

---

## 📋 Tóm tắt flow:

**READ List:** Activity → Repository → ApiService → GET /api/v1/vehicles → parse JSON → Adapter → RecyclerView

**READ Detail:** Click item → Adapter callback → Activity mở Form → Repository → ApiService → GET /api/v1/vehicles/{id} → fill form

**CREATE:** FAB → Form (vehicleId=null) → Save → Repository → ApiService → POST /api/v1/vehicles → finish() → reload list

**UPDATE:** Click item → Form (vehicleId!=null) → Save → Repository → ApiService → PUT /api/v1/vehicles/{id} → finish() → reload list

**DELETE:** Long click → Dialog → Repository → ApiService → DELETE /api/v1/vehicles/{id} → reload list

