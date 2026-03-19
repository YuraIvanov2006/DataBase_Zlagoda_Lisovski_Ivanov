package org.lisovskyi_ivanov.backend.entity;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Category {
    private Long categoryNumber;
    private String categoryName;
}
