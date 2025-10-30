package com.example.myapplication.ui.admin;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.model.account.response.AccountResponseDTO;
import com.example.myapplication.network.AccountApiService;
import com.example.myapplication.network.RetrofitClient;
import com.example.myapplication.repository.AdminAccountsRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdminAccountsListActivity extends AppCompatActivity {

    private RecyclerView rvAccounts;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private FloatingActionButton fabAdd;
    private AdminAccountsRepository repo;
    private AccountApiService accountApiService;
    private AccountsAdapter adapter;
    private List<AccountResponseDTO> accounts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_accounts_list);

        repo = new AdminAccountsRepository(this);
        accountApiService = RetrofitClient.createWithAuth(this, AccountApiService.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        rvAccounts = findViewById(R.id.rvAccounts);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);
        fabAdd = findViewById(R.id.fabAdd);

        adapter = new AccountsAdapter(accounts, accountApiService, this::load, this::deleteAccount);
        rvAccounts.setLayoutManager(new LinearLayoutManager(this));
        rvAccounts.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> startActivity(new Intent(this, AdminCreateAccountActivity.class)));

        load();
    }

    private void load() {
        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);
        repo.getAll(new AdminAccountsRepository.AccountsListCallback() {
            @Override
            public void onSuccess(List<AccountResponseDTO> list) {
                progressBar.setVisibility(View.GONE);
                accounts.clear();
                if (list != null) accounts.addAll(list);
                adapter.notifyDataSetChanged();
                if (accounts.isEmpty()) tvEmpty.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AdminAccountsListActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                tvEmpty.setVisibility(View.VISIBLE);
            }
        });
    }

    private void deleteAccount(AccountResponseDTO account) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete " + account.getUsername() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    progressBar.setVisibility(View.VISIBLE);
                    accountApiService.delete(account.getId()).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            progressBar.setVisibility(View.GONE);
                            if (response.isSuccessful()) {
                                Toast.makeText(AdminAccountsListActivity.this, "Account deleted", Toast.LENGTH_SHORT).show();
                                load();
                            } else {
                                Toast.makeText(AdminAccountsListActivity.this, "Failed to delete", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(AdminAccountsListActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

class AccountsAdapter extends RecyclerView.Adapter<AccountsAdapter.VH> {

    private final List<AccountResponseDTO> items;
    private final AccountApiService apiService;
    private final Runnable onDataChanged;
    private final OnDeleteListener onDeleteListener;

    interface OnDeleteListener {
        void onDelete(AccountResponseDTO account);
    }

    AccountsAdapter(List<AccountResponseDTO> items, AccountApiService apiService, Runnable onDataChanged, OnDeleteListener onDeleteListener) {
        this.items = items;
        this.apiService = apiService;
        this.onDataChanged = onDataChanged;
        this.onDeleteListener = onDeleteListener;
    }

    @Override
    public VH onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
        View v = android.view.LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(VH h, int pos) {
        AccountResponseDTO a = items.get(pos);
        h.tvUsername.setText(a.getUsername());
        h.tvEmail.setText(a.getEmail());
        h.tvRole.setText("DEALER");
        
        // Update UI theo status hiện tại
        updateStatusUI(h, a);
        
        // Check if this is an admin account
        boolean isAdminAccount = a.getEmail() != null && a.getEmail().contains("admin@example.com");
        if (isAdminAccount) {
            h.btnToggleStatus.setEnabled(false);
            h.btnToggleStatus.setText("Protected");
            h.btnToggleStatus.setOnClickListener(v -> 
                android.widget.Toast.makeText(v.getContext(), "Cannot change admin account status", android.widget.Toast.LENGTH_SHORT).show()
            );
            h.itemView.setOnLongClickListener(null);
            return;
        }
        
        // Long click to delete
        h.itemView.setOnLongClickListener(v -> {
            if (onDeleteListener != null) {
                onDeleteListener.onDelete(a);
            }
            return true;
        });
        
        // Set click listener for non-admin accounts
        h.btnToggleStatus.setOnClickListener(v -> {
            int currentPos = h.getAdapterPosition();
            if (currentPos == RecyclerView.NO_POSITION) return;
            
            AccountResponseDTO account = items.get(currentPos);
            h.btnToggleStatus.setEnabled(false);
            
            boolean newStatus = !account.isActive();
            apiService.changeAccountStatus(account.getEmail(), newStatus).enqueue(new Callback<Map<String, String>>() {
                @Override
                public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                    h.btnToggleStatus.setEnabled(true);
                    if (response.isSuccessful()) {
                        // Update data
                        account.setActive(newStatus);
                        // Update UI immediately
                        updateStatusUI(h, account);
                        android.widget.Toast.makeText(v.getContext(), 
                            newStatus ? "Account activated" : "Account deactivated", 
                            android.widget.Toast.LENGTH_SHORT).show();
                    } else {
                        // Parse error message from response
                        String errorMsg = "Failed to change status";
                        try {
                            if (response.errorBody() != null) {
                                String errorBody = response.errorBody().string();
                                if (errorBody.contains("Cannot change status of admin account")) {
                                    errorMsg = "Cannot change admin account status";
                                }
                            }
                        } catch (Exception e) {
                            // Use default error message
                        }
                        android.widget.Toast.makeText(v.getContext(), errorMsg, android.widget.Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Map<String, String>> call, Throwable t) {
                    h.btnToggleStatus.setEnabled(true);
                    android.widget.Toast.makeText(v.getContext(), "Error: " + t.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
    
    private void updateStatusUI(VH h, AccountResponseDTO a) {
        h.tvStatus.setText(a.isActive() ? "Active" : "Inactive");
        h.btnToggleStatus.setText(a.isActive() ? "Deactivate" : "Activate");
    }

    @Override
    public int getItemCount() { return items != null ? items.size() : 0; }

    class VH extends RecyclerView.ViewHolder {
        TextView tvUsername, tvEmail, tvRole, tvStatus;
        com.google.android.material.button.MaterialButton btnToggleStatus;
        VH(View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvRole = itemView.findViewById(R.id.tvRole);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnToggleStatus = itemView.findViewById(R.id.btnToggleStatus);
        }
    }
}


