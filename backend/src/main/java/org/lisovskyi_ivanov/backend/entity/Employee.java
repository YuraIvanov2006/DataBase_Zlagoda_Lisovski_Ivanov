package org.lisovskyi_ivanov.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.lisovskyi_ivanov.backend.enums.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Employee implements UserDetails {
    private Long idEmployee;
    private String emplSurname;
    private String emplName;
    private String emplPatronymic;
    private Role emplRole;
    private BigDecimal salary;
    private LocalDate dateOfBirth;
    private LocalDate dateOfStart;
    private String emplPhoneNumber;
    private String emplCity;
    private String emplStreet;
    private String emplZipCode;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public @Nullable String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return "";
    }
}
