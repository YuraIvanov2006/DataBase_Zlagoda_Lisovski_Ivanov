package org.lisovskyi_ivanov.backend.mapping.mapper;

import org.lisovskyi_ivanov.backend.entity.Category;
import org.lisovskyi_ivanov.backend.entity.Product;
import org.lisovskyi_ivanov.backend.entity.StoreProduct;
import org.lisovskyi_ivanov.backend.mapping.object.StoreProductObject;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class StoreProductRowMapper implements RowMapper<StoreProduct> {
    @Override
    public StoreProduct mapRow(ResultSet rs, int rowNum) throws SQLException {
        return StoreProductObject.getStoreProduct(rs, rowNum);
    }
}
