## CRUD cho Admin — Vehicles, Categories, Accounts (ngắn gọn, có chú thích)

Chỉ giữ 3 phần này theo yêu cầu. Mỗi ý: “file + dòng → làm gì → gọi API nào → UI ra sao”.

### VEHICLES

• READ (List): lấy danh sách xe, đổ RecyclerView
- `AdminVehiclesListActivity` 133–169: gọi `vehicleRepository.getVehicles(...)` → lấy Page từ API → map sang `List<VehicleResponseDTO>` → `adapter.notifyDataSetChanged()`.

```133:169:APP/app/src/main/java/com/example/myapplication/ui/admin/vehicles/AdminVehiclesListActivity.java
vehicleRepository.getVehicles(null, 0, 100, new VehicleRepository.RepositoryCallback<Map<String, Object>>() { ... })
```

- `VehicleRepository` 89–104: bọc Retrofit → gọi API `GET /api/v1/vehicles`.

```89:104:APP/app/src/main/java/com/example/myapplication/repository/VehicleRepository.java
public void getVehicles(String keyword, int page, int size, RepositoryCallback<Map<String, Object>> callback) { ... }
```

- `VehicleController` 61–80: BE endpoint `GET /api/v1/vehicles` (ADMIN/DEALER) trả `Page<VehicleResponseDTO>`.

```61:80:BE/src/main/java/prm/be/controller/VehicleController.java
@GetMapping
@PreAuthorize("hasRole('ADMIN') or hasRole('DEALER')")
public ResponseEntity<Page<VehicleResponseDTO>> getVehicles(...){ ... }
```

• READ (Detail): lấy chi tiết 1 xe khi mở form edit
- `VehicleRepository` 72–87: gọi `GET /api/v1/vehicles/{id}`.

```72:87:APP/app/src/main/java/com/example/myapplication/repository/VehicleRepository.java
public void getById(String id, RepositoryCallback<VehicleResponseDTO> callback) { ... }
```

- `VehicleController` 50–55: BE `GET /api/v1/vehicles/{id}` (ADMIN/DEALER).

```50:55:BE/src/main/java/prm/be/controller/VehicleController.java
@GetMapping("/{id}")
@PreAuthorize("hasRole('ADMIN') or hasRole('DEALER')")
public ResponseEntity<VehicleResponseDTO> getById(@PathVariable("id") String id) { ... }
```

• CREATE: tạo xe mới từ form
- `VehicleRepository` 21–36: gọi `POST /api/v1/vehicles` với body `VehicleRequestDTO`.

```21:36:APP/app/src/main/java/com/example/myapplication/repository/VehicleRepository.java
public void create(VehicleRequestDTO request, RepositoryCallback<VehicleResponseDTO> callback) { ... }
```

- `VehicleController` 24–29: BE `POST /api/v1/vehicles` (ADMIN).

```24:29:BE/src/main/java/prm/be/controller/VehicleController.java
@PostMapping
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<VehicleResponseDTO> create(@RequestBody VehicleRequestDTO dto) { ... }
```

• UPDATE: cập nhật xe từ form edit
- `VehicleRepository` 38–53: gọi `PUT /api/v1/vehicles/{id}` với body mới.

```38:53:APP/app/src/main/java/com/example/myapplication/repository/VehicleRepository.java
public void update(String id, VehicleRequestDTO request, RepositoryCallback<VehicleResponseDTO> callback) { ... }
```

- `VehicleController` 31–38: BE `PUT /api/v1/vehicles/{id}` (ADMIN).

```31:38:BE/src/main/java/prm/be/controller/VehicleController.java
@PutMapping("/{id}")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<VehicleResponseDTO> update(@PathVariable("id") String id, @RequestBody VehicleRequestDTO dto) { ... }
```

• DELETE: xóa xe từ list (nhấn giữ item → Confirm)
- `AdminVehiclesListActivity` 108–131: confirm → `vehicleRepository.delete(id)` → `loadVehicles()`.

```108:131:APP/app/src/main/java/com/example/myapplication/ui/admin/vehicles/AdminVehiclesListActivity.java
// Confirm → repository.delete(id) → loadVehicles()
```

- `VehicleRepository` 55–70: gọi `DELETE /api/v1/vehicles/{id}`.

