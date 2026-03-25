package org.lisovskyi_ivanov.backend.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum Authority {
    ADMIN("admin"),
    USER("user");

    private final String roleName;

    Authority(String roleName) {
        this.roleName = roleName;
    }

    public static Optional<Authority> getRoleFromString(String roleName) {
        return Arrays.stream(values())
                .filter(authority -> authority.roleName.equalsIgnoreCase(roleName))
                .findFirst();
    }
}
