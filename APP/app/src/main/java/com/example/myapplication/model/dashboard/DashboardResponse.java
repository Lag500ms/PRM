package com.example.myapplication.model.dashboard;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;
import java.util.List;

public class DashboardResponse {
    @SerializedName("summary")
    public Summary summary;

    @SerializedName("recentOrders")
    public List<RecentOrderItem> recentOrders;

    @SerializedName("upcomingSchedules")
    public List<UpcomingScheduleItem> upcomingSchedules;

    @SerializedName("inventoryByCategory")
    public List<InventoryByCategoryItem> inventoryByCategory;

    @SerializedName("lowStock")
    public List<LowStockItem> lowStock;

    public static class Summary {
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

        @SerializedName("pendingOrders")
        public long pendingOrders;

        @SerializedName("confirmedOrders")
        public long confirmedOrders;

        @SerializedName("cancelledOrders")
        public long cancelledOrders;
    }

    public static class RecentOrderItem {
        @SerializedName("id")
        public String id;

        @SerializedName("customer")
        public String customer;

        @SerializedName("totalPrice")
        public BigDecimal totalPrice;

        @SerializedName("status")
        public String status;

        @SerializedName("createdAt")
        public String createdAt;
    }

    public static class UpcomingScheduleItem {
        @SerializedName("id")
        public String id;

        @SerializedName("customer")
        public String customer;

        @SerializedName("dateTime")
        public String dateTime;

        @SerializedName("status")
        public String status;
    }

    public static class InventoryByCategoryItem {
        @SerializedName("categoryName")
        public String categoryName;

        @SerializedName("quantity")
        public long quantity;
    }

    public static class LowStockItem {
        @SerializedName("vehicleId")
        public String vehicleId;

        @SerializedName("model")
        public String model;

        @SerializedName("version")
        public String version;

        @SerializedName("quantity")
        public int quantity;
    }
}
