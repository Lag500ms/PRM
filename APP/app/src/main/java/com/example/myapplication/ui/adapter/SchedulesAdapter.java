package com.example.myapplication.ui.schedules.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.model.schedules.ScheduleResponse;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class SchedulesAdapter extends RecyclerView.Adapter<SchedulesAdapter.ViewHolder> {

    private final List<ScheduleResponse> schedules;
    private final OnScheduleClickListener listener;
    private final Context context;

    public SchedulesAdapter(Context context, List<ScheduleResponse> schedules, OnScheduleClickListener listener) {
        this.context = context;
        this.schedules = schedules;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ScheduleResponse schedule = schedules.get(position);
        holder.bind(schedule);
    }

    @Override
    public int getItemCount() {
        return schedules != null ? schedules.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCustomer, tvStatus, tvPhone, tvDateTime, tvAddress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCustomer = itemView.findViewById(R.id.tvCustomer);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvAddress = itemView.findViewById(R.id.tvAddress);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        listener.onScheduleClick(schedules.get(pos));
                    }
                }
            });
        }

        public void bind(ScheduleResponse schedule) {
            tvCustomer.setText(schedule.customer != null ? schedule.customer : "N/A");
            tvPhone.setText(schedule.phone != null ? schedule.phone : "N/A");
            tvAddress.setText(schedule.address != null ? schedule.address : "N/A");

            // Status badge color
            if (schedule.status != null) {
                tvStatus.setText(schedule.status.toUpperCase());
                int color = getStatusColor(schedule.status);
                tvStatus.setBackgroundColor(context.getResources().getColor(color, null));
            }

            // DateTime
            if (schedule.dateTime != null) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                    SimpleDateFormat displayFormat = new SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault());
                    tvDateTime.setText(displayFormat.format(sdf.parse(schedule.dateTime)));
                } catch (Exception e) {
                    tvDateTime.setText(schedule.dateTime);
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

    public interface OnScheduleClickListener {
        void onScheduleClick(ScheduleResponse schedule);
    }
}


