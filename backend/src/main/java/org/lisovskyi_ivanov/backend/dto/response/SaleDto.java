package org.lisovskyi_ivanov.backend.dto.response;

import java.math.BigDecimal;

public record SaleDto(
        String upc,
        String productName,
        String checkNumber,
        Integer productNumber,
        BigDecimal sellingPrice,
        BigDecimal totalRowPrice
) {}