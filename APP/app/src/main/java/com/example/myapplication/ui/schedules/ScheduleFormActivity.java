package com.example.myapplication.ui.schedules;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.example.myapplication.model.account.response.AccountResponseDTO;
import com.example.myapplication.model.account.response.AccountResponsePageDTO;
import com.example.myapplication.model.schedules.CreateScheduleRequest;
import com.example.myapplication.model.schedules.ScheduleResponse;
import com.example.myapplication.model.schedules.UpdateScheduleRequest;
import com.example.myapplication.network.AccountApiService;
import com.example.myapplication.network.RetrofitClient;
import com.example.myapplication.repository.SchedulesRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScheduleFormActivity extends AppCompatActivity {

    public static final String EXTRA_SCHEDULE_ID = "schedule_id";

    private AutoCompleteTextView actvCustomer;
    private TextInputEditText etPhone, etAddress, etDateTime;
    private MaterialButton btnSave;
    private ProgressBar progressBar;
    private SchedulesRepository schedulesRepository;
    private AccountApiService accountApiService;
    private String scheduleId;
    private Calendar selectedDateTime = Calendar.getInstance();

    // Store customer data
    private List<AccountResponseDTO> customerList = new ArrayList<>();
    private Map<String, AccountResponseDTO> customerMap = new HashMap<>();
    private String selectedCustomerFullName = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_form);

        schedulesRepository = new SchedulesRepository(this);
        accountApiService = RetrofitClient.create(AccountApiService.class);

        actvCustomer = findViewById(R.id.actvCustomer);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        etDateTime = findViewById(R.id.etDateTime);
        btnSave = findViewById(R.id.btnSave);
        progressBar = findViewById(R.id.progressBar);

        // Load customer list from API
        loadCustomers();

        scheduleId = getIntent().getStringExtra(EXTRA_SCHEDULE_ID);
        if (scheduleId != null) {
            load(scheduleId);
        }

        // Setup customer selection listener
        actvCustomer.setOnItemClickListener((parent, view, position, id) -> {
            String selectedName = (String) parent.getItemAtPosition(position);
            AccountResponseDTO selectedCustomer = customerMap.get(selectedName);

            if (selectedCustomer != null) {
                selectedCustomerFullName = selectedCustomer.getDetails() != null
                    ? selectedCustomer.getDetails().getFullName()
                    : selectedCustomer.getUsername();

                // Auto-fill phone and address
                String phone = selectedCustomer.getDetails() != null
                    ? selectedCustomer.getDetails().getPhone()
                    : "";
                String address = selectedCustomer.getDetails() != null
                    ? selectedCustomer.getDetails().getAddress()
                    : "";

                etPhone.setText(phone);
                etAddress.setText(address);
            }
        });

        // Click on field or icon to open picker
        etDateTime.setOnClickListener(v -> showDateTimePicker());
        
        // Also handle icon click
        com.google.android.material.textfield.TextInputLayout dateTimeLayout = findViewById(R.id.etDateTime).getParent().getParent() instanceof com.google.android.material.textfield.TextInputLayout 
            ? (com.google.android.material.textfield.TextInputLayout) etDateTime.getParent().getParent() 
            : null;
        if (dateTimeLayout != null) {
            dateTimeLayout.setEndIconOnClickListener(v -> showDateTimePicker());
        }
        
        btnSave.setOnClickListener(v -> save());
    }

    private void loadCustomers() {
        progressBar.setVisibility(View.VISIBLE);

        // Call searchAccountsPublic with keyword "customer"
        accountApiService.searchAccountsPublic("customer", 0, 100).enqueue(new Callback<AccountResponsePageDTO>() {
            @Override
            public void onResponse(Call<AccountResponsePageDTO> call, Response<AccountResponsePageDTO> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    customerList = response.body().getContent();

                    // Create list of customer names for dropdown
                    List<String> customerDisplayNames = new ArrayList<>();
                    customerMap.clear();

                    for (AccountResponseDTO customer : customerList) {
                        String fullName = customer.getDetails() != null && customer.getDetails().getFullName() != null
                            ? customer.getDetails().getFullName()
                            : customer.getUsername();

                        // Display format: "FullName (username)" to avoid duplicate names
                        String displayName = fullName + " (" + customer.getUsername() + ")";

                        customerDisplayNames.add(displayName);
                        customerMap.put(displayName, customer);
                    }

                    // Setup AutoCompleteTextView adapter
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        ScheduleFormActivity.this,
                        android.R.layout.simple_dropdown_item_1line,
                        customerDisplayNames
                    );
                    actvCustomer.setAdapter(adapter);

                    android.util.Log.d("ScheduleFormActivity", "Loaded " + customerDisplayNames.size() + " customers");
                } else {
                    Toast.makeText(ScheduleFormActivity.this, "Failed to load customers", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AccountResponsePageDTO> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ScheduleFormActivity.this, "Error loading customers: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                android.util.Log.e("ScheduleFormActivity", "Failed to load customers", t);
            }
        });
    }

    private void showDateTimePicker() {
        // First show date picker
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDateTime.set(Calendar.YEAR, year);
                    selectedDateTime.set(Calendar.MONTH, month);
                    selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    
                    // Then show time picker
                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                            this,
                            (timeView, hourOfDay, minute) -> {
                                selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                selectedDateTime.set(Calendar.MINUTE, minute);
                                selectedDateTime.set(Calendar.SECOND, 0);
                                
                                // Format: yyyy-MM-ddTHH:mm:ss
                                String formattedDateTime = String.format(Locale.US, "%04d-%02d-%02dT%02d:%02d:%02d",
                                        selectedDateTime.get(Calendar.YEAR),
                                        selectedDateTime.get(Calendar.MONTH) + 1,
                                        selectedDateTime.get(Calendar.DAY_OF_MONTH),
                                        selectedDateTime.get(Calendar.HOUR_OF_DAY),
                                        selectedDateTime.get(Calendar.MINUTE),
                                        selectedDateTime.get(Calendar.SECOND));
                                
                                etDateTime.setText(formattedDateTime);
                            },
                            selectedDateTime.get(Calendar.HOUR_OF_DAY),
                            selectedDateTime.get(Calendar.MINUTE),
                            true
                    );
                    timePickerDialog.show();
                },
                selectedDateTime.get(Calendar.YEAR),
                selectedDateTime.get(Calendar.MONTH),
                selectedDateTime.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void load(String id) {
        progressBar.setVisibility(View.VISIBLE);
        schedulesRepository.getById(id, new SchedulesRepository.ScheduleDetailCallback() {
            @Override
            public void onSuccess(ScheduleResponse s) {
                progressBar.setVisibility(View.GONE);
                if (s != null) {
                    actvCustomer.setText(s.customer, false);
                    selectedCustomerFullName = s.customer;
                    etPhone.setText(s.phone);
                    etAddress.setText(s.address);
                    etDateTime.setText(s.dateTime);
                }
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ScheduleFormActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void save() {
        String customer = actvCustomer.getText() != null ? actvCustomer.getText().toString().trim() : "";
        String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
        String address = etAddress.getText() != null ? etAddress.getText().toString().trim() : "";
        String dateTime = etDateTime.getText() != null ? etDateTime.getText().toString().trim() : "";

        if (customer.isEmpty() || phone.isEmpty() || address.isEmpty() || dateTime.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        SchedulesRepository.ScheduleDetailCallback callback = new SchedulesRepository.ScheduleDetailCallback() {
            @Override
            public void onSuccess(ScheduleResponse response) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ScheduleFormActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);
                Toast.makeText(ScheduleFormActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
            }
        };

        if (scheduleId == null) {
            CreateScheduleRequest req = new CreateScheduleRequest();
            req.customer = selectedCustomerFullName != null ? selectedCustomerFullName : customer;
            req.phone = phone;
            req.address = address;
            req.dateTime = dateTime;
            schedulesRepository.create(req, callback);
        } else {
            UpdateScheduleRequest req = new UpdateScheduleRequest();
            req.id = scheduleId;
            req.customer = selectedCustomerFullName != null ? selectedCustomerFullName : customer;
            req.phone = phone;
            req.address = address;
            req.dateTime = dateTime;
            schedulesRepository.update(req, callback);
        }
    }
}

