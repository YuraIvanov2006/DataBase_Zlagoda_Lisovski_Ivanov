package org.lisovskyi_ivanov.backend.mapping.mapper;

import org.lisovskyi_ivanov.backend.entity.Product;
import org.lisovskyi_ivanov.backend.mapping.object.ProductObject;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ProductRowMapper implements RowMapper<Product> {
    @Override
    public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
        return ProductObject.getProduct(rs, rowNum);
    }
}
