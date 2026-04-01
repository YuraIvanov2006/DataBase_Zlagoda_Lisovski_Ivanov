package org.lisovskyi_ivanov.backend.repository.account_repos;

import lombok.RequiredArgsConstructor;
import org.lisovskyi_ivanov.backend.entity.Account;
import org.lisovskyi_ivanov.backend.mapping.mapper.AccountRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AccountRepositoryImpl implements AccountRepository {

    private static final String SELECT_ALL =
            """
            SELECT a.id_account, a.id_employee, a.login, a.password, a.authority,
                   e.empl_surname, e.empl_name, e.empl_patronymic, e.empl_role, 
                   e.salary, e.date_of_birth, e.date_of_start, e.empl_phone_number, 
                   e.empl_city, e.empl_street, e.empl_zip_code
            FROM accounts a
            JOIN employees e ON a.id_employee = e.id_employee
            """;

    private final JdbcTemplate jdbc;
    private final NamedParameterJdbcTemplate namedJdbc;
    private final AccountRowMapper accountRowMapper;

    @Override
    public List<Account> findAll() {
        return jdbc.query(SELECT_ALL + " ORDER BY a.id_account", accountRowMapper);
    }

    @Override
    public Optional<Account> findById(Long id) {
        String sql = SELECT_ALL + " WHERE a.id_account = ?";
        return jdbc.query(sql, accountRowMapper, id).stream().findFirst();
    }

    @Override
    public Optional<Account> findByLogin(String login) {
        String sql = SELECT_ALL + " WHERE a.login = ?";
        return jdbc.query(sql, accountRowMapper, login).stream().findFirst();
    }

    @Override
    public Account save(Account account) {
        String sql =
                """
                INSERT INTO accounts (id_employee, login, password, authority)
                VALUES (:id_employee, :login, :password, :authority);
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedJdbc.update(sql, accountParameters(account), keyHolder, new String[] {"id_account"});

        Long generatedId = keyHolder.getKeyAs(Long.class);
        return findById(generatedId).orElseThrow();
    }

    @Override
    public int update(Account account) {
        String sql =
                """
                UPDATE accounts SET
                    id_employee = :id_employee,
                    login = :login,
                    password = :password,
                    authority = :authority
                WHERE id_account = :id_account;
                """;

        return namedJdbc.update(sql, accountParameters(account));
    }

    @Override
    public int deleteById(Long id) {
        String sql = "DELETE FROM accounts WHERE id_account = ?";
        return jdbc.update(sql, id);
    }

    @Override
    public int delete(Account account) {
        return deleteById(account.getIdAccount());
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM accounts WHERE id_account = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    @Override
    public boolean existsByLogin(String login) {
        String sql = "SELECT COUNT(*) FROM accounts WHERE login = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, login);
        return count != null && count > 0;
    }

    private SqlParameterSource accountParameters(Account account) {
        return new MapSqlParameterSource()
                .addValue("id_account", account.getIdAccount())
                .addValue("id_employee", account.getEmployee() != null ? account.getEmployee().getIdEmployee() : null)
                .addValue("login", account.getLogin())
                .addValue("password", account.getPassword())
                .addValue("authority", account.getAuthority() != null ? account.getAuthority().name() : null);
    }
}