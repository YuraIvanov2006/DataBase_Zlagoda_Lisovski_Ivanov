package org.lisovskyi_ivanov.backend.mapping.mapper;

import org.lisovskyi_ivanov.backend.entity.Category;
import org.lisovskyi_ivanov.backend.mapping.object.CategoryObject;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class CategoryRowMapper implements RowMapper<Category> {
    @Override
    public Category mapRow(ResultSet rs, int rowNum) throws SQLException {
        return CategoryObject.getCategory(rs, rowNum);
    }
}
