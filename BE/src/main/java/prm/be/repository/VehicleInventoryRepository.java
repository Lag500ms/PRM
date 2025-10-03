package prm.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import prm.be.entity.VehicleInventory;

import java.util.List;

public interface VehicleInventoryRepository extends JpaRepository<VehicleInventory, String> {
    List<VehicleInventory> findByInventory_Id(String inventoryId);
}
