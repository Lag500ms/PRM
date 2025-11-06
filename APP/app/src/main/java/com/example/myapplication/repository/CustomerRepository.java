package com.example.myapplication.repository;

import android.content.Context;
import com.example.myapplication.model.account.request.FullRegisterRequestDTO;
import com.example.myapplication.model.account.response.AccountResponseDTO;
import com.example.myapplication.model.customer.CreateCustomerRequest;
import com.example.myapplication.network.AccountApiService;
import com.example.myapplication.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerRepository {

    private final AccountApiService api;
    private final AccountApiService publicApi;

    public CustomerRepository(Context context) {
        // Use authenticated API for admin operations
        this.api = RetrofitClient.createWithAuth(context, AccountApiService.class);
        // Use public API for search operations
        this.publicApi = RetrofitClient.create(AccountApiService.class);
    }

    /**
     * Tạo customer mới thông qua API register-full (đăng ký với đầy đủ thông tin)
     * username = "customer_" + timestamp để tránh trùng lặp
     * password = "password1" (đáp ứng validation: ít nhất 8 ký tự, có chữ và số)
     */
    public void createCustomer(CreateCustomerRequest request, CustomerCallback callback) {
        // Tạo username unique bằng cách thêm timestamp
        String uniqueUsername = "customer_" + System.currentTimeMillis();

        android.util.Log.d("CustomerRepository", "Creating customer with username: " + uniqueUsername);
        android.util.Log.d("CustomerRepository", "Email: " + request.email);
        android.util.Log.d("CustomerRepository", "FullName: " + request.fullName);
        android.util.Log.d("CustomerRepository", "Phone: " + request.phone);
        android.util.Log.d("CustomerRepository", "Address: " + request.address);

        // Tạo FullRegisterRequestDTO với đầy đủ thông tin
        FullRegisterRequestDTO registerRequest = new FullRegisterRequestDTO();
        registerRequest.setUsername(uniqueUsername);
        registerRequest.setPassword("password1"); // Password hợp lệ (8 ký tự, có chữ và số)
        registerRequest.setEmail(request.email);
        registerRequest.setFullName(request.fullName);
        registerRequest.setPhoneNumber(request.phone);
        registerRequest.setAddress(request.address);
        registerRequest.setCreatedByAdmin(true); // Set to true since admin is creating the customer

        android.util.Log.d("CustomerRepository", "Request object created:");
        android.util.Log.d("CustomerRepository", "  - phoneNumber: " + registerRequest.getPhoneNumber());
        android.util.Log.d("CustomerRepository", "  - address: " + registerRequest.getAddress());
        android.util.Log.d("CustomerRepository", "Calling register-full API with authentication...");

        // Gọi API register-full (chỉ cần 1 lần gọi)
        api.registerWithFullInfo(registerRequest).enqueue(new Callback<AccountResponseDTO>() {
            @Override
            public void onResponse(Call<AccountResponseDTO> call, Response<AccountResponseDTO> response) {
                android.util.Log.d("CustomerRepository", "========== API RESPONSE ==========");
                android.util.Log.d("CustomerRepository", "Response code: " + response.code());
                android.util.Log.d("CustomerRepository", "Response message: " + response.message());
                android.util.Log.d("CustomerRepository", "Response successful: " + response.isSuccessful());

                if (response.isSuccessful() && response.body() != null) {
                    android.util.Log.d("CustomerRepository", "Customer created successfully with full info!");
                    android.util.Log.d("CustomerRepository", "Account ID: " + response.body().getId());
                    android.util.Log.d("CustomerRepository", "==================================");
                    callback.onSuccess();
                } else if (response.code() == 404) {
                    // API chưa có, fallback sang phương pháp cũ
                    android.util.Log.w("CustomerRepository", "register-full API not found (404), using fallback method...");
                    android.util.Log.w("CustomerRepository", "==================================");
                    createCustomerFallback(uniqueUsername, request, callback);
                } else {
                    android.util.Log.e("CustomerRepository", "Failed with code: " + response.code());

                    // Log chi tiết error body
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            android.util.Log.e("CustomerRepository", "Error body length: " + errorBody.length());
                            android.util.Log.e("CustomerRepository", "Full error body: " + errorBody);
                        } else {
                            android.util.Log.e("CustomerRepository", "Error body is null");
                        }
                    } catch (Exception e) {
                        android.util.Log.e("CustomerRepository", "Failed to read error body: " + e.getMessage());
                    }

                    String errorMsg = parseErrorMessage(response);
                    android.util.Log.e("CustomerRepository", "Parsed error message: " + errorMsg);
                    android.util.Log.e("CustomerRepository", "==================================");
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<AccountResponseDTO> call, Throwable t) {
                android.util.Log.e("CustomerRepository", "========== API FAILURE ==========");
                android.util.Log.e("CustomerRepository", "Network failure: " + t.getMessage(), t);
                android.util.Log.e("CustomerRepository", "Exception class: " + t.getClass().getName());
                android.util.Log.e("CustomerRepository", "==================================");
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    /**
     * Phương pháp fallback: dùng register cũ khi register-full không có
     */
    private void createCustomerFallback(String username, CreateCustomerRequest request, CustomerCallback callback) {
        android.util.Log.d("CustomerRepository", "Using fallback: register only (without full info)");

        com.example.myapplication.model.account.request.RegisterRequestDTO simpleRequest =
            new com.example.myapplication.model.account.request.RegisterRequestDTO();
        simpleRequest.setUsername(username);
        simpleRequest.setPassword("password1");
        simpleRequest.setEmail(request.email);
        simpleRequest.setCreatedByAdmin(false);

        api.register(simpleRequest).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    android.util.Log.d("CustomerRepository", "Fallback register successful!");
                    android.util.Log.w("CustomerRepository", "Note: fullName, phone, address not saved (API limitation)");
                    callback.onSuccess();
                } else {
                    String errorMsg = parseErrorMessage(response);
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    /**
     * Parse error message từ response
     */
    private String parseErrorMessage(Response<?> response) {
        try {
            android.util.Log.d("CustomerRepository", "Parsing error for response code: " + response.code());

            if (response.errorBody() != null) {
                String errorBody = response.errorBody().string();
                android.util.Log.e("CustomerRepository", "Error body in parser: " + errorBody);

                // Kiểm tra error body rỗng
                if (errorBody.isEmpty()) {
                    return "Server error (code " + response.code() + "): No error details provided";
                }

                if (errorBody.contains("email") || errorBody.contains("Email")) {
                    return "Email already exists";
                } else if (errorBody.contains("username") || errorBody.contains("Username")) {
                    return "Username already exists";
                } else if (errorBody.contains("password") || errorBody.contains("Password")) {
                    return "Password must be at least 8 characters with letters and numbers";
                } else if (response.code() == 404) {
                    return "API endpoint not found. Please ensure backend is updated.";
                } else if (response.code() == 500) {
                    return "Server error: " + errorBody;
                } else {
                    return "Error (code " + response.code() + "): " + errorBody;
                }
            } else {
                return "Error code " + response.code() + ": " + response.message();
            }
        } catch (Exception e) {
            android.util.Log.e("CustomerRepository", "Error parsing exception: " + e.getMessage());
            return "Failed to create customer (code " + response.code() + "): " + e.getMessage();
        }
    }

    /**
     * Callback interface cho create customer
     */
    public interface CustomerCallback {
        void onSuccess();
        void onError(String error);
    }

    /**
     * Search customers using searchAccountsPublic API with "customer" keyword
     * This is a public endpoint, no authentication required
     */
    public void searchCustomers(int page, int size, CustomerSearchCallback callback) {
        android.util.Log.d("CustomerRepository", "Searching customers with keyword: customer, page: " + page + ", size: " + size);

        publicApi.searchAccountsPublic("customer", page, size).enqueue(new Callback<com.example.myapplication.model.account.response.AccountResponsePageDTO>() {
            @Override
            public void onResponse(Call<com.example.myapplication.model.account.response.AccountResponsePageDTO> call, Response<com.example.myapplication.model.account.response.AccountResponsePageDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    android.util.Log.d("CustomerRepository", "Search successful! Found " + response.body().getContent().size() + " customers");
                    callback.onSuccess(response.body());
                } else {
                    android.util.Log.e("CustomerRepository", "Search failed with code: " + response.code());
                    String errorMsg = parseErrorMessage(response);
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<com.example.myapplication.model.account.response.AccountResponsePageDTO> call, Throwable t) {
                android.util.Log.e("CustomerRepository", "Search network failure: " + t.getMessage());
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    /**
     * Callback interface for search customers
     */
    public interface CustomerSearchCallback {
        void onSuccess(com.example.myapplication.model.account.response.AccountResponsePageDTO response);
        void onError(String error);
    }

    /**
     * Update customer using updatePublic API (public endpoint, no authentication required)
     */
    public void updateCustomer(String id, String fullName, String phone, String address, CustomerCallback callback) {
        android.util.Log.d("CustomerRepository", "Updating customer with ID: " + id);

        // Create AccountDetails
        com.example.myapplication.model.account.response.AccountDetails details =
            new com.example.myapplication.model.account.response.AccountDetails();
        details.setFullName(fullName);
        details.setPhone(phone);
        details.setAddress(address);

        // Create AccountUpdateRequestDTO
        com.example.myapplication.model.account.request.AccountUpdateRequestDTO updateRequest =
            new com.example.myapplication.model.account.request.AccountUpdateRequestDTO(id, null, null, details);

        publicApi.updatePublic(updateRequest).enqueue(new Callback<AccountResponseDTO>() {
            @Override
            public void onResponse(Call<AccountResponseDTO> call, Response<AccountResponseDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    android.util.Log.d("CustomerRepository", "Customer updated successfully!");
                    callback.onSuccess();
                } else {
                    android.util.Log.e("CustomerRepository", "Update failed with code: " + response.code());
                    String errorMsg = parseErrorMessage(response);
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<AccountResponseDTO> call, Throwable t) {
                android.util.Log.e("CustomerRepository", "Update network failure: " + t.getMessage());
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    /**
     * Delete customer using deletePublic API (public endpoint, no authentication required)
     */
    public void deleteCustomer(String id, CustomerCallback callback) {
        android.util.Log.d("CustomerRepository", "Deleting customer with ID: " + id);

        publicApi.deletePublic(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    android.util.Log.d("CustomerRepository", "Customer deleted successfully!");
                    callback.onSuccess();
                } else {
                    android.util.Log.e("CustomerRepository", "Delete failed with code: " + response.code());
                    String errorMsg = parseErrorMessage(response);
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                android.util.Log.e("CustomerRepository", "Delete network failure: " + t.getMessage());
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
}
