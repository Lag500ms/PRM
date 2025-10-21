package com.example.myapplication.network;

import com.example.myapplication.model.vehicle.request.VehicleRequestDTO;
import com.example.myapplication.model.vehicle.response.VehicleResponseDTO;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.*;

public interface VehicleApiService {

    // Tạo vehicle - Chỉ ADMIN
    @POST("v1/vehicles")
    Call<VehicleResponseDTO> createVehicle(@Body VehicleRequestDTO request);

    // Cập nhật vehicle - Chỉ ADMIN
    @PUT("v1/vehicles/{id}")
    Call<VehicleResponseDTO> updateVehicle(@Path("id") String id, @Body VehicleRequestDTO request);

    // Xóa vehicle - Chỉ ADMIN
    @DELETE("v1/vehicles/{id}")
    Call<Void> deleteVehicle(@Path("id") String id);

    // Lấy vehicle theo ID - ADMIN và DEALER
    @GET("v1/vehicles/{id}")
    Call<VehicleResponseDTO> getVehicleById(@Path("id") String id);

    // Lấy danh sách vehicles - ADMIN và DEALER
    @GET("v1/vehicles")
    Call<Map<String, Object>> getVehicles(
            @Query("keyword") String keyword,
            @Query("page") int page,
            @Query("size") int size);
}
