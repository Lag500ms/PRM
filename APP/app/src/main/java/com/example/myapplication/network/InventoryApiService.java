package com.example.myapplication.network;

import com.example.myapplication.model.inventory.CreateInventoryRequest;
import com.example.myapplication.model.inventory.InventoryResponse;
import com.example.myapplication.model.inventory.ReturnVehicleToAdminRequest;
import com.example.myapplication.model.inventory.UpdateVehicleQuantityRequest;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface InventoryApiService {

    @GET("v1/dealer/inventory")
    Call<List<InventoryResponse>> getInventory();

    @GET("v1/dealer/inventory/{id}")
    Call<InventoryResponse> getInventoryById(@Path("id") String id);

    @POST("v1/dealer/inventory")
    Call<InventoryResponse> createInventory(@Body CreateInventoryRequest request);

    @PUT("v1/dealer/inventory/quantity")
    Call<InventoryResponse> updateQuantity(@Body UpdateVehicleQuantityRequest request);

    @PUT("v1/dealer/inventory/return")
    Call<InventoryResponse> returnVehicleToAdmin(@Body ReturnVehicleToAdminRequest request);

    @DELETE("v1/dealer/inventory/{id}")
    Call<Void> deleteInventory(@Path("id") String id);
}
