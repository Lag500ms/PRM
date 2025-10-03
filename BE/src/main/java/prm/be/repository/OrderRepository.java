package prm.be.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import prm.be.entity.Order;
import prm.be.enums.OrderStatus;

import java.time.LocalDateTime;

public interface OrderRepository extends JpaRepository<Order, String> {
    Page<Order> findByAccount_Id(String accountId, Pageable pageable);

    Page<Order> findByAccount_IdAndStatus(String accountId, OrderStatus status, Pageable pageable);

    Page<Order> findByAccount_IdAndCreatedAtBetween(String accountId, LocalDateTime start, LocalDateTime end,
            Pageable pageable);
}
