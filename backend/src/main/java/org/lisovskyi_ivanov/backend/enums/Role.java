package org.lisovskyi_ivanov.backend.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum Role {
    MANAGER("manager"),
    CASHIER("cashier");

    private final String roleName;

    Role(String roleName) {
        this.roleName = roleName;
    }

    public static Optional<Role> getRoleFromString(String roleName) {
        return Arrays.stream(Role.values())
                .filter(role -> role.roleName.equalsIgnoreCase(roleName))
                .findFirst();
    }
}
