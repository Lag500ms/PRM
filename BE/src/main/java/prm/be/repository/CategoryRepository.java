package prm.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import prm.be.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, String> {
}
