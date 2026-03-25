package org.lisovskyi_ivanov.backend.mapping.mapper;

import org.lisovskyi_ivanov.backend.entity.Account;
import org.lisovskyi_ivanov.backend.mapping.object.AccountObject;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class AccountRowMapper implements RowMapper<Account> {
    @Override
    public Account mapRow(ResultSet rs, int rowNum) throws SQLException {
        return AccountObject.getAccount(rs, rowNum);
    }
}
