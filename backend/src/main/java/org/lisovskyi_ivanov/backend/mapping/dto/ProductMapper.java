package org.lisovskyi_ivanov.backend.mapping.dto;

import org.lisovskyi_ivanov.backend.dto.request.ProductRequest;
import org.lisovskyi_ivanov.backend.dto.response.ProductDto;
import org.lisovskyi_ivanov.backend.entity.Category;
import org.lisovskyi_ivanov.backend.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = {Category.class})
public interface ProductMapper {
    // Entity -> DTO
    @Mapping(target = "categoryNumber", source = "category.categoryNumber")
    @Mapping(target = "categoryName", source = "category.categoryName")
    ProductDto toDto(Product product);

    // Request -> Entity
    @Mapping(target = "idProduct", ignore = true)
    @Mapping(target = "category", expression = "java(Category.builder().categoryNumber(request.categoryNumber()).build())")
    Product toEntity(ProductRequest request);
}
