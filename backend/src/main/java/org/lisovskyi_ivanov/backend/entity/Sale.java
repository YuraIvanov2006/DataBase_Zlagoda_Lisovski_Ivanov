package org.lisovskyi_ivanov.backend.entity;

import lombok.*;

import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @ToString(exclude = "check") @EqualsAndHashCode(exclude = "check")
public class Sale {
    private StoreProduct storeProduct;
    private Check check;
    private Integer productNumber;
    private BigDecimal sellingPrice;
}