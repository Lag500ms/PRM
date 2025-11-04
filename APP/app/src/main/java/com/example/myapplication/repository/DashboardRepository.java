package com.example.myapplication.repository;

import android.content.Context;
import com.example.myapplication.model.dashboard.DashboardResponse;
import com.example.myapplication.network.DashboardApiService;
import com.example.myapplication.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardRepository {

    private final DashboardApiService api;

    public DashboardRepository(Context context) {
        this.api = RetrofitClient.createWithAuth(context, DashboardApiService.class);
    }

    public void getDashboard(DashboardCallback callback) {
        api.getDealerDashboard().enqueue(new Callback<DashboardResponse>() {
            @Override
            public void onResponse(Call<DashboardResponse> call, Response<DashboardResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<DashboardResponse> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public interface DashboardCallback {
        void onSuccess(DashboardResponse response);
        void onError(String error);
    }
}
