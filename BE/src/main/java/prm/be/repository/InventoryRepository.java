package prm.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import prm.be.entity.Inventory;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, String> {
    List<Inventory> findByAccount_Id(String accountId);

    Optional<Inventory> findByIdAndAccount_Id(String id, String accountId);
}
