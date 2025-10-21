package com.example.myapplication.network;


import com.example.myapplication.model.vehicle.request.VehicleRequestDTO;
import com.example.myapplication.model.vehicle.response.VehicleResponseDTO;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.*;

public interface VehicleApiService {

    // Tạo vehicle
    @POST("api/v1/vehicles")
    Call<VehicleResponseDTO> createVehicle(@Body VehicleRequestDTO request);

    // Cập nhật vehicle
    @PUT("api/v1/vehicles/{id}")
    Call<VehicleResponseDTO> updateVehicle(@Path("id") String id, @Body VehicleRequestDTO request);

    // Xóa vehicle
    @DELETE("api/v1/vehicles/{id}")
    Call<Void> deleteVehicle(@Path("id") String id);

    // Lấy vehicle theo ID
    @GET("api/v1/vehicles/{id}")
    Call<VehicleResponseDTO> getVehicleById(@Path("id") String id);

    // Lấy danh sách có tìm kiếm và phân trang
    @GET("api/v1/vehicles")
    Call<Map<String, Object>> getVehicles(
            @Query("keyword") String keyword,
            @Query("page") int page,
            @Query("size") int size
    );
}

