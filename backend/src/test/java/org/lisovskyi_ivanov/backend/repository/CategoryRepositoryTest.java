package org.lisovskyi_ivanov.backend.repository;

import org.junit.jupiter.api.Test;
import org.lisovskyi_ivanov.backend.entity.Category;
import org.lisovskyi_ivanov.backend.mapping.mapper.CategoryRowMapper;
import org.lisovskyi_ivanov.backend.repository.category_repos.CategoryRepository;
import org.lisovskyi_ivanov.backend.repository.category_repos.CategoryRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@ActiveProfiles("test")
@Import({CategoryRepositoryImpl.class, CategoryRowMapper.class})
class CategoryRepositoryTest {

    @Autowired
    CategoryRepository categoryRepository;

    @Test
    void findAll_shouldReturnEmpty_whenNoCategories() {
        var categories = categoryRepository.findAll();
        assertTrue(categories.isEmpty());
    }

    @Test
    void findAll_shouldReturnAllCategories_whenExist() {
        categoryRepository.save(buildTestCategory("Meat"));
        categoryRepository.save(buildTestCategory("Bakery"));

        var categories = categoryRepository.findAll();

        assertFalse(categories.isEmpty());
        assertEquals(2, categories.size());
    }

    @Test
    void findAllByCategoryName_shouldReturnCategories_whenExist() {
        var testCategory = buildTestCategory("Dairy");
        categoryRepository.save(testCategory);

        var categories = categoryRepository.findAllByCategoryName("Dairy");

        assertFalse(categories.isEmpty());
        assertEquals(1, categories.size());
        assertEquals("Dairy", categories.getFirst().getCategoryName());
    }

    @Test
    void findByCategoryNumber_shouldReturnCategory_whenExists() {
        var savedCategory = categoryRepository.save(buildTestCategory("Fruits"));

        var category = categoryRepository.findByCategoryNumber(savedCategory.getCategoryNumber());

        assertTrue(category.isPresent());
        assertEquals(savedCategory.getCategoryNumber(), category.get().getCategoryNumber());
    }

    @Test
    void findByCategoryNumber_shouldReturnEmpty_whenNotExists() {
        var testId = ThreadLocalRandom.current().nextLong(1, 1000000);
        var category = categoryRepository.findByCategoryNumber(testId);

        assertTrue(category.isEmpty());
    }

    @Test
    void save_shouldReturnCategoryWithId_whenSaved() {
        var testCategory = buildTestCategory("Vegetables");
        var savedCategory = categoryRepository.save(testCategory);

        assertNotNull(savedCategory.getCategoryNumber());
        assertEquals(testCategory.getCategoryName(), savedCategory.getCategoryName());
    }

    @Test
    void update_shouldUpdateCategory_whenExists() {
        var savedCategory = categoryRepository.save(buildTestCategory("Drinks"));

        savedCategory.setCategoryName("Alcohol");
        categoryRepository.update(savedCategory);

        var updatedCategory = categoryRepository.findByCategoryNumber(savedCategory.getCategoryNumber());

        assertTrue(updatedCategory.isPresent());
        assertEquals("Alcohol", updatedCategory.get().getCategoryName());
    }

    @Test
    void deleteByCategoryNumber_shouldRemoveCategory_whenExists() {
        var savedCategory = categoryRepository.save(buildTestCategory("Sweets"));
        var testId = savedCategory.getCategoryNumber();

        categoryRepository.deleteByCategoryNumber(testId);

        var deletedCategory = categoryRepository.findByCategoryNumber(testId);
        assertTrue(deletedCategory.isEmpty());
    }

    @Test
    void existsById_shouldReturnTrue_whenExists() {
        var savedCategory = categoryRepository.save(buildTestCategory("Snacks"));

        var exists = categoryRepository.existsById(savedCategory.getCategoryNumber());
        assertTrue(exists);
    }

    @Test
    void existsById_shouldReturnFalse_whenNotExists() {
        var testId = ThreadLocalRandom.current().nextLong(1, 1000000);

        var exists = categoryRepository.existsById(testId);
        assertFalse(exists);
    }

    Category buildTestCategory(String name) {
        return Category.builder()
                .categoryName(name)
                .build();
    }
}