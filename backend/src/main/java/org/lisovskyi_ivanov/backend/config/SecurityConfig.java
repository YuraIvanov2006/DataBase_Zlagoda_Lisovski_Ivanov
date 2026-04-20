package org.lisovskyi_ivanov.backend.config;

import java.util.List;
import org.lisovskyi_ivanov.backend.enums.Role;
import org.lisovskyi_ivanov.backend.filter.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(
        HttpSecurity http,
        AuthenticationProvider provider,
        JwtAuthFilter jwtAuthFilter
    ) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(provider)
            .authorizeHttpRequests(auth ->
                auth
                    // ✅ Всі auth ендпоінти відкриті без токена
                    .requestMatchers(
                        "/auth/login",
                        "/auth/register",
                        "/auth/me"
                    )
                    .permitAll()
                    // ── DELETE (тільки менеджер)
                    .requestMatchers(HttpMethod.DELETE, "/employees/**")
                    .hasAuthority(Role.MANAGER.getRoleName())
                    .requestMatchers(HttpMethod.DELETE, "/checks/**")
                    .hasAuthority(Role.MANAGER.getRoleName())
                    .requestMatchers(HttpMethod.DELETE, "/products/**")
                    .hasAuthority(Role.MANAGER.getRoleName())
                    .requestMatchers(HttpMethod.DELETE, "/categories/**")
                    .hasAuthority(Role.MANAGER.getRoleName())
                    .requestMatchers(HttpMethod.DELETE, "/store-products/**")
                    .hasAuthority(Role.MANAGER.getRoleName())
                    .requestMatchers(HttpMethod.DELETE, "/customer-cards/**")
                    .hasAuthority(Role.MANAGER.getRoleName())
                    // ── POST (менеджер, крім чеків)
                    .requestMatchers(HttpMethod.POST, "/employees/**")
                    .hasAuthority(Role.MANAGER.getRoleName())
                    .requestMatchers(HttpMethod.POST, "/products/**")
                    .hasAuthority(Role.MANAGER.getRoleName())
                    .requestMatchers(HttpMethod.POST, "/categories/**")
                    .hasAuthority(Role.MANAGER.getRoleName())
                    .requestMatchers(HttpMethod.POST, "/store-products/**")
                    .hasAuthority(Role.MANAGER.getRoleName())
                    // ── POST карток (менеджер та касир)
                    .requestMatchers(HttpMethod.POST, "/customer-cards/**")
                    .hasAnyAuthority(
                        Role.MANAGER.getRoleName(),
                        Role.CASHIER.getRoleName()
                    )
                    // ── POST чеків (тільки касир)
                    .requestMatchers(HttpMethod.POST, "/checks/**")
                    .hasAuthority(Role.CASHIER.getRoleName())
                    // ── PUT (менеджер для всього, касир тільки картки)
                    .requestMatchers(HttpMethod.PUT, "/customer-cards/**")
                    .hasAnyAuthority(
                        Role.MANAGER.getRoleName(),
                        Role.CASHIER.getRoleName()
                    )
                    .requestMatchers(HttpMethod.PUT, "/**")
                    .hasAuthority(Role.MANAGER.getRoleName())
                    // ── GET (всі авторизовані)
                    .requestMatchers(HttpMethod.GET, "/**")
                    .hasAnyAuthority(
                        Role.MANAGER.getRoleName(),
                        Role.CASHIER.getRoleName()
                    )
                    .anyRequest()
                    .authenticated()
            )
            .addFilterBefore(
                jwtAuthFilter,
                UsernamePasswordAuthenticationFilter.class
            )
            .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(
            List.of("http://localhost:5173", "https://localhost:5173")
        );
        configuration.setAllowedMethods(
            List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")
        );
        configuration.setAllowedHeaders(
            List.of("Authorization", "Content-Type")
        );
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(
        UserDetailsService userDetailsService
    ) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(
            userDetailsService
        );
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}
