package prm.be.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import prm.be.dto.response.dashboard.DashboardResponses.*;
import prm.be.entity.Order;
import prm.be.enums.OrderStatus;
import prm.be.enums.ScheduleStatus;
import prm.be.repository.OrderRepository;
import prm.be.repository.ScheduleRepository;
import prm.be.repository.VehicleInventoryRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

        private final OrderRepository orderRepository;
        private final ScheduleRepository scheduleRepository;
        private final VehicleInventoryRepository vehicleInventoryRepository;

        public DashboardResponse getDashboard(String accountId) {
                // build dashboard aggregates and lists for the current dealer
                DashboardResponse resp = new DashboardResponse();
                resp.setSummary(buildSummary(accountId));
                resp.setRecentOrders(buildRecentOrders(accountId));
                resp.setUpcomingSchedules(buildUpcomingSchedules(accountId));
                resp.setInventoryByCategory(buildInventoryByCategory(accountId));
                resp.setMonthlyRevenue(buildMonthlyRevenue(accountId));
                resp.setLowStock(buildLowStock(accountId));

                // Thêm thống kê trực quan
                resp.setTopSellingVehicles(buildTopSellingVehicles(accountId));
                resp.setOrderStatusStats(buildOrderStatusStats(accountId));
                resp.setScheduleStatusStats(buildScheduleStatusStats(accountId));
                return resp;
        }

        private Summary buildSummary(String accountId) {
                // compute total orders, revenue (completed), inventory count, pending
                // schedules, completed orders
                Summary s = new Summary();
                long totalOrders = orderRepository
                                .findByAccount_Id(accountId,
                                                org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE))
                                .getTotalElements();
                BigDecimal revenue = orderRepository
                                .findByAccount_IdAndStatus(accountId, OrderStatus.COMPLETED,
                                                org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE))
                                .stream().map(Order::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
                long vehicles = vehicleInventoryRepository.findAll().stream()
                                .filter(vi -> vi.getInventory().getAccount().getId().equals(accountId))
                                .mapToLong(vi -> vi.getQuantity() != null ? vi.getQuantity() : 0).sum();
                long pendingSchedules = scheduleRepository.findByAccount_IdAndStatus(accountId, ScheduleStatus.PENDING,
                                org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE))
                                .getTotalElements();
                long completedOrders = orderRepository.findByAccount_IdAndStatus(accountId, OrderStatus.COMPLETED,
                                org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE))
                                .getTotalElements();

                // Thêm thống kê chi tiết
                long pendingOrders = orderRepository.findByAccount_IdAndStatus(accountId, OrderStatus.PENDING,
                                org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE))
                                .getTotalElements();
                long confirmedOrders = orderRepository.findByAccount_IdAndStatus(accountId, OrderStatus.CONFIRMED,
                                org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE))
                                .getTotalElements();
                long cancelledOrders = orderRepository.findByAccount_IdAndStatus(accountId, OrderStatus.CANCELLED,
                                org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE))
                                .getTotalElements();

                long totalSchedules = scheduleRepository.findByAccount_Id(accountId,
                                org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE))
                                .getTotalElements();
                long confirmedSchedules = scheduleRepository
                                .findByAccount_IdAndStatus(accountId, ScheduleStatus.CONFIRMED,
                                                org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE))
                                .getTotalElements();
                long completedSchedules = scheduleRepository
                                .findByAccount_IdAndStatus(accountId, ScheduleStatus.COMPLETED,
                                                org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE))
                                .getTotalElements();
                long cancelledSchedules = scheduleRepository
                                .findByAccount_IdAndStatus(accountId, ScheduleStatus.CANCELLED,
                                                org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE))
                                .getTotalElements();

                s.setTotalOrders(totalOrders);
                s.setTotalRevenue(revenue);
                s.setTotalVehiclesInInventory(vehicles);
                s.setPendingSchedules(pendingSchedules);
                s.setCompletedOrders(completedOrders);

                // Set thống kê chi tiết
                s.setPendingOrders(pendingOrders);
                s.setConfirmedOrders(confirmedOrders);
                s.setCancelledOrders(cancelledOrders);
                s.setTotalSchedules(totalSchedules);
                s.setConfirmedSchedules(confirmedSchedules);
                s.setCompletedSchedules(completedSchedules);
                s.setCancelledSchedules(cancelledSchedules);
                return s;
        }

        private List<RecentOrderItem> buildRecentOrders(String accountId) {
                // latest 10 orders for the dealer
                return orderRepository
                                .findByAccount_Id(accountId, org.springframework.data.domain.PageRequest.of(0, 50))
                                .getContent().stream()
                                .sorted(Comparator.comparing(Order::getCreatedAt).reversed())
                                .limit(10)
                                .map(o -> {
                                        RecentOrderItem item = new RecentOrderItem();
                                        item.setId(o.getId());
                                        item.setCustomer(o.getCustomerInfo() != null ? o.getCustomerInfo().getCustomer()
                                                        : null);
                                        item.setTotalPrice(o.getTotalPrice());
                                        item.setStatus(o.getStatus() != null ? o.getStatus().name() : null);
                                        item.setCreatedAt(o.getCreatedAt());
                                        return item;
                                })
                                .collect(Collectors.toList());
        }

        private List<UpcomingScheduleItem> buildUpcomingSchedules(String accountId) {
                // next 7 days schedules for the dealer
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime next7 = now.plusDays(7);
                return scheduleRepository
                                .findByAccount_IdAndDateTimeBetween(accountId, now, next7,
                                                org.springframework.data.domain.PageRequest.of(0, 50))
                                .getContent().stream()
                                .sorted(Comparator.comparing(prm.be.entity.Schedule::getDateTime))
                                .map(s -> {
                                        UpcomingScheduleItem item = new UpcomingScheduleItem();
                                        item.setId(s.getId());
                                        item.setCustomer(s.getCustomerInfo() != null ? s.getCustomerInfo().getCustomer()
                                                        : null);
                                        item.setDateTime(s.getDateTime());
                                        item.setStatus(s.getStatus() != null ? s.getStatus().name() : null);
                                        return item;
                                })
                                .collect(Collectors.toList());
        }

        private List<InventoryByCategoryItem> buildInventoryByCategory(String accountId) {
                // sum inventory quantities grouped by vehicle category
                Map<String, Integer> byCat = vehicleInventoryRepository.findAll().stream()
                                .filter(vi -> vi.getInventory().getAccount().getId().equals(accountId))
                                .collect(Collectors.groupingBy(vi -> vi.getVehicle().getCategory().getName(),
                                                Collectors.summingInt(vi -> vi.getQuantity() != null ? vi.getQuantity()
                                                                : 0)));
                return byCat.entrySet().stream().map(e -> {
                        InventoryByCategoryItem item = new InventoryByCategoryItem();
                        item.setCategoryName(e.getKey());
                        item.setQuantity(e.getValue());
                        return item;
                }).collect(Collectors.toList());
        }

        private List<MonthlyRevenueItem> buildMonthlyRevenue(String accountId) {
                // revenue grouped by month for completed orders
                return orderRepository
                                .findByAccount_IdAndStatus(accountId, OrderStatus.COMPLETED,
                                                org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE))
                                .stream()
                                .collect(Collectors.groupingBy(o -> YearMonth.from(o.getCreatedAt())))
                                .entrySet().stream()
                                .sorted(Map.Entry.comparingByKey())
                                .map(e -> {
                                        MonthlyRevenueItem item = new MonthlyRevenueItem();
                                        YearMonth ym = e.getKey();
                                        item.setMonth(LocalDate.of(ym.getYear(), ym.getMonth(), 1));
                                        item.setRevenue(
                                                        e.getValue().stream().map(Order::getTotalPrice)
                                                                        .reduce(BigDecimal.ZERO, BigDecimal::add));
                                        return item;
                                })
                                .collect(Collectors.toList());
        }

        private List<LowStockItem> buildLowStock(String accountId) {
                // vehicles with quantity < 5 in dealer inventory
                return vehicleInventoryRepository.findAll().stream()
                                .filter(vi -> vi.getInventory().getAccount().getId().equals(accountId))
                                .filter(vi -> vi.getQuantity() != null && vi.getQuantity() < 5)
                                .map(vi -> {
                                        LowStockItem item = new LowStockItem();
                                        item.setVehicleId(vi.getVehicle().getId());
                                        item.setModel(vi.getVehicle().getModel());
                                        item.setVersion(vi.getVehicle().getVersion());
                                        item.setQuantity(vi.getQuantity());
                                        return item;
                                })
                                .collect(Collectors.toList());
        }

        private List<TopSellingVehicle> buildTopSellingVehicles(String accountId) {
                // Top 5 vehicles bán chạy nhất (dựa trên completed orders)
                // Note: Cần implement logic tính toán dựa trên order items
                // Tạm thời return empty list
                return List.of();
        }

        private OrderStatusStats buildOrderStatusStats(String accountId) {
                OrderStatusStats stats = new OrderStatusStats();
                stats.setPending(orderRepository.findByAccount_IdAndStatus(accountId, OrderStatus.PENDING,
                                org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE))
                                .getTotalElements());
                stats.setConfirmed(orderRepository.findByAccount_IdAndStatus(accountId, OrderStatus.CONFIRMED,
                                org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE))
                                .getTotalElements());
                stats.setCompleted(orderRepository.findByAccount_IdAndStatus(accountId, OrderStatus.COMPLETED,
                                org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE))
                                .getTotalElements());
                stats.setCancelled(orderRepository.findByAccount_IdAndStatus(accountId, OrderStatus.CANCELLED,
                                org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE))
                                .getTotalElements());
                return stats;
        }

        private ScheduleStatusStats buildScheduleStatusStats(String accountId) {
                ScheduleStatusStats stats = new ScheduleStatusStats();
                stats.setPending(scheduleRepository.findByAccount_IdAndStatus(accountId, ScheduleStatus.PENDING,
                                org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE))
                                .getTotalElements());
                stats.setConfirmed(scheduleRepository.findByAccount_IdAndStatus(accountId, ScheduleStatus.CONFIRMED,
                                org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE))
                                .getTotalElements());
                stats.setCompleted(scheduleRepository.findByAccount_IdAndStatus(accountId, ScheduleStatus.COMPLETED,
                                org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE))
                                .getTotalElements());
                stats.setCancelled(scheduleRepository.findByAccount_IdAndStatus(accountId, ScheduleStatus.CANCELLED,
                                org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE))
                                .getTotalElements());
                return stats;
        }
}
