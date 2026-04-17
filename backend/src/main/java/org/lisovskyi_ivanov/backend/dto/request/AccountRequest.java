package org.lisovskyi_ivanov.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AccountRequest(
        @NotNull(message = "ID працівника обов'язковий")
        Long idEmployee,

        @NotBlank(message = "Логін обов'язковий")
        @Size(max = 100)
        @NotNull
        String login,

        @NotBlank(message = "Пароль обов'язковий")
        @Size(max = 255)
        @NotNull
        String password
) {}