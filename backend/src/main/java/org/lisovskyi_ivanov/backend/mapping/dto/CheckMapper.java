package org.lisovskyi_ivanov.backend.mapping.dto;

import org.lisovskyi_ivanov.backend.dto.request.CheckRequest;
import org.lisovskyi_ivanov.backend.dto.response.CheckDto;
import org.lisovskyi_ivanov.backend.entity.Check;
import org.lisovskyi_ivanov.backend.entity.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface CheckMapper {
    // Entity → DTO
    @Mapping(target = "employeeName", source = "employee", qualifiedByName = "toEmployeeName")
    @Mapping(target = "employeeId", source = "employee.idEmployee")
    @Mapping(target = "customerCardNumber", source = "customerCard.cardNumber")
    @Mapping(target = "customerDiscountPercent", source = "customerCard.percent")
    CheckDto toDto(Check check);

    // Request → Entity
    @Mapping(target = "employee.idEmployee", source = "idEmployee")
    @Mapping(target = "customerCard.cardNumber", source = "cardNumber")
    @Mapping(target = "printDate", ignore = true)
    @Mapping(target = "sumTotal", ignore = true)
    @Mapping(target = "vat", ignore = true)
    Check toEntity(CheckRequest request);

    @Named("toEmployeeName")
    default String toEmployeeName(Employee employee) {
        if (employee == null || employee.getEmplSurname() == null || employee.getEmplName() == null) {
            return "Невідомий касир";
        }
        return employee.getEmplSurname() + " " + employee.getEmplName();
    }
}