package com.example.myapplication.model.dashboard;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;

public class Summary {
    @SerializedName("totalOrders")
    public long totalOrders;
    @SerializedName("totalRevenue")
    public BigDecimal totalRevenue;
    @SerializedName("totalVehiclesInInventory")
    public long totalVehiclesInInventory;
    @SerializedName("pendingSchedules")
    public long pendingSchedules;
    @SerializedName("completedOrders")
    public long completedOrders;
}
