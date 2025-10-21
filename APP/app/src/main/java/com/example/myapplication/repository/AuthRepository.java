package com.example.myapplication.repository;

import com.example.myapplication.model.account.request.LoginRequest;
import com.example.myapplication.model.account.response.LoginResponse;
import com.example.myapplication.network.AuthApiService;
import com.example.myapplication.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {

    private final AuthApiService authApi;

    public AuthRepository() {
        authApi = RetrofitClient.create(AuthApiService.class);
    }

    public void login(String username, String password, LoginCallback callback) {
        LoginRequest request = new LoginRequest(username, password);
        authApi.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Login failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void logout(String token, LogoutCallback callback) {
        authApi.logout(token).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Logout failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public interface LoginCallback {
        void onSuccess(LoginResponse response);

        void onError(String error);
    }

    public interface LogoutCallback {
        void onSuccess(String message);

        void onError(String error);
    }
}
