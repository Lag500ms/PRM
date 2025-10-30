package com.example.myapplication.ui.admin.categories;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.model.category.CategoryRequestDTO;
import com.example.myapplication.model.category.CategoryResponseDTO;
import com.example.myapplication.network.CategoryApiService;
import com.example.myapplication.network.RetrofitClient;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

public class AdminCategoriesActivity extends AppCompatActivity {

    private RecyclerView rvCategories;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private FloatingActionButton fabAdd;
    private CategoryApiService categoryApiService;
    private CategoriesAdapter adapter;
    private List<CategoryResponseDTO> categories = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_categories);

        categoryApiService = RetrofitClient.createWithAuth(this, CategoryApiService.class);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        rvCategories = findViewById(R.id.rvCategories);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);
        fabAdd = findViewById(R.id.fabAdd);

        adapter = new CategoriesAdapter(categories, this::editCategory, this::deleteCategory);
        rvCategories.setLayoutManager(new LinearLayoutManager(this));
        rvCategories.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> showAddDialog());

        loadCategories();
    }

    private void loadCategories() {
        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);

        categoryApiService.getAllCategories().enqueue(new Callback<List<CategoryResponseDTO>>() {
            @Override
            public void onResponse(Call<List<CategoryResponseDTO>> call, Response<List<CategoryResponseDTO>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    categories.clear();
                    categories.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    if (categories.isEmpty()) {
                        tvEmpty.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(AdminCategoriesActivity.this, "Failed to load", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<CategoryResponseDTO>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AdminCategoriesActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddDialog() {
        EditText etName = new EditText(this);
        etName.setHint("Category Name");
        etName.setPadding(50, 30, 50, 30);

        new AlertDialog.Builder(this)
                .setTitle("Add Category")
                .setView(etName)
                .setPositiveButton("Add", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    if (!name.isEmpty()) {
                        createCategory(name);
                    } else {
                        Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void createCategory(String name) {
        CategoryRequestDTO request = new CategoryRequestDTO();
        request.setName(name);

        progressBar.setVisibility(View.VISIBLE);
        categoryApiService.createCategory(request).enqueue(new Callback<CategoryResponseDTO>() {
            @Override
            public void onResponse(Call<CategoryResponseDTO> call, Response<CategoryResponseDTO> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(AdminCategoriesActivity.this, "Category created", Toast.LENGTH_SHORT).show();
                    loadCategories();
                } else {
                    Toast.makeText(AdminCategoriesActivity.this, "Failed to create", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CategoryResponseDTO> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AdminCategoriesActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void editCategory(CategoryResponseDTO category) {
        EditText etName = new EditText(this);
        etName.setText(category.getName());
        etName.setPadding(50, 30, 50, 30);

        new AlertDialog.Builder(this)
                .setTitle("Edit Category")
                .setView(etName)
                .setPositiveButton("Update", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    if (!name.isEmpty()) {
                        updateCategory(category.getId(), name);
                    } else {
                        Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateCategory(String id, String name) {
        CategoryRequestDTO request = new CategoryRequestDTO();
        request.setName(name);

        progressBar.setVisibility(View.VISIBLE);
        categoryApiService.updateCategory(id, request).enqueue(new Callback<CategoryResponseDTO>() {
            @Override
            public void onResponse(Call<CategoryResponseDTO> call, Response<CategoryResponseDTO> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(AdminCategoriesActivity.this, "Category updated", Toast.LENGTH_SHORT).show();
                    loadCategories();
                } else {
                    Toast.makeText(AdminCategoriesActivity.this, "Failed to update", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CategoryResponseDTO> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AdminCategoriesActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteCategory(CategoryResponseDTO category) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Category")
                .setMessage("Delete " + category.getName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    progressBar.setVisibility(View.VISIBLE);
                    categoryApiService.deleteCategory(category.getId()).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            progressBar.setVisibility(View.GONE);
                            if (response.isSuccessful()) {
                                Toast.makeText(AdminCategoriesActivity.this, "Category deleted", Toast.LENGTH_SHORT).show();
                                loadCategories();
                            } else {
                                Toast.makeText(AdminCategoriesActivity.this, "Failed to delete", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(AdminCategoriesActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}

