package prm.be.controller;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import prm.be.dto.request.category.CategoryRequestDTO;
import prm.be.dto.response.category.CategoryResponseDTO;
import prm.be.entity.Category;
import prm.be.service.CategoryService;
import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@AllArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final ModelMapper modelMapper;

    /**
     * Lấy category theo ID - Tất cả user đều có thể xem
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CategoryResponseDTO> getCategoryById(@PathVariable("id") String id) {
        Category category = categoryService.getCategoryById(id);
        CategoryResponseDTO categoryResponseDTO = modelMapper.map(category, CategoryResponseDTO.class);
        return ResponseEntity.ok(categoryResponseDTO);
    }

    /**
     * Xóa category - Chỉ ADMIN
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponseDTO> delete(@PathVariable("id") String id) {
        Category category = categoryService.getCategoryById(id);
        CategoryResponseDTO categoryResponseDTO = modelMapper.map(category, CategoryResponseDTO.class);
        categoryService.delete(id);
        return ResponseEntity.ok(categoryResponseDTO);
    }

    /**
     * Cập nhật category - Chỉ ADMIN
     */
    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponseDTO> updateCategory(
            @PathVariable("id") String id,
            @RequestBody CategoryRequestDTO categoryRequestDTO) {

        Category updatedCategory = categoryService.update(id, categoryRequestDTO);
        CategoryResponseDTO responseDTO = modelMapper.map(updatedCategory, CategoryResponseDTO.class);

        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Lấy tất cả categories - Tất cả user đều có thể xem
     */
    @GetMapping("/getAll")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        List<CategoryResponseDTO> categoryResponseDTOS = categories.stream()
                .map(category -> modelMapper.map(category, CategoryResponseDTO.class))
                .toList();
        return ResponseEntity.ok(categoryResponseDTOS);
    }

    /**
     * Tạo category mới - Chỉ ADMIN
     */
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponseDTO> createCategory(@RequestBody CategoryRequestDTO categoryRequestDTO) {
        Category createdCategory = categoryService.create(categoryRequestDTO);
        CategoryResponseDTO responseDTO = modelMapper.map(createdCategory, CategoryResponseDTO.class);
        return ResponseEntity.ok(responseDTO);
    }
}
