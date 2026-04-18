package org.lisovskyi_ivanov.backend.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum ExportEntities {
    EMPLOYEES("employees"),
    CUSTOMERS("customers"),
    PRODUCTS("products"),
    STORE_PRODUCTS("store-products"),
    CHECKS("checks");

    private final String value;

    ExportEntities(String value) {
        this.value = value;
    }

    public static Optional<String> getEntityFromString(String entityName) {
        if (entityName == null || entityName.isBlank())
            return Optional.empty();

        return Arrays.stream(ExportEntities.values())
                .filter(e -> e.value.equalsIgnoreCase(entityName))
                .map(e -> e.value)
                .findFirst();
    }
}
