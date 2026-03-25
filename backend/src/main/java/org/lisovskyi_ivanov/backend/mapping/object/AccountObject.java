package org.lisovskyi_ivanov.backend.mapping.object;

import org.lisovskyi_ivanov.backend.entity.Account;
import org.lisovskyi_ivanov.backend.enums.Authority;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class AccountObject {
    private AccountObject() {
        throw new UnsupportedOperationException("AccountObject is a utility class and cannot be instantiated");
    }

    public static Account getAccount(ResultSet rs, int rowNum) throws SQLException {
        var employee = EmployeeObject.getEmployee(rs, rowNum);

        return Account.builder()
                .idAccount(rs.getLong("id_account"))
                .employee(employee)
                .login(rs.getString("login"))
                .password(rs.getString("password"))
                .authority(Authority.getRoleFromString(rs.getString("authority")).orElse(null))
                .build();
    }
}
