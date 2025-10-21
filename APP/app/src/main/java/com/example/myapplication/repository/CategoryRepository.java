package com.example.myapplication.repository;


import com.example.myapplication.model.category.request.CategoryRequestDTO;
import com.example.myapplication.model.category.response.CategoryResponseDTO;
import com.example.myapplication.network.CategoryApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryRepository {

    private final CategoryApiService apiService;

    public CategoryRepository(CategoryApiService apiService) {
        this.apiService = apiService;
    }

    // Get all
    public void getAll(RepositoryCallback<List<CategoryResponseDTO>> callback) {
        apiService.getAllCategories().enqueue(new Callback<List<CategoryResponseDTO>>() {
            @Override
            public void onResponse(Call<List<CategoryResponseDTO>> call, Response<List<CategoryResponseDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<CategoryResponseDTO>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    // Get by id
    public void getById(String id, RepositoryCallback<CategoryResponseDTO> callback) {
        apiService.getCategoryById(id).enqueue(new Callback<CategoryResponseDTO>() {
            @Override
            public void onResponse(Call<CategoryResponseDTO> call, Response<CategoryResponseDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<CategoryResponseDTO> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    // Create
    public void create(CategoryRequestDTO request, RepositoryCallback<CategoryResponseDTO> callback) {
        apiService.createCategory(request).enqueue(new Callback<CategoryResponseDTO>() {
            @Override
            public void onResponse(Call<CategoryResponseDTO> call, Response<CategoryResponseDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<CategoryResponseDTO> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    // Update
    public void update(String id, CategoryRequestDTO request, RepositoryCallback<CategoryResponseDTO> callback) {
        apiService.updateCategory(id, request).enqueue(new Callback<CategoryResponseDTO>() {
            @Override
            public void onResponse(Call<CategoryResponseDTO> call, Response<CategoryResponseDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<CategoryResponseDTO> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    // Delete
    public void delete(String id, RepositoryCallback<Void> callback) {
        apiService.deleteCategory(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    // Generic callback interface
    public interface RepositoryCallback<T> {
        void onSuccess(T data);
        void onError(String errorMessage);
    }
}

