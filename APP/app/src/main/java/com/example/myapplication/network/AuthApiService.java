package com.example.myapplication.network;

import com.example.myapplication.model.account.request.LoginRequest;
import com.example.myapplication.model.account.response.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApiService {

    @POST("v1/auth/login") // tương ứng với backend @PostMapping("/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("v1/auth/logout") // tương ứng với backend @PostMapping("/logout")
    Call<String> logout(@Body String token);
}
