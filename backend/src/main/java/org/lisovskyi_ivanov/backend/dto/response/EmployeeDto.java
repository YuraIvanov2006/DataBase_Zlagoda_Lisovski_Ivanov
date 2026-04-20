package org.lisovskyi_ivanov.backend.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EmployeeDto(
        Long idEmployee,
        String fullName,
        String emplRole,
        BigDecimal salary,
        LocalDate dateOfBirth,
        LocalDate dateOfStart,
        String emplPhoneNumber,
        String emplCity,
        String emplStreet,
        String emplZipCode
) {}