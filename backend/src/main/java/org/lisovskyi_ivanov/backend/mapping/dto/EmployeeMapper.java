package org.lisovskyi_ivanov.backend.mapping.dto;

import org.lisovskyi_ivanov.backend.dto.request.EmployeeRequest;
import org.lisovskyi_ivanov.backend.dto.response.EmployeeDto;
import org.lisovskyi_ivanov.backend.entity.Employee;
import org.lisovskyi_ivanov.backend.enums.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {
    // Entity -> DTO
    @Mapping(target = "fullName", expression = "java(toFullName(employee))")
    @Mapping(target = "emplRole", source = "emplRole", qualifiedByName = "toRoleName")
    EmployeeDto toDto(Employee employee);

    // Request -> Entity
    @Mapping(target = "idEmployee", ignore = true)
    Employee toEntity(EmployeeRequest request);

    @Named("toRoleName")
    default String toRoleName(Role role) {
        return role.getRoleName();
    }

    default String toFullName(Employee employee) {
        if (employee == null) return "";
        String fullName = employee.getEmplSurname() + " " + employee.getEmplName();
        if (employee.getEmplPatronymic() != null) {
            fullName += " " + employee.getEmplPatronymic();
        }
        return fullName;
    }
}
