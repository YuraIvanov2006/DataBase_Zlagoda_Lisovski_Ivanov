package org.lisovskyi_ivanov.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CustomerCardRequest(
        @NotBlank(message = "Номер карти обов'язковий")
        @Size(max = 13)
        @NotNull
        String cardNumber,

        @NotBlank(message = "Прізвище обов'язкове")
        @Size(max = 50)
        @NotNull
        String custSurname,

        @NotBlank(message = "Ім'я обов'язкове")
        @Size(max = 50)
        @NotNull
        String custName,

        @Size(max = 50)
        String custPatronymic,

        @NotBlank(message = "Номер телефону обов'язковий")
        @Size(max = 13)
        @NotNull
        @Pattern(regexp = "^\\+?[0-9]{1,12}$", message = "Невірний формат телефону")
        String custPhoneNumber,

        @Size(max = 50)
        String custCity,

        @Size(max = 50)
        String custStreet,

        @Size(max = 9)
        String custZipCode,

        @NotNull(message = "Відсоток знижки обов'язковий")
        Integer percent
) {}