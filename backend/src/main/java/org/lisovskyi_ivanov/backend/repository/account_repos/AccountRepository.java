package org.lisovskyi_ivanov.backend.repository.account_repos;

import org.lisovskyi_ivanov.backend.entity.Account;

import java.util.List;
import java.util.Optional;

public interface AccountRepository {
    List<Account> findAll();
    Optional<Account> findById(Long id);
    Optional<Account> findByLogin(String login);
    Account save(Account account);
    int update(Account account);
    int deleteById(Long id);
    int delete(Account account);
    boolean existsById(Long id);
    boolean existsByLogin(String login);
}