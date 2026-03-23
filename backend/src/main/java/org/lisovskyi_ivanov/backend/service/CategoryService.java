package org.lisovskyi_ivanov.backend.service;

import lombok.RequiredArgsConstructor;
import org.lisovskyi_ivanov.backend.entity.Category;
import org.lisovskyi_ivanov.backend.repository.category_repos.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository repository;

    @Transactional(readOnly = true)
    public List<Category> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Category> findByCategoryNumber(Long categoryNumber) {
        if (categoryNumber == null) {
            throw new IllegalArgumentException("Category number must not be null");
        }
        return repository.findByCategoryNumber(categoryNumber);
    }

    @Transactional(readOnly = true)
    public List<Category> findByCategoryName(String categoryName) {
        if (categoryName == null || categoryName.isBlank()) {
            throw new IllegalArgumentException("Category name must not be null or blank");
        }
        return repository.findAllByCategoryName(categoryName);
    }

    @Transactional
    public Category save(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Category must not be null");
        }
        return repository.save(category);
    }

    @Transactional
    public void update(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Category must not be null");
        }
        int updated = repository.update(category);
        if (updated == 0) {
            throw new IllegalArgumentException("Category with number " + category.getCategoryNumber() + " not found");
        }
    }

    @Transactional(readOnly = true)
    public boolean existsById(Long categoryNumber) {
        if (categoryNumber == null) {
            throw new IllegalArgumentException("Category number must not be null");
        }
        return repository.existsById(categoryNumber);
    }

    @Transactional
    public void deleteById(Long categoryNumber) {
        if (categoryNumber == null) {
            throw new IllegalArgumentException("Category number must not be null");
        }
        int deleted = repository.deleteById(categoryNumber);
        if (deleted == 0) {
            throw new IllegalArgumentException("Category with number " + categoryNumber + " not found");
        }
    }

    @Transactional
    public void delete(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Category must not be null");
        }
        int deleted = repository.delete(category);
        if (deleted == 0) {
            throw new IllegalArgumentException("Category with number " + category.getCategoryNumber() + " not found");
        }
    }
}