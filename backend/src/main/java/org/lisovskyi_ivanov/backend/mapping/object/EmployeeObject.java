package org.lisovskyi_ivanov.backend.mapping.object;

import org.lisovskyi_ivanov.backend.entity.Employee;
import org.lisovskyi_ivanov.backend.enums.Role;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class EmployeeObject {
    private EmployeeObject() {
        throw new UnsupportedOperationException("EmployeeObject is a utility class and cannot be instantiated");
    }

    public static Employee getEmployee(ResultSet rs, int rowNum) throws SQLException {
        var dateOfBirth = rs.getDate("date_of_birth");
        var dateOfStart = rs.getDate("date_of_start");

        return Employee.builder()
                .idEmployee(rs.getLong("id_employee"))
                .emplSurname(rs.getString("empl_surname"))
                .emplName(rs.getString("empl_name"))
                .emplPatronymic(rs.getString("empl_patronymic"))
                .emplRole(Role.getRoleFromString(rs.getString("role")).orElse(null))
                .salary(rs.getBigDecimal("salary"))
                .dateOfBirth(dateOfBirth != null ? dateOfBirth.toLocalDate() : null)
                .dateOfStart(dateOfStart != null ? dateOfStart.toLocalDate() : null)
                .emplPhoneNumber(rs.getString("empl_phone_number"))
                .emplCity(rs.getString("empl_city"))
                .emplStreet(rs.getString("empl_street"))
                .emplZipCode(rs.getString("empl_zip_code"))
                .build();
    }
}
