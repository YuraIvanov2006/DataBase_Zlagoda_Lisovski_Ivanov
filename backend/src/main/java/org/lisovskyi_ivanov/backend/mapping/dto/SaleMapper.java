package org.lisovskyi_ivanov.backend.mapping.dto;

import org.lisovskyi_ivanov.backend.dto.request.SaleRequest;
import org.lisovskyi_ivanov.backend.dto.response.SaleDto;
import org.lisovskyi_ivanov.backend.entity.Check;
import org.lisovskyi_ivanov.backend.entity.Sale;
import org.lisovskyi_ivanov.backend.entity.StoreProduct;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SaleMapper {
    // Entity -> DTO
    @Mapping(target = "upc",           source = "storeProduct.upc")
    @Mapping(target = "productName",   source = "storeProduct.product.productName")
    @Mapping(target = "checkNumber",   source = "check.checkNumber")
    @Mapping(target = "totalRowPrice", expression = "java(sale.getSellingPrice().multiply(java.math.BigDecimal.valueOf(sale.getProductNumber())))")
    SaleDto toDto(Sale sale);

    // Request -> Entity
    @Mapping(target = "storeProduct", ignore = true)
    @Mapping(target = "check",        ignore = true)
    @Mapping(target = "sellingPrice", ignore = true)
    Sale toEntity(SaleRequest request);
}