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

public class VehiclesAdapter extends RecyclerView.Adapter<VehiclesAdapter.ViewHolder> {

    private final Context context;
    private final List<VehicleResponseDTO> vehicles;
    private final OnVehicleClickListener listener;
    private final OnVehicleDeleteListener deleteListener;
    
    // Cache for category names
    public static final java.util.Map<String, String> categoryCache = new java.util.HashMap<>();

    public interface OnVehicleClickListener {
        void onVehicleClick(VehicleResponseDTO vehicle);
    }

    public interface OnVehicleDeleteListener {
        void onVehicleDelete(VehicleResponseDTO vehicle);
    }

    public VehiclesAdapter(Context context, List<VehicleResponseDTO> vehicles, OnVehicleClickListener listener, OnVehicleDeleteListener deleteListener) {
        this.context = context;
        this.vehicles = vehicles;
        this.listener = listener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vehicle, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VehicleResponseDTO vehicle = vehicles.get(position);
        holder.bind(vehicle, listener, deleteListener);
    }

    @Override
    public int getItemCount() {
        return vehicles != null ? vehicles.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvModel, tvVersion, tvColor, tvCategory, tvPrice, tvQuantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvModel = itemView.findViewById(R.id.tvModel);
            tvVersion = itemView.findViewById(R.id.tvVersion);
            tvColor = itemView.findViewById(R.id.tvColor);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
        }

        public void bind(VehicleResponseDTO vehicle, OnVehicleClickListener listener, OnVehicleDeleteListener deleteListener) {
            tvModel.setText(vehicle.getModel() != null ? vehicle.getModel() : "N/A");
            tvVersion.setText(vehicle.getVersion() != null ? vehicle.getVersion() : "N/A");
            tvColor.setText(vehicle.getColor() != null ? vehicle.getColor() : "N/A");
            
            // Get category name from cache
            String categoryName = categoryCache.get(vehicle.getCategoryId());
            tvCategory.setText(categoryName != null ? categoryName : "Unknown");
            
            NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
            tvPrice.setText(formatter.format(vehicle.getPrice()));
            
            tvQuantity.setText(String.valueOf(vehicle.getQuantity()));

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onVehicleClick(vehicle);
                }
            });

            itemView.setOnLongClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onVehicleDelete(vehicle);
                }
                return true;
            });
        }
    }
}

