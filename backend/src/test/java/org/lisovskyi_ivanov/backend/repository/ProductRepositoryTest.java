package org.lisovskyi_ivanov.backend.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lisovskyi_ivanov.backend.entity.Category;
import org.lisovskyi_ivanov.backend.entity.Product;
import org.lisovskyi_ivanov.backend.mapping.mapper.CategoryRowMapper;
import org.lisovskyi_ivanov.backend.mapping.mapper.ProductRowMapper;
import org.lisovskyi_ivanov.backend.repository.category_repos.CategoryRepository;
import org.lisovskyi_ivanov.backend.repository.category_repos.CategoryRepositoryImpl;
import org.lisovskyi_ivanov.backend.repository.product_repos.ProductRepository;
import org.lisovskyi_ivanov.backend.repository.product_repos.ProductRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@ActiveProfiles("test")
@Import({
        ProductRepositoryImpl.class, ProductRowMapper.class,
        CategoryRepositoryImpl.class, CategoryRowMapper.class
})
class ProductRepositoryTest {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CategoryRepository categoryRepository;

    private Category savedCategory;

    @BeforeEach
    void setUp() {
        // Зберігаємо категорію перед тестами, щоб не було помилок Foreign Key
        savedCategory = categoryRepository.save(Category.builder()
                .categoryName("Test Category")
                .build());
    }

    @Test
    void findAll_shouldReturnEmpty_whenNoProducts() {
        var products = productRepository.findAll();
        assertTrue(products.isEmpty());
    }

    @Test
    void findAll_shouldReturnAllProducts_whenExist() {
        productRepository.save(buildTestProduct("Product A"));
        productRepository.save(buildTestProduct("Product B"));

        var products = productRepository.findAll();

        assertFalse(products.isEmpty());
        assertEquals(2, products.size());
    }

    @Test
    void findByCategoryNumber_shouldReturnProducts_whenMatch() {
        var testProduct = buildTestProduct("Categorized Product");
        productRepository.save(testProduct);

        var products = productRepository.findByCategoryNumber(savedCategory.getCategoryNumber());

        assertFalse(products.isEmpty());
        assertEquals("Categorized Product", products.getFirst().getProductName());
    }

    @Test
    void findAllOrderByProductName_shouldReturnSortedProducts() {
        productRepository.save(buildTestProduct("Zebra"));
        productRepository.save(buildTestProduct("Apple"));

        var products = productRepository.findAllOrderByProductName();

        assertFalse(products.isEmpty());
        assertEquals("Apple", products.getFirst().getProductName());
        assertEquals("Zebra", products.getLast().getProductName());
    }

    @Test
    void findById_shouldReturnProduct_whenExists() {
        var savedProduct = productRepository.save(buildTestProduct("Find Me"));

        var product = productRepository.findById(savedProduct.getIdProduct());

        assertTrue(product.isPresent());
        assertEquals(savedProduct.getIdProduct(), product.get().getIdProduct());
    }

    @Test
    void findById_shouldReturnEmpty_whenNotExists() {
        var testId = ThreadLocalRandom.current().nextLong(1, 1000000);
        var product = productRepository.findById(testId);

        assertTrue(product.isEmpty());
    }

    @Test
    void findByName_shouldReturnProduct_whenExists() {
        var testName = "Unique Product Name";
        productRepository.save(buildTestProduct(testName));

        var product = productRepository.findByName(testName);

        assertTrue(product.isPresent());
        assertEquals(testName, product.get().getProductName());
    }

    @Test
    void save_shouldReturnSavedProductWithId() {
        var testProduct = buildTestProduct("New Product");
        var savedProduct = productRepository.save(testProduct);

        assertNotNull(savedProduct.getIdProduct());
        assertEquals(testProduct.getProductName(), savedProduct.getProductName());
        assertEquals(savedCategory.getCategoryNumber(), savedProduct.getCategory().getCategoryNumber());
    }

    @Test
    void update_shouldUpdateProduct_whenExists() {
        var savedProduct = productRepository.save(buildTestProduct("Old Product"));

        savedProduct.setProductName("Updated Product");
        savedProduct.setManufacturer("New Manufacturer");
        productRepository.update(savedProduct);

        var updatedProduct = productRepository.findById(savedProduct.getIdProduct());

        assertTrue(updatedProduct.isPresent());
        assertEquals("Updated Product", updatedProduct.get().getProductName());
        assertEquals("New Manufacturer", updatedProduct.get().getManufacturer());
    }

    @Test
    void existsById_shouldReturnTrue_whenExists() {
        var savedProduct = productRepository.save(buildTestProduct("Check Exists"));

        var exists = productRepository.existsById(savedProduct.getIdProduct());
        assertTrue(exists);
    }

    @Test
    void existsById_shouldReturnFalse_whenNotExists() {
        var testId = ThreadLocalRandom.current().nextLong(1, 1000000);

        var exists = productRepository.existsById(testId);
        assertFalse(exists);
    }

    @Test
    void deleteById_shouldRemoveProduct_whenExists() {
        var savedProduct = productRepository.save(buildTestProduct("To Delete By Id"));
        var testId = savedProduct.getIdProduct();

        productRepository.deleteById(testId);

        var deletedProduct = productRepository.findById(testId);
        assertTrue(deletedProduct.isEmpty());
    }

    @Test
    void delete_shouldRemoveProduct_whenExists() {
        var savedProduct = productRepository.save(buildTestProduct("To Delete"));

//        productRepository.delete(savedProduct);

        var deletedProduct = productRepository.findById(savedProduct.getIdProduct());
        assertTrue(deletedProduct.isEmpty());
    }

    // Допоміжний метод для створення тестового продукту
    Product buildTestProduct(String name) {
        return Product.builder()
                .category(savedCategory) // Використовуємо попередньо збережену категорію
                .productName(name)
                .manufacturer("Test Manufacturer")
                .characteristics("Test Characteristics")
                .build();
    }
}