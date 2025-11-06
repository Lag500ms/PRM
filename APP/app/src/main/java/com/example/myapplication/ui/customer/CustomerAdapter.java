package com.example.myapplication.ui.customer;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import java.util.ArrayList;
import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {

    private List<CustomerManagementActivity.CustomerInfo> customers;
    private List<CustomerManagementActivity.CustomerInfo> allCustomers;

    public CustomerAdapter(List<CustomerManagementActivity.CustomerInfo> customers) {
        this.customers = customers;
        this.allCustomers = new ArrayList<>(customers);
    }

    public void updateList(List<CustomerManagementActivity.CustomerInfo> filteredList) {
        this.customers = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_customer, parent, false);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        CustomerManagementActivity.CustomerInfo customer = customers.get(position);

        // Display name
        holder.tvCustomerName.setText(customer.name != null && !customer.name.isEmpty()
            ? customer.name
            : "N/A");

        // Display phone
        holder.tvPhone.setText(customer.phone != null && !customer.phone.isEmpty()
            ? customer.phone
            : "No phone");

        // Display address
        holder.tvAddress.setText(customer.address != null && !customer.address.isEmpty()
            ? customer.address
            : "No address");

        // Handle item click
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), CustomerDetailActivity.class);
            intent.putExtra("CUSTOMER_ID", customer.id);  // Pass customer ID
            intent.putExtra("CUSTOMER_NAME", customer.name);
            intent.putExtra("CUSTOMER_PHONE", customer.phone);
            intent.putExtra("CUSTOMER_ADDRESS", customer.address);
            intent.putExtra("CUSTOMER_EMAIL", customer.email);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return customers.size();
    }

    static class CustomerViewHolder extends RecyclerView.ViewHolder {
        TextView tvCustomerName;
        TextView tvPhone;
        TextView tvAddress;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvAddress = itemView.findViewById(R.id.tvAddress);
        }
    }
}
