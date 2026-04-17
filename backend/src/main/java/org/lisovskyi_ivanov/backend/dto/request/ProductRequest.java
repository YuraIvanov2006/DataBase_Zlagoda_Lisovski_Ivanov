package org.lisovskyi_ivanov.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProductRequest(
        @NotNull(message = "ID категорії є обов'язковим")
        Long categoryNumber,

        @NotBlank(message = "Назва продукту обов'язкова")
        @Size(max = 50)
        @NotNull
        String productName,

        @NotBlank(message = "Виробник обов'язковий")
        @Size(max = 255)
        @NotNull
        String manufacturer,

        @NotBlank(message = "Характеристики обов'язкові")
        @Size(max = 255)
        @NotNull
        String characteristics
) {}