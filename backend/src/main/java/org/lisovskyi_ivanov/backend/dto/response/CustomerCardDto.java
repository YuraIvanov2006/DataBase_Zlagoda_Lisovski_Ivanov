package org.lisovskyi_ivanov.backend.dto.response;

public record CustomerCardDto (
    String cardNumber,
     String custSurname,
     String custName,
     String custPatronymic,
     String custPhoneNumber,
     Integer percent
) {}