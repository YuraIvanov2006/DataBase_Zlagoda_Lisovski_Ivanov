package org.lisovskyi_ivanov.backend.mapping.object;

import org.lisovskyi_ivanov.backend.entity.Check;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class CheckObject {
    private CheckObject() {
        throw new UnsupportedOperationException("CheckObject is a utility class and cannot be instantiated");
    }

    public static Check getCheck(ResultSet rs, int rowNum) throws SQLException {
        var employee = EmployeeObject.getEmployee(rs, rowNum);
        var customerCard = CustomerCardObject.getCustomerCard(rs, rowNum);
        var printDate = rs.getTimestamp("print_date");

        return Check.builder()
                .checkNumber(rs.getString("check_number"))
                .employee(employee)
                .customerCard(customerCard)
                .printDate(printDate != null ? printDate.toLocalDateTime() : null)
                .sumTotal(rs.getBigDecimal("sum_total"))
                .vat(rs.getBigDecimal("vat"))
                .build();
    }
}
