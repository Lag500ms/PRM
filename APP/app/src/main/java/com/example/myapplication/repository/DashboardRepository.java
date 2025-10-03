package com.example.myapplication.repository;

import android.content.Context;
import com.example.myapplication.model.dashboard.DashboardResponse;
import com.example.myapplication.network.DashboardApiService;
import com.example.myapplication.network.RetrofitClient;
import java.io.IOException;

public class DashboardRepository {

    private final DashboardApiService api;

    public DashboardRepository(Context context) {
        this.api = RetrofitClient.createWithAuth(context, DashboardApiService.class);
    }

    public DashboardResponse getDashboard() throws IOException {
        return api.getDashboard().execute().body();
    }
}
