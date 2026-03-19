package org.lisovskyi_ivanov.backend.entity;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CustomerCard {
    private String cardNumber;
    private String custSurname;
    private String custName;
    private String custPatronymic;
    private String custPhoneNumber;
    private String custCity;
    private String custStreet;
    private String custZipCode;
    private Integer percent;
}