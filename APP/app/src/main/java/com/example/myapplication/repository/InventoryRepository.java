package com.example.myapplication.repository;

import android.content.Context;
import com.example.myapplication.model.inventory.CreateInventoryRequest;
import com.example.myapplication.model.inventory.ReturnVehicleToAdminRequest;
import com.example.myapplication.model.inventory.UpdateVehicleQuantityRequest;
import com.example.myapplication.network.InventoryApiService;
import com.example.myapplication.network.RetrofitClient;
import java.util.List;
import com.example.myapplication.model.inventory.InventoryResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InventoryRepository {

    private final InventoryApiService api;

    public InventoryRepository(Context context) {
        this.api = RetrofitClient.createWithAuth(context, InventoryApiService.class);
    }

    public void getInventories(InventoryListCallback callback) {
        api.getInventory().enqueue(new Callback<List<InventoryResponse>>() {
            @Override
            public void onResponse(Call<List<InventoryResponse>> call, Response<List<InventoryResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<InventoryResponse>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getInventoryById(String id, InventoryDetailCallback callback) {
        api.getInventoryById(id).enqueue(new Callback<InventoryResponse>() {
            @Override
            public void onResponse(Call<InventoryResponse> call, Response<InventoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<InventoryResponse> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void createInventory(CreateInventoryRequest request, InventoryDetailCallback callback) {
        api.createInventory(request).enqueue(new Callback<InventoryResponse>() {
            @Override
            public void onResponse(Call<InventoryResponse> call, Response<InventoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<InventoryResponse> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void updateQuantity(UpdateVehicleQuantityRequest request, InventoryDetailCallback callback) {
        api.updateQuantity(request).enqueue(new Callback<InventoryResponse>() {
            @Override
            public void onResponse(Call<InventoryResponse> call, Response<InventoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<InventoryResponse> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void deleteInventory(String id, DeleteCallback callback) {
        api.deleteInventory(id).enqueue(new Callback<Void>() {
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

    public interface InventoryListCallback {
        void onSuccess(List<InventoryResponse> response);
        void onError(String error);
    }

    public interface InventoryDetailCallback {
        void onSuccess(InventoryResponse response);
        void onError(String error);
    }

    public interface DeleteCallback {
        void onSuccess();
        void onError(String error);
    }
}
