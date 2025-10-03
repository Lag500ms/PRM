package com.example.myapplication.repository;

import android.content.Context;
import com.example.myapplication.model.inventory.CreateInventoryRequest;
import com.example.myapplication.model.inventory.UpdateVehicleQuantityRequest;
import com.example.myapplication.network.InventoryApiService;
import com.example.myapplication.network.RetrofitClient;
import java.io.IOException;
import java.util.List;
import com.example.myapplication.model.inventory.InventoryResponse;
import retrofit2.Call;
import retrofit2.Response;

public class InventoryRepository {

    private final InventoryApiService api;

    public InventoryRepository(Context context) {
        this.api = RetrofitClient.createWithAuth(context, InventoryApiService.class);
    }

    public List<InventoryResponse> getInventories() throws IOException {
        return api.getInventory().execute().body();
    }

    public InventoryResponse getInventoryById(String id) throws IOException {
        return api.getInventoryById(id).execute().body();
    }

    public InventoryResponse createInventory(CreateInventoryRequest request) throws IOException {
        return api.createInventory(request).execute().body();
    }

    public InventoryResponse updateQuantity(UpdateVehicleQuantityRequest request) throws IOException {
        return api.updateQuantity(request).execute().body();
    }

    public boolean deleteInventory(String id) throws IOException {
        Response<Void> resp = api.deleteInventory(id).execute();
        return resp.isSuccessful();
    }
}
