package org.lisovskyi_ivanov.backend.mapping.object;

import org.lisovskyi_ivanov.backend.entity.StoreProduct;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class StoreProductObject {
    private StoreProductObject() {
        throw new UnsupportedOperationException("StoreProductObject is a utility class and cannot be instantiated");
    }

    public static StoreProduct getStoreProduct(ResultSet rs, int rowNum) throws SQLException {
        var product = ProductObject.getProduct(rs, rowNum);

        // recursive link to the base product
        String baseProductUpc = rs.getString("base_product_upc");
        StoreProduct baseProduct = null;
        if (baseProductUpc != null) {
            baseProduct = StoreProduct.builder()
                    .upc(baseProductUpc)
                    .build();
        }

        return StoreProduct.builder()
                .upc(rs.getString("upc"))
                .product(product)
                .baseProduct(baseProduct)
                .sellingPrice(rs.getBigDecimal("selling_price"))
                .productsNumber(rs.getInt("products_number"))
                .promotionalProduct(rs.getBoolean("promotional_product"))
                .build();
    }
}