```55:70:APP/app/src/main/java/com/example/myapplication/repository/VehicleRepository.java
public void delete(String id, RepositoryCallback<Void> callback) { ... }
```

- `VehicleController` 40–45: BE `DELETE /api/v1/vehicles/{id}` (ADMIN).

```40:45:BE/src/main/java/prm/be/controller/VehicleController.java
@DeleteMapping("/{id}")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<Void> delete(@PathVariable("id") String id) { ... }
```

---

### CATEGORIES

• READ (List): lấy tất cả categories (đổ RecyclerView)
- `AdminCategoriesActivity` 63–89: gọi `categoryApiService.getAllCategories()` → set list → `adapter.notifyDataSetChanged()`.

```63:89:APP/app/src/main/java/com/example/myapplication/ui/admin/categories/AdminCategoriesActivity.java
categoryApiService.getAllCategories().enqueue(new Callback<List<CategoryResponseDTO>>() { ... })
```

- `CategoryApiService` 11–12: `GET /api/v1/categories/getAll`.

```11:12:APP/app/src/main/java/com/example/myapplication/network/CategoryApiService.java
@GET("v1/categories/getAll") Call<List<CategoryResponseDTO>> getAllCategories();
```

- `CategoryController` 69–77: BE `GET /api/v1/categories/getAll` (authenticated).

```69:77:BE/src/main/java/prm/be/controller/CategoryController.java
@GetMapping("/getAll")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() { ... }
```

• CREATE: tạo category từ dialog
- `AdminCategoriesActivity` 126–149: build request → `createCategory` → reload list.

```126:149:APP/app/src/main/java/com/example/myapplication/ui/admin/categories/AdminCategoriesActivity.java
categoryApiService.createCategory(request).enqueue(new Callback<CategoryResponseDTO>() { ... loadCategories(); })
```

- `CategoryApiService` 17–19: `POST /api/v1/categories/create`.

```17:19:APP/app/src/main/java/com/example/myapplication/network/CategoryApiService.java
@POST("v1/categories/create") Call<CategoryResponseDTO> createCategory(@Body CategoryRequestDTO request);
```

- `CategoryController` 82–88: BE `POST /api/v1/categories/create` (ADMIN).

```82:88:BE/src/main/java/prm/be/controller/CategoryController.java
@PostMapping("/create") @PreAuthorize("hasRole('ADMIN')") public ResponseEntity<CategoryResponseDTO> createCategory(...) { ... }
```

• UPDATE: sửa tên category từ dialog
- `AdminCategoriesActivity` 151–174: build request → `updateCategory` → reload list.

```151:174:APP/app/src/main/java/com/example/myapplication/ui/admin/categories/AdminCategoriesActivity.java
categoryApiService.updateCategory(id, request).enqueue(new Callback<CategoryResponseDTO>() { ... loadCategories(); })
```

- `CategoryApiService` 20–21: `PUT /api/v1/categories/update/{id}`.

```20:21:APP/app/src/main/java/com/example/myapplication/network/CategoryApiService.java
@PUT("v1/categories/update/{id}") Call<CategoryResponseDTO> updateCategory(@Path("id") String id, @Body CategoryRequestDTO request);
```

- `CategoryController` 54–64: BE `PUT /api/v1/categories/update/{id}` (ADMIN).

```54:64:BE/src/main/java/prm/be/controller/CategoryController.java
@PutMapping("/update/{id}") @PreAuthorize("hasRole('ADMIN')") public ResponseEntity<CategoryResponseDTO> updateCategory(...) { ... }
```

• DELETE: xóa category từ list (confirm dialog)
- `AdminCategoriesActivity` 176–203: confirm → `deleteCategory(id)` → reload.

```176:203:APP/app/src/main/java/com/example/myapplication/ui/admin/categories/AdminCategoriesActivity.java
categoryApiService.deleteCategory(category.getId()).enqueue(new Callback<Void>() { ... loadCategories(); })
```

- `CategoryApiService` 23–24: `DELETE /api/v1/categories/{id}`.

```23:24:APP/app/src/main/java/com/example/myapplication/network/CategoryApiService.java
@DELETE("v1/categories/{id}") Call<Void> deleteCategory(@Path("id") String id);
```

- `CategoryController` 42–49: BE `DELETE /api/v1/categories/{id}` (ADMIN).

