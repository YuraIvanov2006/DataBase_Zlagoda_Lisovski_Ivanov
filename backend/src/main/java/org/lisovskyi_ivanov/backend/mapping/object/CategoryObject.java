package org.lisovskyi_ivanov.backend.mapping.object;

import org.lisovskyi_ivanov.backend.entity.Category;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class CategoryObject {
    private CategoryObject() {
        throw new UnsupportedOperationException("CategoryObject is a utility class and cannot be instantiated");
    }

    public static Category getCategory(ResultSet rs, int rowNum) throws SQLException {
        return Category.builder()
                .categoryNumber(rs.getLong("category_number"))
                .categoryName(rs.getString("category_name"))
                .build();
    }
}
