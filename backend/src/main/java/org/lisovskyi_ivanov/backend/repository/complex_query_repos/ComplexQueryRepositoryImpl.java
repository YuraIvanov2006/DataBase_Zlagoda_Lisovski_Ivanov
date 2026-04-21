package org.lisovskyi_ivanov.backend.repository.complex_query_repos;

import lombok.RequiredArgsConstructor;
import org.lisovskyi_ivanov.backend.dto.response.CategorySaleDto;
import org.lisovskyi_ivanov.backend.dto.response.ProductSoldByAllDto;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ComplexQueryRepositoryImpl implements ComplexQueryRepository {

    private final NamedParameterJdbcTemplate namedJdbc;

    @Override
    public List<CategorySaleDto> getCategorySales(LocalDateTime startDate, LocalDateTime endDate) {
        String sql = """
            SELECT 
                c.category_name, 
                SUM(s.product_number) AS total_amount, 
                SUM(s.product_number * s.selling_price) AS total_sum
            FROM categories c
            JOIN products p ON c.category_number = p.category_number
            JOIN store_products sp ON p.id_product = sp.id_product
            JOIN sales s ON sp.upc = s.upc
            JOIN checks ch ON s.check_number = ch.check_number
            WHERE ch.print_date BETWEEN :startDate AND :endDate
            GROUP BY c.category_number, c.category_name
            ORDER BY total_sum DESC
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("startDate", startDate)
                .addValue("endDate", endDate);

        return namedJdbc.query(sql, params, (rs, rowNum) -> new CategorySaleDto(
                rs.getString("category_name"),
                rs.getInt("total_amount"),
                rs.getBigDecimal("total_sum")
        ));
    }

    @Override
    public List<ProductSoldByAllDto> getProductsSoldByAllCashiers() {
        String sql = """
            SELECT p.id_product, p.product_name
            FROM products p
            WHERE NOT EXISTS (
                SELECT e.id_employee
                FROM employees e
                WHERE e.empl_role = 'cashier'
                AND NOT EXISTS (
                    SELECT 1
                    FROM sales s
                    JOIN checks ch ON s.check_number = ch.check_number
                    JOIN store_products sp ON s.upc = sp.upc
                    WHERE ch.id_employee = e.id_employee
                      AND sp.id_product = p.id_product
                )
            )
        """;

        return namedJdbc.query(sql, new MapSqlParameterSource(), (rs, rowNum) -> new ProductSoldByAllDto(
                rs.getLong("id_product"),
                rs.getString("product_name")
        ));
    }
}
