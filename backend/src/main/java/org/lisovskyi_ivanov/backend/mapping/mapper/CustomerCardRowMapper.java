package org.lisovskyi_ivanov.backend.mapping.mapper;

import org.lisovskyi_ivanov.backend.entity.CustomerCard;
import org.lisovskyi_ivanov.backend.mapping.object.CustomerCardObject;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class CustomerCardRowMapper implements RowMapper<CustomerCard> {
    @Override
    public CustomerCard mapRow(ResultSet rs, int rowNum) throws SQLException {
        return CustomerCardObject.getCustomerCard(rs, rowNum);
    }
}