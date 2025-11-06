package com.example.myapplication.ui.admin.categories;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.model.category.CategoryResponseDTO;
import com.google.android.material.button.MaterialButton;
import java.util.List;

/**
 * CategoriesAdapter - Adapter cho RecyclerView hiển thị danh sách categories
 * 
 * Chức năng:
 * - Hiển thị danh sách categories
 * - Click nút Edit → gọi onEditListener → mở dialog sửa
 * - Click nút Delete → gọi onDeleteListener → xóa category
 */
public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.VH> {

    private final List<CategoryResponseDTO> items;
    private final OnEditListener onEditListener;
    private final OnDeleteListener onDeleteListener;

    public interface OnEditListener {
        void onEdit(CategoryResponseDTO category);
    }

    public interface OnDeleteListener {
        void onDelete(CategoryResponseDTO category);
    }

    public CategoriesAdapter(List<CategoryResponseDTO> items, OnEditListener onEditListener, OnDeleteListener onDeleteListener) {
        this.items = items;
        this.onEditListener = onEditListener;
        this.onDeleteListener = onDeleteListener;
    }

    /**
     * onCreateViewHolder() - Tạo ViewHolder từ layout item_category.xml
     */
    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new VH(view);
    }

    /**
     * onBindViewHolder() - Gắn dữ liệu category vào ViewHolder và setup click listeners
     */
    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        CategoryResponseDTO category = items.get(position);
        holder.tvCategoryName.setText(category.getName());

        holder.btnEdit.setOnClickListener(v -> {
            if (onEditListener != null) {
                onEditListener.onEdit(category);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (onDeleteListener != null) {
                onDeleteListener.onDelete(category);
            }
        });
    }

    /**
     * getItemCount() - Trả về số lượng items trong list
     */
    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    /**
     * VH - ViewHolder giữ các view để hiển thị category
     */
    static class VH extends RecyclerView.ViewHolder {
        TextView tvCategoryName;
        MaterialButton btnEdit, btnDelete;

        VH(View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}

