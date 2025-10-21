package com.example.myapplication.network;


import com.example.myapplication.model.category.request.CategoryRequestDTO;
import com.example.myapplication.model.category.response.CategoryResponseDTO;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface CategoryApiService {

    @GET("api/v1/categories/getAll")
    Call<List<CategoryResponseDTO>> getAllCategories();

    @GET("api/v1/categories/{id}")
    Call<CategoryResponseDTO> getCategoryById(@Path("id") String id);

    @POST("api/v1/categories/create")
    Call<CategoryResponseDTO> createCategory(@Body CategoryRequestDTO request);

    @PUT("api/v1/categories/update/{id}")
    Call<CategoryResponseDTO> updateCategory(@Path("id") String id, @Body CategoryRequestDTO request);

    @DELETE("/api/v1/categories/{id}")
    Call<Void> deleteCategory(@Path("id") String id);

}

