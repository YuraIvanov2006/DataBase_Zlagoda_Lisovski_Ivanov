package org.lisovskyi_ivanov.backend.repository.sale_repos;

import lombok.RequiredArgsConstructor;
import org.lisovskyi_ivanov.backend.entity.Sale;
import org.lisovskyi_ivanov.backend.mapping.mapper.SaleRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SaleRepositoryImpl implements SaleRepository {
    private static final String SELECT_ALL =
            """
            SELECT s.upc, s.check_number, s.product_number, s.selling_price,
                   sp.id_product, sp.upc_prom, sp.selling_price AS sp_selling_price, sp.products_number, sp.promotional_product,
                   p.category_number, p.product_name, p.manufacturer, p.characteristics,
                   cat.category_name,
                   ch.id_employee, ch.card_number, ch.print_date, ch.sum_total, ch.vat,
                   e.empl_surname, e.empl_name, e.empl_patronymic, e.empl_role, 
                   e.salary, e.date_of_birth, e.date_of_start, e.empl_phone_number, 
                   e.empl_city, e.empl_street, e.empl_zip_code,
                   cc.cust_surname, cc.cust_name, cc.cust_patronymic, cc.cust_phone_number, 
                   cc.cust_city, cc.cust_street, cc.cust_zip_code, cc.percent,
                   sp.upc_prom AS base_product_upc
            FROM sales s
            JOIN store_products sp ON s.upc = sp.upc
            JOIN products p ON sp.id_product = p.id_product
            JOIN categories cat ON p.category_number = cat.category_number
            JOIN checks ch ON s.check_number = ch.check_number
            JOIN employees e ON ch.id_employee = e.id_employee
            LEFT JOIN customer_cards cc ON ch.card_number = cc.card_number
            """;

    private final JdbcTemplate jdbc;
    private final NamedParameterJdbcTemplate namedJdbc;
    private final SaleRowMapper saleRowMapper;

    @Override
    public List<Sale> findAll() {
        return jdbc.query(SELECT_ALL, saleRowMapper);
    }

    @Override
    public Optional<Sale> findById(String upc, String checkNumber) {
        String sql = SELECT_ALL + " WHERE s.upc = ? AND s.check_number = ?";
        return jdbc.query(sql, saleRowMapper, upc, checkNumber)
                .stream()
                .findFirst();
    }

    @Override
    public Sale save(Sale sale) {
        String sql =
                """
                INSERT INTO sales (
                    upc, check_number, product_number, selling_price
                ) VALUES (
                    :upc, :check_number, :product_number, :selling_price
                );
                """;
        namedJdbc.update(sql, saleParameters(sale));
        return sale;
    }

    @Override
    public int update(Sale sale) {
        String sql = """
            UPDATE sales
            SET product_number = ?, selling_price = ?
            WHERE upc = ? AND check_number = ?
            """;
        return jdbc.update(sql,
                sale.getProductNumber(),
                sale.getSellingPrice(),
                sale.getStoreProduct().getUpc(),
                sale.getCheck().getCheckNumber()
        );
    }

    @Override
    public int deleteById(String upc, String checkNumber) {
        String sql = "DELETE FROM sales WHERE upc = ? AND check_number = ?";
        return jdbc.update(sql, upc, checkNumber);
    }

    @Override
    public int delete(Sale sale) {
        return deleteById(sale.getStoreProduct().getUpc(), sale.getCheck().getCheckNumber());
    }

    @Override
    public boolean existsById(String upc, String checkNumber) {
        String sql = "SELECT COUNT(upc) FROM sales WHERE upc = ? AND check_number = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, upc, checkNumber);
        return count != null && count > 0;
    }

    @Override
    public List<Sale> findAllByCheckNumber(String checkNumber) {
        String sql = SELECT_ALL + " WHERE s.check_number = ?";
        return jdbc.query(sql, saleRowMapper, checkNumber);
    }

    @Override
    public List<Sale> findAllByUpc(String upc) {
        String sql = SELECT_ALL + " WHERE s.upc = ?";
        return jdbc.query(sql, saleRowMapper, upc);
    }

    @Override
    public List<Sale> findAllByCustSurname(String custSurname) {
        String sql = SELECT_ALL + " WHERE cc.cust_surname = ?";
        return jdbc.query(sql, saleRowMapper, custSurname);
    }

    // ТУТ ЗМІНЕНО String НА Long
    @Override
    public List<Sale> findAllByEmployeeId(Long employeeId) {
        String sql = SELECT_ALL + " WHERE e.id_employee = ?";
        return jdbc.query(sql, saleRowMapper, employeeId);
    }

    @Override
    public List<Sale> findSalesByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        String sql = SELECT_ALL + " WHERE s.selling_price BETWEEN ? AND ?";
        return jdbc.query(sql, saleRowMapper, minPrice, maxPrice);
    }

    @Override
    public List<Sale> findSalesByProductNumberGreaterThan(Integer amount) {
        String sql = SELECT_ALL + " WHERE s.product_number > ?";
        return jdbc.query(sql, saleRowMapper, amount);
    }

    @Override
    public BigDecimal calculateTotalSumByCheckNumber(String checkNumber) {
        String sql = "SELECT SUM(selling_price * product_number) FROM sales WHERE check_number = ?";
        return jdbc.queryForObject(sql, BigDecimal.class, checkNumber);
    }

    @Override
    public Integer countTotalProductsSoldByUpc(String upc) {
        String sql = "SELECT SUM(product_number) FROM sales WHERE upc = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, upc);
        return count != null ? count : 0;
    }

    @Override
    public int deleteAllByCheckNumber(String checkNumber) {
        String sql = "DELETE FROM sales WHERE check_number = ?";
        return jdbc.update(sql, checkNumber);
    }

    private SqlParameterSource saleParameters(Sale sale) {
        return new MapSqlParameterSource()
                .addValue("upc", sale.getStoreProduct() != null ? sale.getStoreProduct().getUpc() : null)
                .addValue("check_number", sale.getCheck() != null ? sale.getCheck().getCheckNumber() : null)
                .addValue("product_number", sale.getProductNumber())
                .addValue("selling_price", sale.getSellingPrice());
    }
}