package org.lisovskyi_ivanov.backend.dto.response;

import java.math.BigDecimal;

public record StoreProductDto(
        String upc,
        ProductDto product,
        String baseProductUpc,
        BigDecimal sellingPrice,
        int productsNumber,
        boolean promotionalProduct
) {}