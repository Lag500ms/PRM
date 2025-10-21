package com.example.myapplication.repository;

import com.example.myapplication.model.vehicle.request.VehicleRequestDTO;
import com.example.myapplication.model.vehicle.response.VehicleResponseDTO;
import com.example.myapplication.network.VehicleApiService;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VehicleRepository {

    private final VehicleApiService apiService;

    public VehicleRepository(VehicleApiService apiService) {
        this.apiService = apiService;
    }

    public void create(VehicleRequestDTO request, RepositoryCallback<VehicleResponseDTO> callback) {
        apiService.createVehicle(request).enqueue(new Callback<VehicleResponseDTO>() {
            @Override
            public void onResponse(Call<VehicleResponseDTO> call, Response<VehicleResponseDTO> response) {
                if (response.isSuccessful() && response.body() != null)
                    callback.onSuccess(response.body());
                else
                    callback.onError("Error: " + response.code());
            }

            @Override
            public void onFailure(Call<VehicleResponseDTO> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void update(String id, VehicleRequestDTO request, RepositoryCallback<VehicleResponseDTO> callback) {
        apiService.updateVehicle(id, request).enqueue(new Callback<VehicleResponseDTO>() {
            @Override
            public void onResponse(Call<VehicleResponseDTO> call, Response<VehicleResponseDTO> response) {
                if (response.isSuccessful() && response.body() != null)
                    callback.onSuccess(response.body());
                else
                    callback.onError("Error: " + response.code());
            }

            @Override
            public void onFailure(Call<VehicleResponseDTO> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void delete(String id, RepositoryCallback<Void> callback) {
        apiService.deleteVehicle(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful())
                    callback.onSuccess(null);
                else
                    callback.onError("Error: " + response.code());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getById(String id, RepositoryCallback<VehicleResponseDTO> callback) {
        apiService.getVehicleById(id).enqueue(new Callback<VehicleResponseDTO>() {
            @Override
            public void onResponse(Call<VehicleResponseDTO> call, Response<VehicleResponseDTO> response) {
                if (response.isSuccessful() && response.body() != null)
                    callback.onSuccess(response.body());
                else
                    callback.onError("Error: " + response.code());
            }

            @Override
            public void onFailure(Call<VehicleResponseDTO> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getVehicles(String keyword, int page, int size, RepositoryCallback<Map<String, Object>> callback) {
        apiService.getVehicles(keyword, page, size).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null)
                    callback.onSuccess(response.body());
                else
                    callback.onError("Error: " + response.code());
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public interface RepositoryCallback<T> {
        void onSuccess(T data);
        void onError(String errorMessage);
    }
}

