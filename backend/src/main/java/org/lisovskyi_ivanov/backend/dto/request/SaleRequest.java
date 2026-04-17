package org.lisovskyi_ivanov.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record SaleRequest(
        @NotBlank(message = "Штрих-код товару обов'язковий")
        @Size(max = 12)
        @NotNull
        String upc,

        @NotNull(message = "Кількість обов'язкова")
        @Positive(message = "Кількість має бути більше нуля")
        Integer productNumber
) {}