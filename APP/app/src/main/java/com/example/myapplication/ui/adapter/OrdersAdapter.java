package com.example.myapplication.ui.orders.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.model.orders.OrderResponse;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {

    private final List<OrderResponse> orders;
    private final OnOrderClickListener listener;
    private final Context context;

    public OrdersAdapter(Context context, List<OrderResponse> orders, OnOrderClickListener listener) {
        this.context = context;
        this.orders = orders;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderResponse order = orders.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orders != null ? orders.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCustomer, tvStatus, tvPhone, tvAddress, tvPrice, tvDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCustomer = itemView.findViewById(R.id.tvCustomer);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvDate = itemView.findViewById(R.id.tvDate);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        listener.onOrderClick(orders.get(pos));
                    }
                }
            });
        }

        public void bind(OrderResponse order) {
            tvCustomer.setText(order.customer != null ? order.customer : "N/A");
            tvPhone.setText(order.phone != null ? order.phone : "N/A");
            tvAddress.setText(order.address != null ? order.address : "N/A");

            if (order.totalPrice != null) {
                DecimalFormat df = new DecimalFormat("#,###");
                tvPrice.setText("VND " + df.format(order.totalPrice.doubleValue()));
            }

            // Status badge color
            if (order.status != null) {
                tvStatus.setText(order.status.toUpperCase());
                int color = getStatusColor(order.status);
                tvStatus.setBackgroundColor(context.getResources().getColor(color, null));
            }

            // Date
            if (order.createdAt != null) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                    SimpleDateFormat displayFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                    tvDate.setText("Created: " + displayFormat.format(sdf.parse(order.createdAt)));
                } catch (Exception e) {
                    tvDate.setText("Created: " + order.createdAt);
                }
            }
        }

        private int getStatusColor(String status) {
            if (status == null) return R.color.status_pending;
            switch (status.toUpperCase()) {
                case "PENDING":
                    return R.color.status_pending;
                case "CONFIRMED":
                    return R.color.status_approved;
                case "CANCELLED":
                    return R.color.status_cancelled;
                case "COMPLETED":
                    return R.color.status_completed;
                default:
                    return R.color.status_pending;
            }
        }
    }

    public interface OnOrderClickListener {
        void onOrderClick(OrderResponse order);
    }
}

