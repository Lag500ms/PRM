package com.example.myapplication.ui.schedules;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.example.myapplication.model.schedules.ScheduleResponse;
import com.example.myapplication.model.schedules.UpdateStatusRequest;
import com.example.myapplication.repository.SchedulesRepository;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

public class ScheduleDetailActivity extends AppCompatActivity {

    public static final String EXTRA_SCHEDULE_ID = "schedule_id";

    private TextView tvCustomer, tvPhone, tvAddress, tvDateTime, tvStatus;
    private MaterialButton btnEdit, btnDelete, btnApprove, btnCancel;
    private ProgressBar progressBar;
    private SchedulesRepository schedulesRepository;
    private String scheduleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_detail);

        // Setup toolbar with back button
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        schedulesRepository = new SchedulesRepository(this);
        tvCustomer = findViewById(R.id.tvCustomer);
        tvPhone = findViewById(R.id.tvPhone);
        tvAddress = findViewById(R.id.tvAddress);
        tvDateTime = findViewById(R.id.tvDateTime);
        tvStatus = findViewById(R.id.tvStatus);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);
        btnApprove = findViewById(R.id.btnApprove);
        btnCancel = findViewById(R.id.btnCancel);
        progressBar = findViewById(R.id.progressBar);

        scheduleId = getIntent().getStringExtra(EXTRA_SCHEDULE_ID);
        load();

        btnEdit.setOnClickListener(v -> {
            Intent i = new Intent(this, ScheduleFormActivity.class);
            i.putExtra(ScheduleFormActivity.EXTRA_SCHEDULE_ID, scheduleId);
            startActivity(i);
        });

        btnDelete.setOnClickListener(v -> delete());
        btnApprove.setOnClickListener(v -> updateStatus("CONFIRMED"));
        btnCancel.setOnClickListener(v -> updateStatus("CANCELLED"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        load();
    }

    private void load() {
        progressBar.setVisibility(View.VISIBLE);
        schedulesRepository.getById(scheduleId, new SchedulesRepository.ScheduleDetailCallback() {
            @Override
            public void onSuccess(ScheduleResponse s) {
                progressBar.setVisibility(View.GONE);
                if (s != null) {
                    tvCustomer.setText(s.customer);
                    tvPhone.setText(s.phone);
                    tvAddress.setText(s.address);
                    tvDateTime.setText(s.dateTime);
                    tvStatus.setText(s.status);
                }
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ScheduleDetailActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateStatus(String newStatus) {
        progressBar.setVisibility(View.VISIBLE);
        UpdateStatusRequest req = new UpdateStatusRequest();
        req.id = scheduleId;
        req.status = newStatus;
        schedulesRepository.updateStatus(req, new SchedulesRepository.ScheduleDetailCallback() {
            @Override
            public void onSuccess(ScheduleResponse response) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ScheduleDetailActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                load();
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ScheduleDetailActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void delete() {
        progressBar.setVisibility(View.VISIBLE);
        schedulesRepository.delete(scheduleId, new SchedulesRepository.DeleteCallback() {
            @Override
            public void onSuccess() {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ScheduleDetailActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ScheduleDetailActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }
}


