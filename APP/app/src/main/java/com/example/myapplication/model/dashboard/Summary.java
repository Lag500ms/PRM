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

    // Thêm thống kê chi tiết mới
    @SerializedName("pendingOrders")
    public long pendingOrders;
    @SerializedName("confirmedOrders")
    public long confirmedOrders;
    @SerializedName("cancelledOrders")
    public long cancelledOrders;
    @SerializedName("totalSchedules")
    public long totalSchedules;
    @SerializedName("confirmedSchedules")
    public long confirmedSchedules;
    @SerializedName("completedSchedules")
    public long completedSchedules;
    @SerializedName("cancelledSchedules")
    public long cancelledSchedules;
}
