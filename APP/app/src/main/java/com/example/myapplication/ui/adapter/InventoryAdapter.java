package com.example.myapplication.ui.adapter;

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
    private final OnItemClickListener clickListener;
    private final OnItemLongClickListener longClickListener;

    public interface OnItemClickListener {
        void onItemClick(VehicleItem item);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(VehicleItem item);
    }

    public InventoryAdapter(List<VehicleItem> vehicles, OnItemClickListener clickListener, OnItemLongClickListener longClickListener) {
        this.vehicles = vehicles;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
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
        holder.bind(vehicle, clickListener, longClickListener);
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

        public void bind(final VehicleItem vehicle, final OnItemClickListener clickListener, final OnItemLongClickListener longClickListener) {
            tvModel.setText(vehicle.model != null ? vehicle.model : "N/A");
            tvVersion.setText(vehicle.version != null ? vehicle.version : "N/A");
            tvColor.setText(vehicle.color != null ? vehicle.color : "N/A");
            tvCategory.setText(vehicle.categoryName != null ? vehicle.categoryName : "N/A");
            tvQuantity.setText(vehicle.quantity != null ? String.valueOf(vehicle.quantity) : "0");

            itemView.setOnClickListener(v -> clickListener.onItemClick(vehicle));
            itemView.setOnLongClickListener(v -> {
                longClickListener.onItemLongClick(vehicle);
                return true;
            });
        }
    }
}
