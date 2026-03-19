package org.lisovskyi_ivanov.backend.mapping.mapper;

import org.lisovskyi_ivanov.backend.entity.Check;
import org.lisovskyi_ivanov.backend.entity.CustomerCard;
import org.lisovskyi_ivanov.backend.entity.Employee;
import org.lisovskyi_ivanov.backend.enums.Role;
import org.lisovskyi_ivanov.backend.mapping.object.CheckObject;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class CheckRowMapper implements RowMapper<Check> {
    @Override
    public Check mapRow(ResultSet rs, int rowNum) throws SQLException {
        return CheckObject.getCheck(rs, rowNum);
    }
}