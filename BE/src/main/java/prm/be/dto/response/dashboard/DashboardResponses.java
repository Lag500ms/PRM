package prm.be.dto.response.dashboard;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class DashboardResponses {

    @Data
    public static class Summary {
        private long totalOrders;
        private BigDecimal totalRevenue;
        private long totalVehiclesInInventory;
        private long pendingSchedules;
        private long completedOrders;

        // Thêm thống kê trực quan
        private long pendingOrders;
        private long confirmedOrders;
        private long cancelledOrders;
        private long totalSchedules;
        private long confirmedSchedules;
        private long completedSchedules;
        private long cancelledSchedules;
    }

    @Data
    public static class RecentOrderItem {
        private String id;
        private String customer;
        private BigDecimal totalPrice;
        private String status;
        private LocalDateTime createdAt;
    }

    @Data
    public static class UpcomingScheduleItem {
        private String id;
        private String customer;
        private LocalDateTime dateTime;
        private String status;
    }

    @Data
    public static class InventoryByCategoryItem {
        private String categoryName;
        private long quantity;
    }

    @Data
    public static class MonthlyRevenueItem {
        private LocalDate month;
        private BigDecimal revenue;
    }

    @Data
    public static class LowStockItem {
        private String vehicleId;
        private String model;
        private String version;
        private int quantity;
    }

    @Data
    public static class TopSellingVehicle {
        private String vehicleId;
        private String model;
        private String version;
        private String categoryName;
        private long totalSold;
        private BigDecimal totalRevenue;
    }

    @Data
    public static class OrderStatusStats {
        private long pending;
        private long confirmed;
        private long completed;
        private long cancelled;
    }

    @Data
    public static class ScheduleStatusStats {
        private long pending;
        private long confirmed;
        private long completed;
        private long cancelled;
    }

    @Data
    public static class DashboardResponse {
        private Summary summary;
        private List<RecentOrderItem> recentOrders;
        private List<UpcomingScheduleItem> upcomingSchedules;
        private List<InventoryByCategoryItem> inventoryByCategory;
        private List<MonthlyRevenueItem> monthlyRevenue;
        private List<LowStockItem> lowStock;

        // Thêm thống kê trực quan
        private List<TopSellingVehicle> topSellingVehicles;
        private OrderStatusStats orderStatusStats;
        private ScheduleStatusStats scheduleStatusStats;
    }
}
