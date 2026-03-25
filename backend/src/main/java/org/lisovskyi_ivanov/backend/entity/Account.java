package org.lisovskyi_ivanov.backend.entity;

import lombok.*;
import org.jspecify.annotations.Nullable;
import org.lisovskyi_ivanov.backend.enums.Authority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Account implements UserDetails {
    private Long idAccount;
    private Employee employee;
    private String login;
    private String password;
    @Getter
    private Authority authority;

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public @Nullable String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(employee.getEmplRole().name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
