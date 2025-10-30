package com.example.myapplication.ui.inventory.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.model.inventory.VehicleItem;
import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ViewHolder> {

    private final List<VehicleItem> vehicles;

    public InventoryAdapter(List<VehicleItem> vehicles) {
        this.vehicles = vehicles;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inventory, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VehicleItem vehicle = vehicles.get(position);
        holder.bind(vehicle);
    }

    @Override
    public int getItemCount() {
        return vehicles != null ? vehicles.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvModel, tvVersion, tvColor, tvCategory, tvQuantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvModel = itemView.findViewById(R.id.tvModel);
            tvVersion = itemView.findViewById(R.id.tvVersion);
            tvColor = itemView.findViewById(R.id.tvColor);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
        }

        public void bind(VehicleItem vehicle) {
            tvModel.setText(vehicle.model != null ? vehicle.model : "N/A");
            tvVersion.setText(vehicle.version != null ? vehicle.version : "N/A");
            tvColor.setText(vehicle.color != null ? vehicle.color : "N/A");
            tvCategory.setText(vehicle.categoryName != null ? vehicle.categoryName : "N/A");
            tvQuantity.setText(vehicle.quantity != null ? String.valueOf(vehicle.quantity) : "0");
        }
    }
}


