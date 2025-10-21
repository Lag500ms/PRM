package com.example.myapplication.network;

import com.example.myapplication.model.account.request.AccountUpdateRequestDTO;
import com.example.myapplication.model.account.request.RegisterRequestDTO;
import com.example.myapplication.model.account.response.AccountRequestDTO;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface AccountApiService {

    @POST("api/v1/accounts/register")
    Call<AccountRequestDTO> register(@Body RegisterRequestDTO request);

    @POST("api/v1/accounts/save")
    Call<AccountRequestDTO> saveByAdmin(@Body RegisterRequestDTO request);

    @GET("api/v1/accounts/getAll")
    Call<List<AccountRequestDTO>> getAll();

    @GET("api/v1/accounts/{id}")
    Call<AccountRequestDTO> getById(@Path("id") String id);

    @GET("api/v1/accounts/by-email/{email}")
    Call<AccountRequestDTO> getByEmail(@Path("email") String email);

    @GET("api/v1/accounts/by-username/{username}")
    Call<AccountRequestDTO> getByUsername(@Path("username") String username);

    @PUT("api/v1/accounts/update")
    Call<AccountRequestDTO> update(@Body AccountUpdateRequestDTO request);

    @DELETE("api/v1/accounts/{id}")
    Call<Void> delete(@Path("id") String id);
}

