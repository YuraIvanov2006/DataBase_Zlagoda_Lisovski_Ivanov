package org.lisovskyi_ivanov.backend.dto.response;

public record ProductDto(
        Long idProduct,
        String productName,
        String manufacturer,
        String characteristics,
        Long categoryNumber,
        String categoryName
) {}