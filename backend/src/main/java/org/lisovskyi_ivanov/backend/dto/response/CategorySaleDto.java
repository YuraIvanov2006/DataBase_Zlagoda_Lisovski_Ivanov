package org.lisovskyi_ivanov.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategorySaleDto {
    private String categoryName;
    private Integer totalAmount;
    private BigDecimal totalSum;
}
