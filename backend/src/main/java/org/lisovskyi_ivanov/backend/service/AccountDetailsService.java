package org.lisovskyi_ivanov.backend.service;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.lisovskyi_ivanov.backend.entity.Account;
import org.lisovskyi_ivanov.backend.exception.NotFoundException;
import org.lisovskyi_ivanov.backend.repository.account_repos.AccountRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountDetailsService implements UserDetailsService {
    private final AccountRepository repository;

    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        return repository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("User was not found"));
    }

    public boolean existsByLogin(String login) {
        return repository.findByLogin(login).isPresent();
    }

    public Account findByLogin(String login) {
        if (login == null || login.isBlank()) {
            throw new IllegalArgumentException("Login must not be null or blank");
        }
        return repository.findByLogin(login)
                .orElseThrow(() -> new NotFoundException(Account.class, "login", login));
    }

    public Account save(Account account) {
        return repository.save(account);
    }
}
