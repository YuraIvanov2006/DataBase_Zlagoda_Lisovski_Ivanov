package org.lisovskyi_ivanov.backend.mapping.dto;

import org.lisovskyi_ivanov.backend.dto.request.CategoryRequest;
import org.lisovskyi_ivanov.backend.dto.response.CategoryDto;
import org.lisovskyi_ivanov.backend.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    // Entity → DTO
    CategoryDto toDto(Category category);

    // Request → Entity
    @Mapping(target = "categoryNumber", ignore = true)
    Category toEntity(CategoryRequest request);
}