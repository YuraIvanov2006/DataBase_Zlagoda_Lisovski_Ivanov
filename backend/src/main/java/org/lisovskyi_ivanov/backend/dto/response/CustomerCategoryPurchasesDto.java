package org.lisovskyi_ivanov.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CustomerCategoryPurchasesDto {
    private String cardNumber;
    private String custSurname;
    private String custName;
    private Integer totalItems;
    private BigDecimal totalSpent;
}
