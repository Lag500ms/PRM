package com.example.myapplication.ui;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.model.dashboard.DashboardResponse;
import com.example.myapplication.network.DashboardApiService;
import com.example.myapplication.network.RetrofitClient;
import com.example.myapplication.repository.AuthRepository;
import com.example.myapplication.ui.chatbot.ChatbotActivity;
import com.example.myapplication.ui.InventoryActivity;
import com.example.myapplication.ui.orders.OrdersListActivity;
import com.example.myapplication.ui.schedules.SchedulesListActivity;
import com.example.myapplication.utils.SharedPrefManager;

import java.text.NumberFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardDealerActivity extends AppCompatActivity {

    private LinearLayout statsSection;
    private TextView tvTotalOrders, tvTotalRevenue, tvTotalVehicles, tvPendingSchedules;
    private DashboardApiService dashboardApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_dealer);

        dashboardApiService = RetrofitClient.createWithAuth(this, DashboardApiService.class);

        statsSection = findViewById(R.id.statsSection);
        tvTotalOrders = findViewById(R.id.tvTotalOrders);
        tvTotalRevenue = findViewById(R.id.tvTotalRevenue);
        tvTotalVehicles = findViewById(R.id.tvTotalVehicles);
        tvPendingSchedules = findViewById(R.id.tvPendingSchedules);

        loadDashboard();

        // Orders button
        findViewById(R.id.btnOrders).setOnClickListener(v -> {
            startActivity(new Intent(this, OrdersListActivity.class));
        });

        // Schedules button
        findViewById(R.id.btnSchedules).setOnClickListener(v -> {
            startActivity(new Intent(this, SchedulesListActivity.class));
        });

        // Inventory button
        findViewById(R.id.btnInventory).setOnClickListener(v -> {
            startActivity(new Intent(this, InventoryActivity.class));
        });

        // Chatbot button
        findViewById(R.id.btnChat).setOnClickListener(v -> {
            startActivity(new Intent(this, ChatbotActivity.class));
        });

        // Profile avatar - Show popup menu
        ImageView imgProfile = findViewById(R.id.imgProfile);
        imgProfile.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(this, v);
            popupMenu.getMenuInflater().inflate(R.menu.menu_profile_popup, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menu_logout) {
                    new AuthRepository(this).logout(() -> {
                        startActivity(new Intent(this, LoginActivity.class));
                        finish();
                    });
                    return true;
                }
                return false;
            });
            popupMenu.show();
        });
    }

    private void loadDashboard() {
        dashboardApiService.getDealerDashboard().enqueue(new Callback<DashboardResponse>() {
            @Override
            public void onResponse(Call<DashboardResponse> call, Response<DashboardResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DashboardResponse data = response.body();
                    if (data.summary != null) {
                        statsSection.setVisibility(View.VISIBLE);

                        tvTotalOrders.setText(String.valueOf(data.summary.totalOrders));

                        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
                        String revenue = data.summary.totalRevenue != null
                                ? currencyFormat.format(data.summary.totalRevenue)
                                : "$0";
                        tvTotalRevenue.setText(revenue);

                        tvTotalVehicles.setText(String.valueOf(data.summary.totalVehiclesInInventory));
                        tvPendingSchedules.setText(String.valueOf(data.summary.pendingSchedules));
                    }
                } else {
                    Toast.makeText(DashboardDealerActivity.this, "Failed to load dashboard", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DashboardResponse> call, Throwable t) {
                Toast.makeText(DashboardDealerActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
