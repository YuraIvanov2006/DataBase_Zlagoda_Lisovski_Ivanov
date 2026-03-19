package org.lisovskyi_ivanov.backend.entity;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Product {
    private Long idProduct;
    private Category category;
    private String productName;
    private String manufacturer;
    private String characteristics;
}
