package prm.be.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import prm.be.entity.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle, String> {
    Page<Vehicle> findByAccount_Id(String accountId, Pageable pageable);
}
