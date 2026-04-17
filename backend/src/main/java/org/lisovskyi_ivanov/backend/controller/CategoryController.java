package org.lisovskyi_ivanov.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.lisovskyi_ivanov.backend.dto.request.CategoryRequest;
import org.lisovskyi_ivanov.backend.dto.response.CategoryDto;
import org.lisovskyi_ivanov.backend.entity.Category;
import org.lisovskyi_ivanov.backend.mapping.dto.CategoryMapper;
import org.lisovskyi_ivanov.backend.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    // -- GET
    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        List<CategoryDto> categories = categoryService.findAll().stream()
                .map(categoryMapper::toDto)
                .toList();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{categoryNumber}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable Long categoryNumber) {
        Category category = categoryService.findByCategoryNumber(categoryNumber);
        return ResponseEntity.ok(categoryMapper.toDto(category));
    }

    // -- POST
    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CategoryRequest request) {
        Category categoryToSave = categoryMapper.toEntity(request);
        Category savedCategory = categoryService.save(categoryToSave);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryMapper.toDto(savedCategory));
    }

    // -- PUT
    @PutMapping("/{categoryNumber}")
    public ResponseEntity<CategoryDto> updateCategory(
            @PathVariable Long categoryNumber,
            @Valid @RequestBody CategoryRequest request) {

        Category categoryToUpdate = categoryMapper.toEntity(request);
        categoryToUpdate.setCategoryNumber(categoryNumber);

        Category updatedCategory = categoryService.update(categoryToUpdate);
        return ResponseEntity.ok(categoryMapper.toDto(updatedCategory));
    }

    // -- DELETE
    @DeleteMapping("/{categoryNumber}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryNumber) {
        categoryService.deleteById(categoryNumber);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}