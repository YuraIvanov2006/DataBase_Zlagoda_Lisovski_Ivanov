package org.lisovskyi_ivanov.backend.repository.category_repos;

import org.lisovskyi_ivanov.backend.entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    List<Category> findAll();
    List<Category> findAllByCategoryName(String categoryName);
    Optional<Category> findByCategoryNumber(Long categoryNumber);
    Category save(Category category);
    int update(Category category);
    int deleteByCategoryNumber(Long categoryNumber);
    boolean existsById(Long categoryNumber);
}
