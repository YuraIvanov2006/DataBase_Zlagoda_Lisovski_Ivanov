package org.lisovskyi_ivanov.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record StoreProductRequest(
        @NotBlank(message = "Штрих-код (UPC) обов'язковий")
        @Size(max = 12)
        @NotNull
        String upc,

        @NotNull(message = "ID базового продукту обов'язковий")
        Long idProduct,

        @Size(max = 12)
        String baseProductUpc, // Може бути null

        @NotNull(message = "Ціна обов'язкова")
        @PositiveOrZero(message = "Ціна не може бути від'ємною")
        BigDecimal sellingPrice,

        @NotNull(message = "Кількість обов'язкова")
        @PositiveOrZero(message = "Кількість не може бути від'ємною")
        Integer productsNumber,

        @NotNull(message = "Ознака акційного товару обов'язкова")
        Boolean promotionalProduct
) {}