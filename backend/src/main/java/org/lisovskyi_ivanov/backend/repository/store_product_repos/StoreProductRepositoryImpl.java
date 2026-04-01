package org.lisovskyi_ivanov.backend.repository.store_product_repos;

import lombok.RequiredArgsConstructor;
import org.lisovskyi_ivanov.backend.entity.StoreProduct;
import org.lisovskyi_ivanov.backend.mapping.mapper.StoreProductRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class StoreProductRepositoryImpl implements StoreProductRepository {
    private static final String SELECT_ALL =
            """
            SELECT sp.upc, sp.id_product, sp.upc_prom, sp.selling_price, 
                   sp.products_number, sp.promotional_product,
                   p.product_name, p.manufacturer, p.characteristics, p.category_number,
                   c.category_name,
                   sp.upc_prom AS base_product_upc
            FROM store_products sp
            INNER JOIN products p ON sp.id_product = p.id_product
            INNER JOIN categories c ON p.category_number = c.category_number
            """;

    private final JdbcTemplate jdbc;
    private final NamedParameterJdbcTemplate namedJdbc;
    private final StoreProductRowMapper storeProductRowMapper;

    @Override
    public List<StoreProduct> findAll() {
        return jdbc.query(SELECT_ALL + " ORDER BY sp.upc", storeProductRowMapper);
    }

    @Override
    public List<StoreProduct> findAllPromotional() {
        return findAllByPromotionalOrNot(true);
    }

    @Override
    public List<StoreProduct> findAllNotPromotional() {
        return findAllByPromotionalOrNot(false);
    }

    @Override
    public List<StoreProduct> findAllByProductId(Long productId) {
        String sql = SELECT_ALL + " WHERE sp.id_product = ? ORDER BY sp.upc";
        return jdbc.query(sql, storeProductRowMapper, productId);
    }

    @Override
    public Optional<StoreProduct> findByUPC(String upc) {
        String sql = SELECT_ALL + " WHERE sp.upc = ?";
        return jdbc.query(sql, storeProductRowMapper, upc)
                .stream()
                .findFirst();
    }

    @Override
    public StoreProduct save(StoreProduct storeProduct) {
        String sql =
                """
                INSERT INTO store_products (
                    upc, id_product, upc_prom, selling_price, products_number, promotional_product
                ) VALUES (
                    :upc, :id_product, :upc_prom, :selling_price, :products_number, :promotional_product
                )
                """;

        namedJdbc.update(sql, storeProductParameters(storeProduct));
        return findByUPC(storeProduct.getUpc()).orElseThrow();
    }

    @Override
    public int update(StoreProduct storeProduct) {
        String sql =
                """
                UPDATE store_products SET
                    id_product = :id_product,
                    upc_prom = :upc_prom,
                    selling_price = :selling_price,
                    products_number = :products_number,
                    promotional_product = :promotional_product
                WHERE upc = :upc;
                """;

        return namedJdbc.update(sql, storeProductParameters(storeProduct));
    }

    @Override
    public int delete(StoreProduct storeProduct) {
        return deleteByUPC(storeProduct.getUpc());
    }

    @Override
    public int deleteByUPC(String upc) {
        String sql = "DELETE FROM store_products WHERE upc = ?";
        return jdbc.update(sql, upc);
    }

    private List<StoreProduct> findAllByPromotionalOrNot(boolean promotional) {
        String sql = SELECT_ALL + " WHERE sp.promotional_product = ? ORDER BY sp.upc";
        return jdbc.query(sql, storeProductRowMapper, promotional);
    }

    private SqlParameterSource storeProductParameters(StoreProduct storeProduct) {
        return new MapSqlParameterSource()
                .addValue("upc", storeProduct.getUpc())
                .addValue("id_product", storeProduct.getProduct().getIdProduct())
                .addValue("upc_prom", storeProduct.getBaseProduct() != null ? storeProduct.getBaseProduct().getUpc() : null)
                .addValue("selling_price", storeProduct.getSellingPrice())
                .addValue("products_number", storeProduct.getProductsNumber())
                .addValue("promotional_product", storeProduct.isPromotionalProduct());
    }
}