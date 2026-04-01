package org.lisovskyi_ivanov.backend.repository.check_repos;

import lombok.RequiredArgsConstructor;
import org.lisovskyi_ivanov.backend.entity.Check;
import org.lisovskyi_ivanov.backend.mapping.mapper.CheckRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CheckRepositoryImpl implements CheckRepository {
    private static final String SELECT_ALL =
            """
            SELECT checks.check_number, checks.id_employee, checks.card_number, 
                   checks.print_date, checks.sum_total, checks.vat,
                   employees.empl_surname, employees.empl_name, employees.empl_patronymic, 
                   employees.empl_role, employees.salary, employees.date_of_birth, 
                   employees.date_of_start, employees.empl_phone_number, employees.empl_city, 
                   employees.empl_street, employees.empl_zip_code,
                   customer_cards.cust_surname, customer_cards.cust_name, customer_cards.cust_patronymic, 
                   customer_cards.cust_phone_number, customer_cards.cust_city, customer_cards.cust_street, 
                   customer_cards.cust_zip_code, customer_cards.percent
            FROM checks
            LEFT JOIN employees ON checks.id_employee = employees.id_employee
            LEFT JOIN customer_cards ON checks.card_number = customer_cards.card_number
            """;

    private final JdbcTemplate jdbc;
    private final NamedParameterJdbcTemplate namedJdbc;
    private final CheckRowMapper checkRowMapper;

    @Override
    public List<Check> findAll() {
        return jdbc.query(SELECT_ALL, checkRowMapper);
    }

    @Override
    public Optional<Check> findByCheckNumber(String checkNumber) {
        String sql = SELECT_ALL + " WHERE checks.check_number = ?";
        return jdbc.query(sql, checkRowMapper, checkNumber)
                .stream()
                .findFirst();
    }

    @Override
    public List<Check> findByEmployeeId(Long employeeId) {
        String sql = SELECT_ALL + " WHERE checks.id_employee = ?";
        return jdbc.query(sql, checkRowMapper, employeeId);
    }

    @Override
    public List<Check> findByCustomerCardNumber(String cardNumber) {
        String sql = SELECT_ALL + " WHERE checks.card_number = ?";
        return jdbc.query(sql, checkRowMapper, cardNumber);
    }

    @Override
    public List<Check> findByPrintDate(LocalDateTime date) {
        String sql = SELECT_ALL + " WHERE CAST(checks.print_date AS DATE) = CAST(? AS DATE)";
        return jdbc.query(sql, checkRowMapper, date);
    }

    @Override
    public List<Check> findBySumTotalGreaterThan(BigDecimal sumTotal) {
        String sql = SELECT_ALL + " WHERE checks.sum_total > ?";
        return jdbc.query(sql, checkRowMapper, sumTotal);
    }

    @Override
    public List<Check> findByVatGreaterThan(BigDecimal vat) {
        String sql = SELECT_ALL + " WHERE checks.vat > ?";
        return jdbc.query(sql, checkRowMapper, vat);
    }

    @Override
    public boolean existsByCheckNumber(String checkNumber) {
        String sql = "SELECT COUNT(check_number) FROM checks WHERE check_number = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, checkNumber);
        return count != null && count > 0;
    }

    @Override
    public Check save(Check check) {
        String sql =
                """
                INSERT INTO checks (
                    check_number, id_employee, card_number, print_date, sum_total, vat
                )
                VALUES (
                    :check_number, :id_employee, :card_number, :print_date, :sum_total, :vat
                );
                """;

        namedJdbc.update(sql, checkParameters(check));
        return findByCheckNumber(check.getCheckNumber()).orElseThrow();
    }

    @Override
    public int update(Check check) {
        String sql =
                """
                UPDATE checks SET
                    id_employee = :id_employee,
                    card_number = :card_number,
                    print_date = :print_date,
                    sum_total = :sum_total,
                    vat = :vat
                WHERE check_number = :check_number
                """;
        return namedJdbc.update(sql, checkParameters(check));
    }

    @Override
    public int delete(Check check) {
        return deleteByCheckNumber(check.getCheckNumber());
    }

    @Override
    public int deleteByCheckNumber(String checkNumber) {
        String sql = "DELETE FROM checks WHERE check_number = ?";
        return jdbc.update(sql, checkNumber);
    }

    // Ось цей метод був відсутній у вас на скріншоті
    private SqlParameterSource checkParameters(Check check) {
        return new MapSqlParameterSource()
                .addValue("check_number", check.getCheckNumber())
                .addValue("id_employee", check.getEmployee() != null ? check.getEmployee().getIdEmployee() : null)
                .addValue("card_number", check.getCustomerCard() != null ? check.getCustomerCard().getCardNumber() : null)
                .addValue("print_date", check.getPrintDate())
                .addValue("sum_total", check.getSumTotal())
                .addValue("vat", check.getVat());
    }
}