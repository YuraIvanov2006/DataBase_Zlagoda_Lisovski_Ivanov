package org.lisovskyi_ivanov.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSoldByAllDto {
    private Long idProduct;
    private String productName;
}
