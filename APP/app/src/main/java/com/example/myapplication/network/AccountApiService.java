package com.example.myapplication.network;

import com.example.myapplication.model.account.request.AccountUpdateRequestDTO;
import com.example.myapplication.model.account.request.RegisterRequestDTO;
import com.example.myapplication.model.account.response.AccountResponseDTO;
import com.example.myapplication.model.account.response.AccountResponsePageDTO;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.*;

public interface AccountApiService {

    @POST("v1/accounts/register")
    Call<Void> register(@Body RegisterRequestDTO request);

    @POST("v1/accounts/save")
    Call<Void> saveByAdmin(@Body RegisterRequestDTO request);

    @GET("v1/accounts/getAll")
    Call<List<AccountResponseDTO>> getAll();

    @GET("v1/accounts/{id}")
    Call<AccountResponseDTO> getById(@Path("id") String id);

    @GET("v1/accounts/by-email/{email}")
    Call<AccountResponseDTO> getByEmail(@Path("email") String email);

    @GET("v1/accounts/by-username/{username}")
    Call<AccountResponseDTO> getByUsername(@Path("username") String username);

    @PUT("v1/accounts/update")
    Call<AccountResponseDTO> update(@Body AccountUpdateRequestDTO request);

    @DELETE("v1/accounts/{id}")
    Call<Void> delete(@Path("id") String id);

    @PUT("v1/accounts/account/status")
    Call<Map<String, String>> changeAccountStatus(
            @Query("email") String email,
            @Query("active") boolean active
    );

    @GET("v1/accounts/search")
    Call<AccountResponsePageDTO> searchAccountsByUsername(
            @Query("keyword") String keyword,
            @Query("page") int page,
            @Query("size") int size
    );


}

