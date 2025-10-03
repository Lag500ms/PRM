package prm.be.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import prm.be.entity.Schedule;
import prm.be.enums.ScheduleStatus;

import java.time.LocalDateTime;

public interface ScheduleRepository extends JpaRepository<Schedule, String> {
    Page<Schedule> findByAccount_Id(String accountId, Pageable pageable);

    Page<Schedule> findByAccount_IdAndStatus(String accountId, ScheduleStatus status, Pageable pageable);

    Page<Schedule> findByAccount_IdAndDateTimeBetween(String accountId, LocalDateTime start, LocalDateTime end,
            Pageable pageable);
}
