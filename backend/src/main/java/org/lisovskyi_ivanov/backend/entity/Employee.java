package org.lisovskyi_ivanov.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.lisovskyi_ivanov.backend.enums.Role;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Employee {
    private Long idEmployee;
    private String emplSurname;
    private String emplName;
    private String emplPatronymic;
    private Role emplRole;
    private BigDecimal salary;
    private LocalDate dateOfBirth;
    private LocalDate dateOfStart;
    private String emplPhoneNumber;
    private String emplCity;
    private String emplStreet;
    private String emplZipCode;
}
