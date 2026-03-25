package org.lisovskyi_ivanov.backend.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lisovskyi_ivanov.backend.entity.Category;
import org.lisovskyi_ivanov.backend.entity.Product;
import org.lisovskyi_ivanov.backend.entity.StoreProduct;
import org.lisovskyi_ivanov.backend.mapping.mapper.CategoryRowMapper;
import org.lisovskyi_ivanov.backend.mapping.mapper.ProductRowMapper;
import org.lisovskyi_ivanov.backend.mapping.mapper.StoreProductRowMapper;
import org.lisovskyi_ivanov.backend.repository.category_repos.CategoryRepositoryImpl;
import org.lisovskyi_ivanov.backend.repository.product_repos.ProductRepositoryImpl;
import org.lisovskyi_ivanov.backend.repository.store_product_repos.StoreProductRepository;
import org.lisovskyi_ivanov.backend.repository.store_product_repos.StoreProductRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@ActiveProfiles("test")
@Import({
        StoreProductRepositoryImpl.class, StoreProductRowMapper.class,
        ProductRepositoryImpl.class, ProductRowMapper.class,
        CategoryRepositoryImpl.class, CategoryRowMapper.class
})
class StoreProductRepositoryTest {

    @Autowired
    StoreProductRepository storeProductRepository;

    @Autowired
    ProductRepositoryImpl productRepository;

    @Autowired
    CategoryRepositoryImpl categoryRepository;

    private Product savedProduct;

    @BeforeEach
    void setUp() {
        // Зберігаємо залежності ПЕРЕД тестами
        var category = categoryRepository.save(Category.builder()
                .categoryName("Dairy")
                .build());

        savedProduct = productRepository.save(Product.builder()
                .category(category)
                .productName("Milk")
                .manufacturer("Yagotynske")
                .characteristics("2.6% fat")
                .build());
    }

    @Test
    void findAll_shouldReturnEmpty_whenNoStoreProducts() {
        var products = storeProductRepository.findAll();
        assertTrue(products.isEmpty());
    }

    @Test
    void findAll_shouldReturnAllStoreProducts() {
        storeProductRepository.save(buildTestStoreProduct(false));
        storeProductRepository.save(buildTestStoreProduct(true));

        var products = storeProductRepository.findAll();

        assertFalse(products.isEmpty());
        assertEquals(2, products.size());
    }

    @Test
    void findAllPromotional_shouldReturnOnlyPromotional() {
        storeProductRepository.save(buildTestStoreProduct(false));
        var promoProduct = storeProductRepository.save(buildTestStoreProduct(true));

        var promoList = storeProductRepository.findAllPromotional();

        assertEquals(1, promoList.size());
        assertEquals(promoProduct.getUpc(), promoList.getFirst().getUpc());
        assertTrue(promoList.getFirst().isPromotionalProduct());
    }

    @Test
    void findAllNotPromotional_shouldReturnOnlyNotPromotional() {
        var normalProduct = storeProductRepository.save(buildTestStoreProduct(false));
        storeProductRepository.save(buildTestStoreProduct(true));

        var normalList = storeProductRepository.findAllNotPromotional();

        assertEquals(1, normalList.size());
        assertEquals(normalProduct.getUpc(), normalList.getFirst().getUpc());
        assertFalse(normalList.getFirst().isPromotionalProduct());
    }

    @Test
    void findAllByProductId_shouldReturnMatchingProducts() {
        var testProduct = buildTestStoreProduct(false);
        storeProductRepository.save(testProduct);

        var foundProducts = storeProductRepository.findAllByProductId(savedProduct.getIdProduct());

        assertFalse(foundProducts.isEmpty());
        assertEquals(savedProduct.getIdProduct(), foundProducts.getFirst().getProduct().getIdProduct());
    }

    @Test
    void findByUPC_shouldReturnProduct_whenExists() {
        var testProduct = buildTestStoreProduct(false);
        storeProductRepository.save(testProduct);

        var found = storeProductRepository.findByUPC(testProduct.getUpc());

        assertTrue(found.isPresent());
        assertEquals(testProduct.getUpc(), found.get().getUpc());
    }

    @Test
    void findByUPC_shouldReturnEmpty_whenNotExists() {
        var found = storeProductRepository.findByUPC("NON_EXISTING");
        assertTrue(found.isEmpty());
    }

    @Test
    void save_shouldReturnSavedProduct() {
        var testProduct = buildTestStoreProduct(false);
        var saved = storeProductRepository.save(testProduct);

        assertNotNull(saved);
        assertEquals(testProduct.getUpc(), saved.getUpc());
    }

    @Test
    void update_shouldUpdateProduct_whenExists() {
        var testProduct = buildTestStoreProduct(false);
        storeProductRepository.save(testProduct);

        testProduct.setProductsNumber(500);
        testProduct.setSellingPrice(new BigDecimal("45.50"));
        storeProductRepository.update(testProduct);

        var updated = storeProductRepository.findByUPC(testProduct.getUpc());

        assertTrue(updated.isPresent());
        assertEquals(500, updated.get().getProductsNumber());
        assertEquals(0, new BigDecimal("45.50").compareTo(updated.get().getSellingPrice()));
    }

    @Test
    void deleteByUPC_shouldRemoveProduct_whenExists() {
        var testProduct = buildTestStoreProduct(false);
        storeProductRepository.save(testProduct);

        storeProductRepository.deleteByUPC(testProduct.getUpc());

        var deleted = storeProductRepository.findByUPC(testProduct.getUpc());
        assertTrue(deleted.isEmpty());
    }

    @Test
    void delete_shouldRemoveProduct_whenExists() {
        var testProduct = buildTestStoreProduct(false);
        storeProductRepository.save(testProduct);

        storeProductRepository.delete(testProduct);

        var deleted = storeProductRepository.findByUPC(testProduct.getUpc());
        assertTrue(deleted.isEmpty());
    }

    // Допоміжний метод для створення товару в магазині
    StoreProduct buildTestStoreProduct(boolean isPromotional) {
        return StoreProduct.builder()
                .upc(UUID.randomUUID().toString().substring(0, 12))
                .product(savedProduct) // Використовуємо вже збережений у БД Product
                .sellingPrice(new BigDecimal("35.00"))
                .productsNumber(100)
                .promotionalProduct(isPromotional)
                .baseProduct(null) // Якщо це акційний товар, тут мав би бути лінк на базовий
                .build();
    }
}