package org.lisovskyi_ivanov.backend.mapping.object;

import org.lisovskyi_ivanov.backend.entity.Product;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class ProductObject {
    private ProductObject() {
        throw new UnsupportedOperationException("ProductObject is a utility class and cannot be instantiated");
    }

    public static Product getProduct(ResultSet rs, int rowNum) throws SQLException {
        var category = CategoryObject.getCategory(rs, rowNum);

        return Product.builder()
                .idProduct(rs.getLong("id_product"))
                .category(category)
                .productName(rs.getString("product_name"))
                .manufacturer(rs.getString("manufacturer"))
                .characteristics(rs.getString("characteristics"))
                .build();
    }
}
