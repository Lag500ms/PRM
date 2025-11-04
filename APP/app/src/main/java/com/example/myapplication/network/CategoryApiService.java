package com.example.myapplication.network;

import com.example.myapplication.model.category.CategoryRequestDTO;
import com.example.myapplication.model.category.CategoryResponseDTO;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface CategoryApiService {

    @GET("v1/categories/getAll")
    Call<List<CategoryResponseDTO>> getAllCategories();

    @GET("v1/categories/{id}")
    Call<CategoryResponseDTO> getCategoryById(@Path("id") String id);

    @POST("v1/categories/create")
    Call<CategoryResponseDTO> createCategory(@Body CategoryRequestDTO request);

    @PUT("v1/categories/update/{id}")
    Call<CategoryResponseDTO> updateCategory(@Path("id") String id, @Body CategoryRequestDTO request);

    @DELETE("v1/categories/{id}")
    Call<Void> deleteCategory(@Path("id") String id);
}
