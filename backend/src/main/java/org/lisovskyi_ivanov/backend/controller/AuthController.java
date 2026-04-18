package org.lisovskyi_ivanov.backend.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.lisovskyi_ivanov.backend.entity.Account;
import org.lisovskyi_ivanov.backend.entity.Employee;
import org.lisovskyi_ivanov.backend.service.AccountDetailsService;
import org.lisovskyi_ivanov.backend.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationProvider authenticationProvider;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final AccountDetailsService accountDetailsService;
    private final PasswordEncoder passwordEncoder;

    // record для логіну та паролю
    public record LoginRequest(
            @NotBlank(message = "Логін обов'язковий") String login,
            @NotBlank(message = "Пароль обов'язковий") @Size(min = 6) String password
    ) {}

    // record для реєстрації
    public record RegisterRequest(
            @NotNull Long idEmployee,
            @NotBlank String login,
            @NotBlank @Size(min = 6) String password
    ) {}

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginRequest loginRequest) {
        authenticationProvider.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.login(), loginRequest.password())
        );
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.login());
        String token = jwtService.generateToken(userDetails);
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
        if (accountDetailsService.existsByLogin(request.login())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); // 409 — логін зайнятий
        }

        Account account = Account.builder()
                .employee(Employee.builder().idEmployee(request.idEmployee()).build())
                .login(request.login())
                .password(passwordEncoder.encode(request.password()))
                .build();

        accountDetailsService.save(account);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}

