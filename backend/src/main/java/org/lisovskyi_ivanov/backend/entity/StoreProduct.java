package org.lisovskyi_ivanov.backend.entity;

import lombok.*;

import java.math.BigDecimal;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(exclude = "baseProduct")
@ToString(exclude = "baseProduct")
public class StoreProduct {
    private String upc;
    private Product product;
    private StoreProduct baseProduct;
    private BigDecimal sellingPrice;
    private int productsNumber;
    private boolean promotionalProduct;
}
