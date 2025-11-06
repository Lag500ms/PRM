package com.example.myapplication.ui.admin.vehicles;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.model.vehicle.response.VehicleResponseDTO;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * VehiclesAdapter - Adapter cho RecyclerView hiển thị danh sách vehicles
 * 
 * Chức năng:
 * - Hiển thị danh sách vehicles trong RecyclerView
 * - Click vào item → gọi listener.onVehicleClick() → mở form edit
 * - Long click vào item → gọi deleteListener.onVehicleDelete() → xóa vehicle
 * - Hiển thị tên category từ categoryCache (thay vì categoryId)
 */
public class VehiclesAdapter extends RecyclerView.Adapter<VehiclesAdapter.ViewHolder> {

    private final Context context;
    private final List<VehicleResponseDTO> vehicles;
    private final OnVehicleClickListener listener;
    private final OnVehicleDeleteListener deleteListener;
    
    // Cache for category names
    public static final java.util.Map<String, String> categoryCache = new java.util.HashMap<>();

    // Interface callback khi click vào item
    public interface OnVehicleClickListener {
        void onVehicleClick(VehicleResponseDTO vehicle);
    }

    // Interface callback khi long click vào item
    public interface OnVehicleDeleteListener {
        void onVehicleDelete(VehicleResponseDTO vehicle);
    }

    /**
     * Constructor - Khởi tạo Adapter với list vehicles và 2 callback
     */
    public VehiclesAdapter(Context context, List<VehicleResponseDTO> vehicles, OnVehicleClickListener listener, OnVehicleDeleteListener deleteListener) {
        this.context = context;
        this.vehicles = vehicles;
        this.listener = listener;
        this.deleteListener = deleteListener;
    }

    /**
     * onCreateViewHolder() - Tạo ViewHolder từ layout item_vehicle.xml
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vehicle, parent, false);
        return new ViewHolder(view);
    }

    /**
     * onBindViewHolder() - Gắn dữ liệu vehicle vào ViewHolder
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VehicleResponseDTO vehicle = vehicles.get(position);
        holder.bind(vehicle, listener, deleteListener);
    }

    /**
     * getItemCount() - Trả về số lượng items trong list
     */
    @Override
    public int getItemCount() {
        return vehicles != null ? vehicles.size() : 0;
    }

    /**
     * ViewHolder - Giữ các TextView để hiển thị thông tin vehicle
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvModel, tvVersion, tvColor, tvCategory, tvPrice, tvQuantity;

        /**
         * ViewHolder() - Khởi tạo và tìm các view từ layout
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvModel = itemView.findViewById(R.id.tvModel);
            tvVersion = itemView.findViewById(R.id.tvVersion);
            tvColor = itemView.findViewById(R.id.tvColor);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
        }

        /**
         * bind() - Gắn dữ liệu vehicle vào các TextView và setup click listeners
         */
        public void bind(VehicleResponseDTO vehicle, OnVehicleClickListener listener, OnVehicleDeleteListener deleteListener) {
            // Hiển thị thông tin vehicle
            tvModel.setText(vehicle.getModel() != null ? vehicle.getModel() : "N/A");
            tvVersion.setText(vehicle.getVersion() != null ? vehicle.getVersion() : "N/A");
            tvColor.setText(vehicle.getColor() != null ? vehicle.getColor() : "N/A");
            
            // Lấy tên category từ cache (thay vì hiển thị categoryId)
            String categoryName = categoryCache.get(vehicle.getCategoryId());
            tvCategory.setText(categoryName != null ? categoryName : "Unknown");
            
            // Format price thành currency
            NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
            tvPrice.setText(formatter.format(vehicle.getPrice()));
            
            tvQuantity.setText(String.valueOf(vehicle.getQuantity()));

            // Click vào item → mở form edit
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onVehicleClick(vehicle);
                }
            });

            // Long click vào item → xóa vehicle
            itemView.setOnLongClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onVehicleDelete(vehicle);
                }
                return true;
            });
        }
    }
}

