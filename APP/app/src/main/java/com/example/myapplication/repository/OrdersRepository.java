package com.example.myapplication.repository;

import android.content.Context;
import com.example.myapplication.model.common.PageResponse;
import com.example.myapplication.model.orders.CreateOrderRequest;
import com.example.myapplication.model.orders.UpdateOrderRequest;
import com.example.myapplication.model.orders.UpdateStatusRequest;
import com.example.myapplication.model.orders.OrderResponse;
import com.example.myapplication.network.OrdersApiService;
import com.example.myapplication.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrdersRepository {

    private final OrdersApiService api;

    public OrdersRepository(Context context) {
        this.api = RetrofitClient.createWithAuth(context, OrdersApiService.class);
    }

    public void list(int page, int size, String status, OrdersListCallback callback) {
        api.listOrders(page, size, status).enqueue(new Callback<PageResponse<OrderResponse>>() {
            @Override
            public void onResponse(Call<PageResponse<OrderResponse>> call, Response<PageResponse<OrderResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String errorMsg = "Failed: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += " - " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                        // ignore
                    }
                    android.util.Log.e("OrdersRepository", "Error: " + errorMsg);
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<PageResponse<OrderResponse>> call, Throwable t) {
                android.util.Log.e("OrdersRepository", "Failure: " + t.getMessage(), t);
                callback.onError(t.getMessage());
            }
        });
    }

    public void getById(String id, OrderDetailCallback callback) {
        api.getOrder(id).enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void create(CreateOrderRequest request, OrderDetailCallback callback) {
        api.createOrder(request).enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void update(UpdateOrderRequest request, OrderDetailCallback callback) {
        api.updateOrder(request).enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void updateStatus(UpdateStatusRequest request, OrderDetailCallback callback) {
        api.updateStatus(request).enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void delete(String id, DeleteCallback callback) {
        api.deleteOrder(id).enqueue(new Callback<Void>() {
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

    public interface OrdersListCallback {
        void onSuccess(PageResponse<OrderResponse> response);
        void onError(String error);
    }

    public interface OrderDetailCallback {
        void onSuccess(OrderResponse response);
        void onError(String error);
    }

    public interface DeleteCallback {
        void onSuccess();
        void onError(String error);
    }
}
