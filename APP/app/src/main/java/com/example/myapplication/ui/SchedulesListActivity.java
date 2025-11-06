package com.example.myapplication.ui.schedules;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.model.common.PageResponse;
import com.example.myapplication.model.schedules.ScheduleResponse;
import com.example.myapplication.repository.SchedulesRepository;
import com.example.myapplication.ui.schedules.adapter.SchedulesAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import java.util.ArrayList;
import java.util.List;
import android.content.Intent;

public class SchedulesListActivity extends AppCompatActivity {

    private RecyclerView rvSchedules;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private FloatingActionButton fabAddSchedule;
    private AutoCompleteTextView statusFilterDropdown;
    private SchedulesRepository schedulesRepository;
    private SchedulesAdapter adapter;
    private List<ScheduleResponse> schedules = new ArrayList<>();
    private int currentPage = 0;
    private final int pageSize = 10;
    private String selectedStatus = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedules_list);

        schedulesRepository = new SchedulesRepository(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initViews();
        setupStatusFilter();
        setupRecyclerView();
        loadSchedules();

        fabAddSchedule.setOnClickListener(v -> {
            startActivity(new Intent(this, ScheduleFormActivity.class));
        });
    }

    private void initViews() {
        rvSchedules = findViewById(R.id.rvSchedules);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);
        fabAddSchedule = findViewById(R.id.fabAddSchedule);
        statusFilterDropdown = findViewById(R.id.statusFilterDropdown);
    }

    private void setupStatusFilter() {
        String[] statusOptions = {"All", "PENDING", "CONFIRMED", "COMPLETED", "CANCELLED"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, statusOptions);
        statusFilterDropdown.setAdapter(adapter);
        statusFilterDropdown.setText("All", false);

        statusFilterDropdown.setOnItemClickListener((parent, view, position, id) -> {
            String selected = statusOptions[position];
            selectedStatus = selected.equals("All") ? null : selected;
            loadSchedules();
        });
    }

    private void setupRecyclerView() {
        adapter = new SchedulesAdapter(this, schedules, schedule -> {
            Intent i = new Intent(this, ScheduleDetailActivity.class);
            i.putExtra(ScheduleDetailActivity.EXTRA_SCHEDULE_ID, schedule.id);
            startActivity(i);
        });
        rvSchedules.setLayoutManager(new LinearLayoutManager(this));
        rvSchedules.setAdapter(adapter);
    }

    private void loadSchedules() {
        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);

        schedulesRepository.list(currentPage, pageSize, null, null, selectedStatus, new SchedulesRepository.SchedulesListCallback() {
            @Override
            public void onSuccess(PageResponse<ScheduleResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response != null && response.content != null) {
                    schedules.clear();
                    schedules.addAll(response.content);
                    adapter.notifyDataSetChanged();
                    if (schedules.isEmpty()) {
                        tvEmpty.setVisibility(View.VISIBLE);
                    }
                } else {
                    tvEmpty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(SchedulesListActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                tvEmpty.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh list when coming back from detail screen
        loadSchedules();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}


