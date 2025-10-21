package prm.be.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import prm.be.dto.request.category.CategoryRequestDTO;
import prm.be.entity.Category;
import prm.be.exception.NotFoundException;
import prm.be.repository.CategoryRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public Category create(CategoryRequestDTO categoryRequestDTO) {
        if (categoryRequestDTO.getName() == null || categoryRequestDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be empty");
        }

        String name = categoryRequestDTO.getName().trim();

        boolean exists = categoryRepository.findAll().stream()
                .anyMatch(c -> c.getName().equalsIgnoreCase(name));

        if (exists) {
            throw new IllegalArgumentException("Category name already exists: " + name);
        }

        Category category = Category.builder()
                .name(name)
                .build();

        return categoryRepository.save(category);
    }

    @Transactional
    public void delete(String id) {
        if (!categoryRepository.existsById(id)) {
            throw new NotFoundException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }

    @Transactional
    public Category update(String id, CategoryRequestDTO categoryRequestDTO) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found with id: " + id));

        if (categoryRequestDTO.getName() != null && !categoryRequestDTO.getName().trim().isEmpty()) {
            String newName = categoryRequestDTO.getName().trim();

            boolean exists = categoryRepository.findAll().stream()
                    .anyMatch(c -> c.getName().equalsIgnoreCase(newName) && !c.getId().equals(id));

            if (exists) {
                throw new IllegalArgumentException("Category name already exists: " + newName);
            }

            category.setName(newName);
        }

        return categoryRepository.save(category);
    }

    public Category getCategoryById(String id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found with id: " + id));
    }

    public List<Category> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()) {
            throw new NotFoundException("No categories found");
        }
        return categories;
    }
}

