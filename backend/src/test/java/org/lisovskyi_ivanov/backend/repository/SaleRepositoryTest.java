package org.lisovskyi_ivanov.backend.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lisovskyi_ivanov.backend.entity.*;
import org.lisovskyi_ivanov.backend.enums.Role;
import org.lisovskyi_ivanov.backend.mapping.mapper.*;
import org.lisovskyi_ivanov.backend.repository.category_repos.CategoryRepositoryImpl;
import org.lisovskyi_ivanov.backend.repository.check_repos.CheckRepositoryImpl;
import org.lisovskyi_ivanov.backend.repository.customer_card_repos.CustomerCardRepositoryImpl;
import org.lisovskyi_ivanov.backend.repository.employee_repos.EmployeeRepositoryImpl;
import org.lisovskyi_ivanov.backend.repository.product_repos.ProductRepositoryImpl;
import org.lisovskyi_ivanov.backend.repository.sale_repos.SaleRepository;
import org.lisovskyi_ivanov.backend.repository.sale_repos.SaleRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@ActiveProfiles("test")
@Import({
        SaleRepositoryImpl.class, SaleRowMapper.class,
        CheckRepositoryImpl.class, CheckRowMapper.class,
        EmployeeRepositoryImpl.class, EmployeeRowMapper.class,
        CustomerCardRepositoryImpl.class, CustomerCardRowMapper.class,
        ProductRepositoryImpl.class, ProductRowMapper.class,
        CategoryRepositoryImpl.class, CategoryRowMapper.class
})
class SaleRepositoryTest {

    @Autowired
    SaleRepository saleRepository;

    @Autowired
    CheckRepositoryImpl checkRepository;

    @Autowired
    EmployeeRepositoryImpl employeeRepository;

    @Autowired
    CustomerCardRepositoryImpl customerCardRepository;

    @Autowired
    ProductRepositoryImpl productRepository;

    @Autowired
    CategoryRepositoryImpl categoryRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    private Check savedCheck;
    private StoreProduct savedStoreProduct;

    @BeforeEach
    void setUp() {
        var category = categoryRepository.save(Category.builder().categoryName("Food").build());

        var product = productRepository.save(Product.builder()
                .category(category)
                .productName("Bread")
                .manufacturer("KyivHlib")
                .characteristics("White")
                .build());

        var upc = UUID.randomUUID().toString().substring(0, 12);
        jdbcTemplate.update(
                "INSERT INTO store_products (upc, id_product, selling_price, products_number, promotional_product) VALUES (?, ?, ?, ?, ?)",
                upc, product.getIdProduct(), new BigDecimal("20.00"), 100, false
        );

        savedStoreProduct = StoreProduct.builder()
                .upc(upc)
                .product(product)
                .sellingPrice(new BigDecimal("20.00"))
                .productsNumber(100)
                .promotionalProduct(false)
                .build();

        var employee = employeeRepository.save(Employee.builder()
                .emplSurname("Petrov")
                .emplName("Petro")
                .emplRole(Role.CASHIER)
                .salary(new BigDecimal("10000.00"))
                .dateOfBirth(LocalDate.of(1995, 1, 1))
                .dateOfStart(LocalDate.of(2021, 1, 1))
                .emplPhoneNumber("+380991234567")
                .emplCity("Kyiv")
                .emplStreet("Street")
                .emplZipCode("00000")
                .build());

        var customerCard = customerCardRepository.save(CustomerCard.builder()
                .cardNumber(UUID.randomUUID().toString().substring(0, 13))
                .custSurname("Ivanov")
                .custName("Ivan")
                .custPhoneNumber("+380661234567")
                .custCity("Kyiv")
                .custStreet("Avenue")
                .custZipCode("11111")
                .percent(5)
                .build());

        savedCheck = checkRepository.save(Check.builder()
                .checkNumber(UUID.randomUUID().toString().substring(0, 10))
                .employee(employee)
                .customerCard(customerCard)
                .printDate(LocalDateTime.now())
                .sumTotal(new BigDecimal("100.00"))
                .vat(new BigDecimal("20.00"))
                .build());
    }

    @Test
    void findAll_shouldReturnEmpty_whenNoSales() {
        var sales = saleRepository.findAll();
        assertTrue(sales.isEmpty());
    }

