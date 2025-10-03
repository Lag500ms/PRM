package com.example.myapplication.model.dashboard;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class DashboardResponse {
    @SerializedName("summary")
    public Summary summary;
    @SerializedName("recentOrders")
    public java.util.List<RecentOrderItem> recentOrders;
    @SerializedName("upcomingSchedules")
    public java.util.List<UpcomingScheduleItem> upcomingSchedules;
    @SerializedName("inventoryByCategory")
    public java.util.List<InventoryByCategoryItem> inventoryByCategory;
    @SerializedName("monthlyRevenue")
    public java.util.List<MonthlyRevenueItem> monthlyRevenue;
    @SerializedName("lowStock")
    public java.util.List<LowStockItem> lowStock;
}
