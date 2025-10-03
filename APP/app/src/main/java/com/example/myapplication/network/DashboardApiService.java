package com.example.myapplication.network;

import com.example.myapplication.model.dashboard.DashboardResponse;
import retrofit2.Call;
import retrofit2.http.GET;

public interface DashboardApiService {

    @GET("v1/dealer/dashboard")
    Call<DashboardResponse> getDashboard();
}
