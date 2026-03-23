package org.lisovskyi_ivanov.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Product {
    private Long idProduct;
    private Category category;
    private String productName;
    private String manufacturer;
    private String characteristics;
}
