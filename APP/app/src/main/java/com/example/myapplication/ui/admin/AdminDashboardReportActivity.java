package com.example.myapplication.ui.admin;


import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.example.myapplication.model.dashboard.DashboardResponse;
import com.example.myapplication.repository.DashboardRepository;

public class AdminDashboardReportActivity extends AppCompatActivity {

    private TextView tvSummary, tvOrders;
    private ProgressBar progressBar;
    private DashboardRepository repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_report);

        com.google.android.material.appbar.MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        repo = new DashboardRepository(this);
        tvSummary = findViewById(R.id.tvSummary);
        tvOrders = findViewById(R.id.tvOrders);
        progressBar = findViewById(R.id.progressBar);

        load();
    }

    private void load() {
        progressBar.setVisibility(View.VISIBLE);
        repo.getDashboard(new DashboardRepository.DashboardCallback() {
            @Override
            public void onSuccess(DashboardResponse d) {
                progressBar.setVisibility(View.GONE);
                if (d != null && d.summary != null) {
                    tvSummary.setText("Total Revenue: " + (d.summary.totalRevenue != null ? d.summary.totalRevenue : "0"));
                    if (d.recentOrders != null && !d.recentOrders.isEmpty()) {
                        tvOrders.setText("Recent: " + d.recentOrders.size() + " orders");
                    }
                }
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AdminDashboardReportActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }
}

