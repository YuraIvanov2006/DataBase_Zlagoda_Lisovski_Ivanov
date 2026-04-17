package org.lisovskyi_ivanov.backend.mapping.dto;

import org.lisovskyi_ivanov.backend.dto.request.CustomerCardRequest;
import org.lisovskyi_ivanov.backend.dto.response.CustomerCardDto;
import org.lisovskyi_ivanov.backend.entity.CustomerCard;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerCardMapper {
    // Entity → DTO
    CustomerCardDto toDto(CustomerCard card);

    // Request -> Entity
    @Mapping(target = "cardNumber", ignore = true)
    CustomerCard toEntity(CustomerCardRequest request);
}