```42:49:BE/src/main/java/prm/be/controller/CategoryController.java
@DeleteMapping("/{id}") @PreAuthorize("hasRole('ADMIN')") public ResponseEntity<CategoryResponseDTO> delete(@PathVariable("id") String id) { ... }
```

---

### ACCOUNTS (Dealer Accounts)

• READ (List): xem danh sách dealer
- `AdminAccountsListActivity` 74–101: `repo.getAll()` → set list → `adapter.notifyDataSetChanged()`.

```74:101:APP/app/src/main/java/com/example/myapplication/ui/admin/AdminAccountsListActivity.java
repo.getAll(new AdminAccountsRepository.AccountsListCallback() { ... adapter.notifyDataSetChanged(); })
```

- `AdminAccountsRepository` 23–39: gọi API `GET /api/v1/accounts/getAll`.

```23:39:APP/app/src/main/java/com/example/myapplication/repository/AdminAccountsRepository.java
public void getAll(AccountsListCallback callback) { api.getAll().enqueue(...); }
```

- `AccountController` 39–48: BE `GET /api/v1/accounts/getAll` (ADMIN).

```39:48:BE/src/main/java/prm/be/controller/AccountController.java
@GetMapping("/getAll") @PreAuthorize("hasRole('ADMIN')") public ResponseEntity<List<AccountResponseDTO>> getAll() { ... }
```

• CREATE: tạo dealer mới từ form
- `AdminCreateAccountActivity` 44–75: build `RegisterRequestDTO` → `repo.createDealerByAdmin(req)` → finish.

```44:75:APP/app/src/main/java/com/example/myapplication/ui/admin/AdminCreateAccountActivity.java
repo.createDealerByAdmin(req, new AdminAccountsRepository.CreateCallback() { ... finish(); })
```

- `AdminAccountsRepository` 41–57: gọi `POST /api/v1/accounts/save`.

```41:57:APP/app/src/main/java/com/example/myapplication/repository/AdminAccountsRepository.java
public void createDealerByAdmin(RegisterRequestDTO request, CreateCallback callback) { api.saveByAdmin(request).enqueue(...); }
```

- `AccountController` 33–37: BE `POST /api/v1/accounts/save` (ADMIN).

```33:37:BE/src/main/java/prm/be/controller/AccountController.java
@PostMapping("/save") @PreAuthorize("hasRole('ADMIN')") public void saveByAdmin(@Valid @RequestBody RegisterRequestDTO request) { ... }
```

• UPDATE: bật/tắt active/inactive
- `AdminAccountsListActivity` 164–238 (Adapter): click nút → `changeAccountStatus(email, newStatus)` → `onDataChanged.run()` để reload danh sách.

```164:238:APP/app/src/main/java/com/example/myapplication/ui/admin/AdminAccountsListActivity.java
apiService.changeAccountStatus(account.getEmail(), newStatus).enqueue(new Callback<Map<String, String>>() { ... onDataChanged.run(); })
```

- `AccountController` 82–93: BE `PUT /api/v1/accounts/account/status?email=...&active=...` (ADMIN).

```82:93:BE/src/main/java/prm/be/controller/AccountController.java
@PutMapping("/account/status") @PreAuthorize("hasRole('ADMIN')") public ResponseEntity<Map<String, String>> changeAccountStatus(...) { ... }
```

• DELETE: xóa account (nhấn giữ item → Confirm)
- `AdminAccountsListActivity` 103–130: confirm → `accountApiService.delete(id)` → reload list.

```103:130:APP/app/src/main/java/com/example/myapplication/ui/admin/AdminAccountsListActivity.java
accountApiService.delete(account.getId()).enqueue(new Callback<Void>() { ... load(); })
```

- `AdminAccountsRepository` 77–93: wrapper `deleteAccount(id)`.

```77:93:APP/app/src/main/java/com/example/myapplication/repository/AdminAccountsRepository.java
public void deleteAccount(String id, DeleteCallback callback) { api.delete(id).enqueue(...); }
```

- `AccountController` 75–80: BE `DELETE /api/v1/accounts/{id}` (ADMIN).

```75:80:BE/src/main/java/prm/be/controller/AccountController.java
@DeleteMapping("/{id}") @PreAuthorize("hasRole('ADMIN')") public ResponseEntity<Void> delete(@PathVariable String id) { ... }
```


