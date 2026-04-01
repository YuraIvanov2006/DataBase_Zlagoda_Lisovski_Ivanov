package org.lisovskyi_ivanov.backend.repository.product_repos;

import lombok.RequiredArgsConstructor;
import org.lisovskyi_ivanov.backend.entity.Product;
import org.lisovskyi_ivanov.backend.mapping.mapper.ProductRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {
    private static final String SELECT_ALL =
            """
            SELECT products.id_product, products.category_number, products.product_name, 
                   products.manufacturer, products.characteristics,
                   categories.category_name
            FROM products
            LEFT JOIN categories ON products.category_number = categories.category_number
            """;

    private final JdbcTemplate jdbc;
    private final NamedParameterJdbcTemplate namedJdbc;
    private final ProductRowMapper productRowMapper;

    @Override
    public List<Product> findAll() {
        return jdbc.query(SELECT_ALL + " ORDER BY products.id_product", productRowMapper);
    }

    @Override
    public List<Product> findByCategoryNumber(Long categoryNumber) {
        String sql = SELECT_ALL + " WHERE products.category_number = ? ORDER BY products.product_name";
        return jdbc.query(sql, productRowMapper, categoryNumber);
    }

    @Override
    public List<Product> findAllOrderByProductName() {
        String sql = SELECT_ALL + " ORDER BY products.product_name";
        return jdbc.query(sql, productRowMapper);
    }

    @Override
    public Optional<Product> findById(Long id) {
        String sql = SELECT_ALL + " WHERE products.id_product = ?";
        return jdbc.query(sql, productRowMapper, id).stream().findFirst();
    }

    @Override
    public Optional<Product> findByName(String name) {
        String sql = SELECT_ALL + " WHERE products.product_name = ?";
        return jdbc.query(sql, productRowMapper, name).stream().findFirst();
    }

    @Override
    public Product save(Product product) {
        String sql =
                """
                INSERT INTO products (
                    category_number, product_name, manufacturer, characteristics
                ) VALUES (
                    :category_number, :product_name, :manufacturer, :characteristics
                );
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedJdbc.update(sql, productParameters(product), keyHolder, new String[] {"id_product"});

        Long generatedId = keyHolder.getKeyAs(Long.class);
        return findById(generatedId)
                .orElseThrow(() -> new IllegalStateException("Failed to save product with ID: " + generatedId));
    }

    @Override
    public int update(Product product) {
        String sql =
                """
                UPDATE products SET
                    category_number = :category_number,
                    product_name = :product_name,
                    manufacturer = :manufacturer,
                    characteristics = :characteristics
                WHERE id_product = :id_product;
                """;

        return namedJdbc.update(sql, productParameters(product));
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(id_product) FROM products WHERE id_product = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    @Override
    public int deleteById(Long id) {
        String sql = "DELETE FROM products WHERE id_product = ?";
        return jdbc.update(sql, id);
    }

    private SqlParameterSource productParameters(Product product) {
        return new MapSqlParameterSource()
                .addValue("id_product", product.getIdProduct())
                .addValue("category_number", product.getCategory() != null  ? product.getCategory().getCategoryNumber() : null)
                .addValue("product_name", product.getProductName())
                .addValue("manufacturer", product.getManufacturer())
                .addValue("characteristics", product.getCharacteristics());
    }
}