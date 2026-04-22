package org.lisovskyi_ivanov.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CategoryBoughtByAllDto {
    private Long categoryNumber;
    private String categoryName;
}
