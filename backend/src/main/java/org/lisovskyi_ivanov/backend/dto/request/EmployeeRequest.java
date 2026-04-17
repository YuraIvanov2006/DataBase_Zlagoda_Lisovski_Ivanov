package org.lisovskyi_ivanov.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.lisovskyi_ivanov.backend.enums.Role;
import java.math.BigDecimal;
import java.time.LocalDate;

public record EmployeeRequest(
        @NotBlank(message = "Прізвище обов'язкове")
        @Size(max = 50)
        @NotNull
        String emplSurname,

        @NotBlank(message = "Ім'я обов'язкове")
        @Size(max = 50)
        @NotNull
        String emplName,

        @Size(max = 50)
        String emplPatronymic, // Може бути null

        @NotNull(message = "Роль обов'язкова")
        Role emplRole,

        @NotNull(message = "Зарплата обов'язкова")
        BigDecimal salary,

        @NotNull(message = "Дата народження обов'язкова")
        LocalDate dateOfBirth,

        @NotNull(message = "Дата початку роботи обов'язкова")
        LocalDate dateOfStart,

        @NotBlank(message = "Номер телефону обов'язковий")
        @Size(max = 13)
        @NotNull
        @Pattern(regexp = "^\\+?[0-9]{1,12}$", message = "Невірний формат телефону")
        String emplPhoneNumber,

        @NotBlank(message = "Місто обов'язкове")
        @Size(max = 50)
        @NotNull
        String emplCity,

        @NotBlank(message = "Вулиця обов'язкова")
        @Size(max = 50)
        @NotNull
        String emplStreet,

        @NotBlank(message = "Індекс обов'язковий")
        @Size(max = 9)
        @NotNull
        String emplZipCode
) {}