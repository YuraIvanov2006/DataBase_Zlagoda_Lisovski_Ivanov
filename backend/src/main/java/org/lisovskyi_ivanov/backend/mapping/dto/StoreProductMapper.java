package org.lisovskyi_ivanov.backend.mapping.dto;

import org.lisovskyi_ivanov.backend.dto.request.StoreProductRequest;
import org.lisovskyi_ivanov.backend.dto.response.StoreProductDto;
import org.lisovskyi_ivanov.backend.entity.StoreProduct;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface StoreProductMapper {
    // Entity -> DTO
    @Mapping(target = "baseProductUpc", source = "baseProduct.upc")
    StoreProductDto toDto(StoreProduct storeProduct);

    // Request -> Entity
    @Mapping(target = "product",     ignore = true)
    @Mapping(target = "baseProduct", ignore = true)
    StoreProduct toEntity(StoreProductRequest request);
}