package org.lisovskyi_ivanov.backend.entity;

import lombok.*;
import java.math.BigDecimal;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Sale {
    private StoreProduct storeProduct;
    private Check check;
    private Integer productNumber;
    private BigDecimal sellingPrice;
}