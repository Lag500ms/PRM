package com.example.myapplication.network;

import com.example.myapplication.model.account.request.AccountUpdateRequestDTO;
import com.example.myapplication.model.account.request.RegisterRequestDTO;
import com.example.myapplication.model.account.response.AccountResponseDTO;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.*;

public interface AccountApiService {

    @POST("api/v1/accounts/register")
    Call<Void> register(@Body RegisterRequestDTO request);

    @POST("api/v1/accounts/save")
    Call<Void> saveByAdmin(@Body RegisterRequestDTO request);

    @GET("api/v1/accounts/getAll")
    Call<List<AccountResponseDTO>> getAll();

    @GET("api/v1/accounts/{id}")
    Call<AccountResponseDTO> getById(@Path("id") String id);

    @GET("api/v1/accounts/by-email/{email}")
    Call<AccountResponseDTO> getByEmail(@Path("email") String email);

    @GET("api/v1/accounts/by-username/{username}")
    Call<AccountResponseDTO> getByUsername(@Path("username") String username);

    @PUT("api/v1/accounts/update")
    Call<AccountResponseDTO> update(@Body AccountUpdateRequestDTO request);

    @DELETE("api/v1/accounts/{id}")
    Call<Void> delete(@Path("id") String id);

    @PUT("api/v1/accounts/account/status")
    Call<Map<String, String>> changeAccountStatus(
            @Query("email") String email,
            @Query("active") boolean active
    );

}

