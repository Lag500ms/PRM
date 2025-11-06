package com.example.myapplication.ui.admin.categories;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.model.category.CategoryResponseDTO;
import com.example.myapplication.network.CategoryApiService;
import com.example.myapplication.network.RetrofitClient;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * AdminCategoriesActivity - Màn hình quản lý categories (CRUD đầy đủ)
 * 
 * Chức năng:
 * - Hiển thị danh sách categories trong RecyclerView
 * - FAB button → dialog tạo category mới (CREATE)
 * - Click nút Edit → dialog sửa category (UPDATE)
 * - Click nút Delete → dialog xác nhận xóa (DELETE)
 */
public class AdminCategoriesActivity extends AppCompatActivity {

    private RecyclerView rvCategories;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private FloatingActionButton fabAdd;
    private CategoryApiService categoryApiService;
    private CategoriesAdapter adapter;
    private List<CategoryResponseDTO> categories = new ArrayList<>();

    /**
     * onCreate() - Khởi tạo Activity: setup views, adapter, load categories
     */
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

        adapter = new CategoriesAdapter(categories, this::showEditDialog, this::deleteCategory);
        rvCategories.setLayoutManager(new LinearLayoutManager(this));
        rvCategories.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> showCreateDialog());

        loadCategories();
    }

    /**
     * loadCategories() - Load danh sách categories từ API → hiển thị trong RecyclerView
     */
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
                }
            }

            @Override
            public void onFailure(Call<List<CategoryResponseDTO>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                tvEmpty.setVisibility(View.VISIBLE);
                Toast.makeText(AdminCategoriesActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * showEditDialog() - Hiện dialog để sửa category (fill sẵn tên cũ)
     */
    private void showEditDialog(CategoryResponseDTO category) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_category_form, null);
        TextInputEditText etName = dialogView.findViewById(R.id.etCategoryName);
        etName.setText(category.getName());

        new AlertDialog.Builder(this)
                .setTitle("Edit Category")
                .setView(dialogView)
                .setPositiveButton("Update", (dialog, which) -> {
                    String name = etName.getText() != null ? etName.getText().toString().trim() : "";
                    if (!name.isEmpty()) {
                        updateCategory(category.getId(), name);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * updateCategory() - Sửa category: gọi API PUT → reload list
     */
    private void updateCategory(String id, String name) {
        progressBar.setVisibility(View.VISIBLE);
        com.example.myapplication.model.category.CategoryRequestDTO request = new com.example.myapplication.model.category.CategoryRequestDTO();
        request.setName(name);

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

    /**
     * deleteCategory() - Xóa category: hiện dialog xác nhận → gọi API DELETE → reload list
     */
    private void deleteCategory(CategoryResponseDTO category) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Category")
                .setMessage("Are you sure you want to delete " + category.getName() + "?")
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

    /**
     * showCreateDialog() - Hiện dialog để tạo category mới
     */
    private void showCreateDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_category_form, null);
        TextInputEditText etName = dialogView.findViewById(R.id.etCategoryName);

        new AlertDialog.Builder(this)
                .setTitle("Create Category")
                .setView(dialogView)
                .setPositiveButton("Create", (dialog, which) -> {
                    String name = etName.getText() != null ? etName.getText().toString().trim() : "";
                    if (!name.isEmpty()) {
                        createCategory(name);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * createCategory() - Tạo category mới: gọi API POST → reload list
     */
    private void createCategory(String name) {
        progressBar.setVisibility(View.VISIBLE);
        com.example.myapplication.model.category.CategoryRequestDTO request = new com.example.myapplication.model.category.CategoryRequestDTO();
        request.setName(name);

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
}

