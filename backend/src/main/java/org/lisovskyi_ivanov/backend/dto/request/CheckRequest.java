package org.lisovskyi_ivanov.backend.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record CheckRequest(
        @NotBlank(message = "Номер чеку обов'язковий")
        @Size(max = 10)
        @NotNull
        String checkNumber,

        @NotNull(message = "ID працівника (касира) обов'язковий")
        Long idEmployee,

        @Size(max = 13)
        String cardNumber, // Може бути null

        @NotEmpty(message = "Чек не може бути порожнім")
        @Valid
        List<SaleRequest> sales
) {}