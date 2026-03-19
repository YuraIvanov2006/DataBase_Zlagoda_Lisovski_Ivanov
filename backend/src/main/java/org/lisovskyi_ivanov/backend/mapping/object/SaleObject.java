package org.lisovskyi_ivanov.backend.mapping.object;

import org.lisovskyi_ivanov.backend.entity.Sale;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class SaleObject {
    private SaleObject() {
        throw new UnsupportedOperationException("SaleObject is a utility class and cannot be instantiated");
    }

    public static Sale getSale(ResultSet rs, int rowNum) throws SQLException {
        var storeProduct = StoreProductObject.getStoreProduct(rs, rowNum);
        var check = CheckObject.getCheck(rs, rowNum);

        return Sale.builder()
                .storeProduct(storeProduct)
                .check(check)
                .productNumber(rs.getInt("product_number"))
                .sellingPrice(rs.getBigDecimal("selling_price"))
                .build();
    }
}