    @Test
    void save_shouldReturnSavedSale() {
        var testSale = buildTestSale();
        var savedSale = saleRepository.save(testSale);

        assertNotNull(savedSale);
        assertEquals(testSale.getProductNumber(), savedSale.getProductNumber());
    }

    @Test
    void findById_shouldReturnSale_whenExists() {
        var testSale = buildTestSale();
        saleRepository.save(testSale);

        var sale = saleRepository.findById(testSale.getStoreProduct().getUpc(), testSale.getCheck().getCheckNumber());

        assertTrue(sale.isPresent());
        assertEquals(testSale.getProductNumber(), sale.get().getProductNumber());
    }

    @Test
    void update_shouldUpdateSale_whenExists() {
        var testSale = buildTestSale();
        saleRepository.save(testSale);

        testSale.setProductNumber(10);
        testSale.setSellingPrice(new BigDecimal("150.00"));
        saleRepository.update(testSale);

        var updatedSale = saleRepository.findById(testSale.getStoreProduct().getUpc(), testSale.getCheck().getCheckNumber());

        assertTrue(updatedSale.isPresent());
        assertEquals(10, updatedSale.get().getProductNumber());
        assertEquals(0, new BigDecimal("150.00").compareTo(updatedSale.get().getSellingPrice()));
    }

    @Test
    void deleteById_shouldRemoveSale_whenExists() {
        var testSale = buildTestSale();
        saleRepository.save(testSale);

        saleRepository.deleteById(testSale.getStoreProduct().getUpc(), testSale.getCheck().getCheckNumber());

        var deletedSale = saleRepository.findById(testSale.getStoreProduct().getUpc(), testSale.getCheck().getCheckNumber());
        assertTrue(deletedSale.isEmpty());
    }

    @Test
    void existsById_shouldReturnTrue_whenExists() {
        var testSale = buildTestSale();
        saleRepository.save(testSale);

        var exists = saleRepository.existsById(testSale.getStoreProduct().getUpc(), testSale.getCheck().getCheckNumber());
        assertTrue(exists);
    }

    @Test
    void findAllByCheckNumber_shouldReturnSales() {
        var testSale = buildTestSale();
        saleRepository.save(testSale);

        var sales = saleRepository.findAllByCheckNumber(testSale.getCheck().getCheckNumber());

        assertFalse(sales.isEmpty());
        assertEquals(testSale.getCheck().getCheckNumber(), sales.getFirst().getCheck().getCheckNumber());
    }

    @Test
    void findSalesByPriceRange_shouldReturnSales() {
        var testSale = buildTestSale();
        testSale.setSellingPrice(new BigDecimal("50.00"));
        saleRepository.save(testSale);

        var sales = saleRepository.findSalesByPriceRange(new BigDecimal("40.00"), new BigDecimal("60.00"));

        assertFalse(sales.isEmpty());
    }

    @Test
    void calculateTotalSumByCheckNumber_shouldReturnCorrectSum() {
        var testSale = buildTestSale();
        testSale.setProductNumber(2);
        testSale.setSellingPrice(new BigDecimal("25.00"));
        saleRepository.save(testSale);

        var total = saleRepository.calculateTotalSumByCheckNumber(testSale.getCheck().getCheckNumber());

        assertEquals(0, new BigDecimal("50.00").compareTo(total));
    }

    @Test
    void countTotalProductsSoldByUpc_shouldReturnCorrectCount() {
        var testSale = buildTestSale();
        testSale.setProductNumber(5);
        saleRepository.save(testSale);

        var count = saleRepository.countTotalProductsSoldByUpc(testSale.getStoreProduct().getUpc());

        assertEquals(5, count);
    }

    @Test
    void deleteAllByCheckNumber_shouldRemoveAllSalesForCheck() {
        var testSale = buildTestSale();
        saleRepository.save(testSale);

        saleRepository.deleteAllByCheckNumber(testSale.getCheck().getCheckNumber());

        var sales = saleRepository.findAllByCheckNumber(testSale.getCheck().getCheckNumber());
        assertTrue(sales.isEmpty());
    }

    Sale buildTestSale() {
        return Sale.builder()
                .storeProduct(savedStoreProduct)
                .check(savedCheck)
                .productNumber(2)
                .sellingPrice(new BigDecimal("45.50"))
                .build();
    }
}