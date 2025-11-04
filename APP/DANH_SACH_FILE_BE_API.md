# DANH SÁCH FILE BACKEND/API TRONG PROJECT FE CHÍNH

## 📁 NETWORK FILES (10 files)

### API Services (9 files) - Định nghĩa các Retrofit API endpoints
1. `network/AuthApiService.java` - API đăng nhập, đăng ký
2. `network/AccountApiService.java` - API quản lý tài khoản (CRUD, getByUsername, updateStatus)
3. `network/CategoryApiService.java` - API quản lý danh mục xe
4. `network/VehicleApiService.java` - API quản lý xe điện (CRUD)
5. `network/InventoryApiService.java` - API quản lý kho hàng (receive, get)
6. `network/OrdersApiService.java` - API quản lý đơn hàng (CRUD, updateStatus)
7. `network/SchedulesApiService.java` - API quản lý lịch chạy thử (CRUD, updateStatus)
8. `network/DashboardApiService.java` - API dashboard cho dealer
9. `network/ChatbotApiService.java` - API chatbot

### Network Utilities (2 files)
10. `network/RetrofitClient.java` - Retrofit client configuration (BASE_URL, OkHttpClient)
11. `network/AuthInterceptor.java` - Interceptor để tự động thêm JWT token vào headers

---

## 📁 REPOSITORY FILES (10 files)

Repository pattern - Xử lý logic gọi API và callback:

1. `repository/AuthRepository.java` - Repository xử lý đăng nhập, đăng ký
2. `repository/AccountRepository.java` - Repository quản lý tài khoản
3. `repository/AdminAccountsRepository.java` - Repository admin quản lý accounts (getAll, updateStatus)
4. `repository/CategoryRepository.java` - Repository quản lý danh mục
5. `repository/VehicleRepository.java` - Repository quản lý xe điện
6. `repository/InventoryRepository.java` - Repository quản lý kho hàng
7. `repository/OrdersRepository.java` - Repository quản lý đơn hàng
8. `repository/SchedulesRepository.java` - Repository quản lý lịch chạy thử
9. `repository/DashboardRepository.java` - Repository dashboard
10. `repository/ChatbotRepository.java` - Repository chatbot

---

## 📁 MODEL FILES - Request/Response DTOs

### Account (5 files)
1. `model/account/request/LoginRequest.java` - Request đăng nhập
2. `model/account/request/RegisterRequestDTO.java` - Request đăng ký
3. `model/account/request/AccountUpdateRequestDTO.java` - Request cập nhật account
4. `model/account/response/LoginResponse.java` - Response đăng nhập (token)
5. `model/account/response/AccountResponseDTO.java` - Response thông tin account
6. `model/account/response/AccountResponsePageDTO.java` - Response phân trang accounts
7. `model/account/response/AccountDetails.java` - Chi tiết account

### Category (2 files)
8. `model/category/CategoryRequestDTO.java` - Request tạo/cập nhật category
9. `model/category/CategoryResponseDTO.java` - Response category

### Vehicle (2 files)
10. `model/vehicle/request/VehicleRequestDTO.java` - Request tạo/cập nhật vehicle
11. `model/vehicle/response/VehicleResponseDTO.java` - Response vehicle

### Inventory (4 files)
12. `model/inventory/CreateInventoryRequest.java` - Request tạo inventory
13. `model/inventory/UpdateVehicleQuantityRequest.java` - Request cập nhật số lượng
14. `model/inventory/InventoryResponse.java` - Response inventory
15. `model/inventory/VehicleItem.java` - Model item vehicle trong inventory

### Orders (4 files)
16. `model/orders/CreateOrderRequest.java` - Request tạo đơn hàng
17. `model/orders/UpdateOrderRequest.java` - Request cập nhật đơn hàng
18. `model/orders/UpdateStatusRequest.java` - Request cập nhật trạng thái
19. `model/orders/OrderResponse.java` - Response đơn hàng

### Schedules (4 files)
20. `model/schedules/CreateScheduleRequest.java` - Request tạo lịch hẹn
21. `model/schedules/UpdateScheduleRequest.java` - Request cập nhật lịch hẹn
22. `model/schedules/UpdateStatusRequest.java` - Request cập nhật trạng thái
23. `model/schedules/ScheduleResponse.java` - Response lịch hẹn

### Dashboard (9 files)
24. `model/dashboard/DashboardResponse.java` - Response dashboard tổng
25. `model/dashboard/Summary.java` - Tóm tắt thống kê
26. `model/dashboard/OrderStatusStats.java` - Thống kê trạng thái đơn hàng
27. `model/dashboard/ScheduleStatusStats.java` - Thống kê trạng thái lịch hẹn
28. `model/dashboard/MonthlyRevenueItem.java` - Doanh thu theo tháng
29. `model/dashboard/TopSellingVehicle.java` - Xe bán chạy
30. `model/dashboard/LowStockItem.java` - Xe sắp hết hàng
31. `model/dashboard/RecentOrderItem.java` - Đơn hàng gần đây
32. `model/dashboard/UpcomingScheduleItem.java` - Lịch hẹn sắp tới
33. `model/dashboard/InventoryByCategoryItem.java` - Tồn kho theo danh mục

### Chatbot (2 files)
34. `model/chatbot/ChatbotRequest.java` - Request chatbot
35. `model/chatbot/ChatbotResponse.java` - Response chatbot

### Common (1 file)
36. `model/common/PageResponse.java` - Response phân trang chung
37. `model/enums/Role.java` - Enum vai trò (ADMIN, DEALER, etc.)

---

## 📁 UTILS FILES (1 file)

1. `utils/SharedPrefManager.java` - Quản lý SharedPreferences (lưu token, username, role, userId)

---

## 📊 TỔNG KẾT

### Network Layer:
- **API Services**: 9 files (Retrofit interfaces)
- **Network Utilities**: 2 files (RetrofitClient, AuthInterceptor)
- **Tổng Network**: 11 files

### Repository Layer:
- **Repositories**: 10 files
- **Tổng Repository**: 10 files

### Model Layer (DTOs):
- **Account**: 7 files
- **Category**: 2 files
- **Vehicle**: 2 files
- **Inventory**: 4 files
- **Orders**: 4 files
- **Schedules**: 4 files
- **Dashboard**: 9 files
- **Chatbot**: 2 files
- **Common**: 2 files
- **Tổng Models**: 40 files

### Utils:
- **1 file**

---

## 📈 TỔNG CỘNG FILE BACKEND/API:
- **Network**: 11 files
- **Repository**: 10 files
- **Model DTOs**: 40 files
- **Utils**: 1 file
- **TỔNG**: **62 files**

---

## 🔗 CẤU TRÚC LIÊN KẾT

```
UI Activities
    ↓
Repository (logic, callbacks)
    ↓
API Service (Retrofit interface)
    ↓
AuthInterceptor (thêm JWT token)
    ↓
RetrofitClient (BASE_URL: http://10.0.2.2:8080/api)
    ↓
Backend Server (Spring Boot)
```

---

## 📝 LƯU Ý

- **BASE_URL**: `http://10.0.2.2:8080/api` (Android Emulator localhost)
- **Authentication**: JWT token được tự động thêm vào header qua `AuthInterceptor`
- **Token Storage**: Lưu trong `SharedPrefManager` (SharedPreferences)
- **Callback Pattern**: Repository sử dụng `RepositoryCallback<T>` để xử lý async results
- **Error Handling**: Parse error body từ `response.errorBody()` để hiển thị message

