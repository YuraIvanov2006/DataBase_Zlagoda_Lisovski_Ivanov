package org.lisovskyi_ivanov.backend.repository.category_repos;

import org.lisovskyi_ivanov.backend.entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    List<Category> findAll();
    Optional<Category> findById(Long id);
    Category save(Category category);
    void update(Category category);
    void delete(Category category);
    void deleteById(Long id);
    boolean existsById(Long id);
}
