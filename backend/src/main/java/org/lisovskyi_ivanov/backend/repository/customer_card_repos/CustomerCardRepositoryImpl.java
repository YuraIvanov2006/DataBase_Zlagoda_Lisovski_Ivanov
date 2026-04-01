package org.lisovskyi_ivanov.backend.repository.customer_card_repos;

import lombok.RequiredArgsConstructor;
import org.lisovskyi_ivanov.backend.entity.CustomerCard;
import org.lisovskyi_ivanov.backend.mapping.mapper.CustomerCardRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CustomerCardRepositoryImpl implements CustomerCardRepository {
    private static final String TABLE_NAME = "customer_cards";
    private static final String SELECT_ALL =
            "SELECT card_number, cust_surname, cust_name, cust_patronymic, " +
                    "cust_phone_number, cust_city, cust_street, cust_zip_code, percent " +
                    "FROM " + TABLE_NAME;

    private final JdbcTemplate jdbc;
    private final NamedParameterJdbcTemplate namedJdbc;
    private final CustomerCardRowMapper customerCardRowMapper;

    @Override
    public List<CustomerCard> findAll() {
        return jdbc.query(SELECT_ALL, customerCardRowMapper);
    }

    @Override
    public List<CustomerCard> findAllByCustSurname(String surname) {
        String sql = SELECT_ALL + " WHERE cust_surname = ?";
        return jdbc.query(sql, customerCardRowMapper, surname);
    }

    @Override
    public Optional<CustomerCard> findByCardNumber(String cardNumber) {
        String sql = SELECT_ALL + " WHERE card_number = ?";
        return jdbc.query(sql, customerCardRowMapper, cardNumber)
                .stream()
                .findFirst();
    }

    @Override
    public CustomerCard save(CustomerCard customerCard) {
        String sql =
                """
                INSERT INTO customer_cards (
                    card_number, cust_surname, cust_name, cust_patronymic, 
                    cust_phone_number, cust_city, cust_street, cust_zip_code, percent
                ) VALUES (
                    :card_number, :cust_surname, :cust_name, :cust_patronymic, 
                    :cust_phone_number, :cust_city, :cust_street, :cust_zip_code, :percent
                );
                """;

        namedJdbc.update(sql, customerCardParameters(customerCard));
        return findByCardNumber(customerCard.getCardNumber())
                .orElseThrow(() -> new IllegalStateException(
                        "Failed to save customer card with number: " + customerCard.getCardNumber()));
    }

    @Override
    public int update(CustomerCard customerCard) {
        String sql =
                """
                UPDATE customer_cards SET
                    cust_surname = :cust_surname,
                    cust_name = :cust_name,
                    cust_patronymic = :cust_patronymic,
                    cust_phone_number = :cust_phone_number,
                    cust_city = :cust_city,
                    cust_street = :cust_street,
                    cust_zip_code = :cust_zip_code,
                    percent = :percent
                WHERE card_number = :card_number;
                """;

        return namedJdbc.update(sql, customerCardParameters(customerCard));
    }

    @Override
    public int delete(CustomerCard customerCard) {
        return deleteById(customerCard.getCardNumber());
    }

    @Override
    public int deleteById(String cardNumber) {
        String sql = "DELETE FROM customer_cards WHERE card_number = ?";
        return jdbc.update(sql, cardNumber);
    }

    @Override
    public boolean existsById(String cardNumber) {
        String sql = "SELECT COUNT(card_number) FROM customer_cards WHERE card_number = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, cardNumber);
        return count != null && count > 0;
    }

    private SqlParameterSource customerCardParameters(CustomerCard customerCard) {
        return new MapSqlParameterSource()
                .addValue("card_number", customerCard.getCardNumber())
                .addValue("cust_surname", customerCard.getCustSurname())
                .addValue("cust_name", customerCard.getCustName())
                .addValue("cust_patronymic", customerCard.getCustPatronymic())
                .addValue("cust_phone_number", customerCard.getCustPhoneNumber())
                .addValue("cust_city", customerCard.getCustCity())
                .addValue("cust_street", customerCard.getCustStreet())
                .addValue("cust_zip_code", customerCard.getCustZipCode())
                .addValue("percent", customerCard.getPercent());
    }
}