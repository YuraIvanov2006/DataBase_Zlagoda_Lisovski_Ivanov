package org.lisovskyi_ivanov.backend.mapping.object;

import org.lisovskyi_ivanov.backend.entity.CustomerCard;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class CustomerCardObject {

    private CustomerCardObject() {
        throw new UnsupportedOperationException("CustomerCardObject is a utility class and cannot be instantiated");
    }

    public static CustomerCard getCustomerCard(ResultSet rs, int rowNum) throws SQLException {
        return CustomerCard.builder()
                .cardNumber(rs.getString("card_number"))
                .custSurname(rs.getString("cust_surname"))
                .custName(rs.getString("cust_name"))
                .custPatronymic(rs.getString("cust_patronymic"))
                .custPhoneNumber(rs.getString("cust_phone_number"))
                .custCity(rs.getString("cust_city"))
                .custStreet(rs.getString("cust_street"))
                .custZipCode(rs.getString("cust_zip_code"))
                .percent(rs.getInt("percent"))
                .build();
    }
}